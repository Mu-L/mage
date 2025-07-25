package mage.client.remote;

import mage.cards.decks.Deck;
import mage.client.MageFrame;
import mage.client.SessionHandler;
import mage.client.chat.ChatPanelBasic;
import mage.client.constants.Constants.DeckEditorMode;
import mage.client.dialog.PreferencesDialog;
import mage.client.draft.DraftPanel;
import mage.client.game.GamePanel;
import mage.client.plugins.impl.Plugins;
import mage.client.util.DeckUtil;
import mage.client.util.GUISizeHelper;
import mage.client.util.IgnoreList;
import mage.client.util.audio.AudioManager;
import mage.client.util.object.SaveObjectUtil;
import mage.interfaces.callback.CallbackClient;
import mage.interfaces.callback.ClientCallback;
import mage.interfaces.callback.ClientCallbackType;
import mage.remote.ActionData;
import mage.remote.Session;
import mage.util.DebugUtil;
import mage.view.*;
import mage.view.ChatMessage.MessageType;
import org.apache.log4j.Logger;
import org.mage.card.arcane.ManaSymbols;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.*;

/**
 * Client side implementation (process commands from a server)
 *
 * @author BetaSteward_at_googlemail.com, JayDi85
 */
public class CallbackClientImpl implements CallbackClient {

    private static final Logger logger = Logger.getLogger(CallbackClientImpl.class);

    private final MageFrame frame;
    private final Map<ClientCallbackType, Integer> lastMessages;
    private final Map<UUID, GameClientMessage> firstGameData;

    public CallbackClientImpl(MageFrame frame) {
        this.frame = frame;
        this.lastMessages = new HashMap<>();
        this.firstGameData = new HashMap<>();
        Arrays.stream(ClientCallbackType.values()).forEach(t -> this.lastMessages.put(t, 0));
    }

    @Override
    public void onNewConnection() {
        // must clean temp data for each new connection
        this.lastMessages.clear();
        this.firstGameData.clear();
    }

    @Override
    public synchronized void onCallback(final ClientCallback callback) {
        callback.decompressData();

        // put replay related code here
        SaveObjectUtil.saveObject(callback.getData(), callback.getMethod().toString());

        // reconnect fix with miss data, part 1 of 2
        // on reconnect many events can come in diff order, so init GameView data with first available info
        // game_start event can come after game view - must save it here (part 1) before real use in swing thread (part 2)
        if (callback.getData() instanceof GameClientMessage) {
            firstGameData.putIfAbsent(callback.getObjectId(), (GameClientMessage) callback.getData());
        }

        // all GUI related code must be executed in swing thread
        SwingUtilities.invokeLater(() -> {
            try {
                if (DebugUtil.NETWORK_SHOW_CLIENT_CALLBACK_MESSAGES_LOG) {
                    logger.info(callback.getInfo());
                }

                // process bad connection (events can income in wrong order, so outdated data must be ignored)
                // - table/dialog events like game start, game end, choose dialog - must be processed anyway
                // - messages events like chat, inform, error - must be processed anyway
                // - update events like opponent priority - can be ignored
                if (!callback.getMethod().getType().equals(ClientCallbackType.CLIENT_SIDE_EVENT)) {
                    int lastAnyMessageId = this.lastMessages.values().stream().mapToInt(x -> x).max().orElse(0);
                    if (lastAnyMessageId > callback.getMessageId()) {
                        // un-synced message
                        if (callback.getMethod().getType().mustIgnoreOnOutdated()) {
                            // ignore
                            logger.warn(String.format("ignore un-synced message %d - %s - %s, possible reason: slow connection/performance",
                                    callback.getMessageId(),
                                    callback.getMethod().getType(),
                                    callback.getMethod()
                            ));
                            return;
                        } else {
                            // process it anyway
                            logger.debug(String.format("processing un-synced message %d - %s - %s, possible reason: slow connection/performance",
                                    callback.getMessageId(),
                                    callback.getMethod().getType(),
                                    callback.getMethod()
                            ));
                        }
                    }

                    // keep track of synced messages only
                    if (!callback.getMethod().getType().canComeInAnyOrder()) {
                        this.lastMessages.put(callback.getMethod().getType(), callback.getMessageId());
                    }
                }

                switch (callback.getMethod()) {

                    case START_GAME: {
                        TableClientMessage message = (TableClientMessage) callback.getData();
                        gameStarted(callback.getMessageId(), message.getCurrentTableId(), message.getParentTableId(), message.getGameId(), message.getPlayerId());

                        // reconnect fix with miss data, part 2 of 2
                        // START_GAME event can come after GAME_INIT or any other, so must force update with first info
                        // START_GAME even raises before a real game start, so it hasn't GameView in payload
                        GamePanel gamePanel = MageFrame.getGame(callback.getObjectId());
                        if (gamePanel != null && gamePanel.isMissGameData()) {
                            GameClientMessage mes = firstGameData.getOrDefault(callback.getObjectId(), null);
                            if (mes != null) {
                                logger.warn("Found miss game data, requesting latest info... (possible reason: reconnect)");
                                gamePanel.init(callback.getMessageId(), mes.getGameView(), true);
                                // ask server to resent latest data
                                // TODO: replace by special async request like requestGameUpdate
                                SessionHandler.sendPlayerUUID(callback.getObjectId(), UUID.randomUUID());
                            } else {
                                // it's ok, new game come here
                                // logger.error("Found miss game data, but can't find any usefull info (report to developers)");
                            }
                        }
                        break;
                    }

                    case START_TOURNAMENT: {
                        TableClientMessage message = (TableClientMessage) callback.getData();
                        tournamentStarted(callback.getMessageId(), callback.getObjectId(), message.getCurrentTableId(), message.getPlayerId());
                        break;
                    }

                    case REPLAY_GAME: {
                        replayGame(callback.getObjectId());
                        break;
                    }

                    case SHOW_TOURNAMENT: {
                        TableClientMessage message = (TableClientMessage) callback.getData();
                        showTournament(message.getCurrentTableId(), callback.getObjectId());
                        break;
                    }

                    case WATCHGAME: {
                        TableClientMessage message = (TableClientMessage) callback.getData();
                        watchGame(message.getCurrentTableId(), message.getParentTableId(), callback.getObjectId());
                        break;
                    }

                    case CHATMESSAGE: {
                        ChatMessage message = (ChatMessage) callback.getData();
                        // Drop messages from ignored users
                        if (message.getUsername() != null && IgnoreList.IGNORED_MESSAGE_TYPES.contains(message.getMessageType())) {
                            final String serverAddress = SessionHandler.getSession().getServerHost();
                            if (IgnoreList.userIsIgnored(serverAddress, message.getUsername())) {
                                break;
                            }
                        }

                        ChatPanelBasic panel = MageFrame.getChat(callback.getObjectId());
                        if (panel != null) {
                            // play the sound related to the message
                            if (message.getSoundToPlay() != null) {
                                switch (message.getSoundToPlay()) {
                                    case PlayerLeft:
                                        AudioManager.playPlayerLeft();
                                        break;
                                    case PlayerQuitTournament:
                                        AudioManager.playPlayerQuitTournament();
                                        break;
                                    case PlayerSubmittedDeck:
                                        AudioManager.playPlayerSubmittedDeck();
                                        break;
                                    case PlayerWhispered:
                                        AudioManager.playPlayerWhispered();
                                        break;
                                }
                            }
                            // send start message to chat if not done yet
                            if (!panel.isStartMessageDone()) {
                                createChatStartMessage(panel);
                            }
                            // send the message to subchat if exists and it's not a game message
                            if (message.getMessageType() != MessageType.GAME && panel.getConnectedChat() != null) {
                                panel.getConnectedChat().receiveMessage(message.getUsername(), message.getMessage(), message.getTime(), message.getTurnInfo(), message.getMessageType(), ChatMessage.MessageColor.BLACK);
                            } else {
                                panel.receiveMessage(message.getUsername(), message.getMessage(), message.getTime(), message.getTurnInfo(), message.getMessageType(), message.getColor());
                            }

                        }
                        break;
                    }

                    case SERVER_MESSAGE: {
                        if (callback.getData() != null) {
                            ChatMessage message = (ChatMessage) callback.getData();
                            showMessageDialog(null, message.getMessage(), "Server message");
                        }
                        break;
                    }

                    case JOINED_TABLE: {
                        TableClientMessage message = (TableClientMessage) callback.getData();
                        joinedTable(message.getRoomId(), message.getCurrentTableId(), message.getFlag());
                        break;
                    }

                    case REPLAY_INIT: {
                        GamePanel panel = MageFrame.getGame(callback.getObjectId());
                        if (panel != null) {
                            panel.init(callback.getMessageId(), (GameView) callback.getData(), true);
                        }
                        break;
                    }

                    case REPLAY_DONE: {
                        GamePanel panel = MageFrame.getGame(callback.getObjectId());
                        if (panel != null) {
                            panel.endMessage(callback.getMessageId(), null, null, (String) callback.getData());
                        }
                        break;
                    }

                    case REPLAY_UPDATE: {
                        GamePanel panel = MageFrame.getGame(callback.getObjectId());
                        if (panel != null) {
                            panel.updateGame(callback.getMessageId(), (GameView) callback.getData());
                        }
                        break;
                    }

                    case GAME_INIT: {
                        GamePanel panel = MageFrame.getGame(callback.getObjectId());
                        if (panel != null) {
                            appendJsonEvent("GAME_INIT", callback.getObjectId(), callback.getData());
                            panel.init(callback.getMessageId(), (GameView) callback.getData(), true);
                        }
                        break;
                    }

                    case GAME_OVER: {
                        GameClientMessage message = (GameClientMessage) callback.getData();
                        GamePanel panel = MageFrame.getGame(callback.getObjectId());
                        if (panel != null) {
                            Session session = SessionHandler.getSession();
                            if (session.isJsonLogActive()) {
                                appendJsonEvent("GAME_OVER", callback.getObjectId(), message);
                            }
                            panel.endMessage(callback.getMessageId(), message.getGameView(), message.getOptions(), message.getMessage());
                        }
                        break;
                    }

                    case GAME_ERROR: {
                        frame.showErrorDialog("SERVER", "game error", (String) callback.getData());
                        break;
                    }

                    case GAME_ASK: {
                        GameClientMessage message = (GameClientMessage) callback.getData();
                        GamePanel panel = MageFrame.getGame(callback.getObjectId());
                        if (panel != null) {
                            appendJsonEvent("GAME_ASK", callback.getObjectId(), message);
                            panel.ask(callback.getMessageId(), message.getGameView(), message.getMessage(), message.getOptions());
                        }
                        break;
                    }

                    case GAME_TARGET: {
                        // e.g. Pick triggered ability
                        GameClientMessage message = (GameClientMessage) callback.getData();
                        GamePanel panel = MageFrame.getGame(callback.getObjectId());
                        if (panel != null) {
                            appendJsonEvent("GAME_TARGET", callback.getObjectId(), message);
                            panel.pickTarget(callback.getMessageId(), message.getGameView(), message.getOptions(), message.getMessage(),
                                    message.getCardsView1(), message.getTargets(), message.isFlag());
                        }
                        break;
                    }

                    case GAME_SELECT: {
                        GameClientMessage message = (GameClientMessage) callback.getData();
                        GamePanel panel = MageFrame.getGame(callback.getObjectId());
                        if (panel != null) {
                            appendJsonEvent("GAME_SELECT", callback.getObjectId(), message);
                            panel.select(callback.getMessageId(), message.getGameView(), message.getOptions(), message.getMessage());
                        }
                        break;
                    }

                    case GAME_CHOOSE_ABILITY: {
                        AbilityPickerView abilityPickerView = (AbilityPickerView) callback.getData();
                        GamePanel panel = MageFrame.getGame(callback.getObjectId());
                        if (panel != null) {
                            appendJsonEvent("GAME_CHOOSE_ABILITY", callback.getObjectId(), callback.getData());
                            panel.pickAbility(callback.getMessageId(), abilityPickerView.getGameView(), null, abilityPickerView);
                        }
                        break;
                    }

                    case GAME_CHOOSE_PILE: {
                        GameClientMessage message = (GameClientMessage) callback.getData();
                        GamePanel panel = MageFrame.getGame(callback.getObjectId());
                        if (panel != null) {
                            appendJsonEvent("GAME_CHOOSE_PILE", callback.getObjectId(), message);
                            panel.pickPile(callback.getMessageId(), message.getGameView(), message.getOptions(), message.getMessage(), message.getCardsView1(), message.getCardsView2());
                        }
                        break;
                    }

                    case GAME_CHOOSE_CHOICE: {
                        GameClientMessage message = (GameClientMessage) callback.getData();
                        GamePanel panel = MageFrame.getGame(callback.getObjectId());
                        if (panel != null) {
                            appendJsonEvent("GAME_CHOOSE_CHOICE", callback.getObjectId(), message);
                            panel.getChoice(callback.getMessageId(), message.getGameView(), message.getOptions(), message.getChoice(), callback.getObjectId());
                        }
                        break;
                    }

                    case GAME_PLAY_MANA: {
                        GameClientMessage message = (GameClientMessage) callback.getData();
                        GamePanel panel = MageFrame.getGame(callback.getObjectId());
                        if (panel != null) {
                            appendJsonEvent("GAME_PLAY_MANA", callback.getObjectId(), message);
                            panel.playMana(callback.getMessageId(), message.getGameView(), message.getOptions(), message.getMessage());
                        }
                        break;
                    }

                    case GAME_PLAY_XMANA: {
                        GameClientMessage message = (GameClientMessage) callback.getData();
                        GamePanel panel = MageFrame.getGame(callback.getObjectId());
                        if (panel != null) {
                            appendJsonEvent("GAME_PLAY_XMANA", callback.getObjectId(), message);
                            panel.playXMana(callback.getMessageId(), message.getGameView(), message.getOptions(), message.getMessage());
                        }
                        break;
                    }

                    case GAME_GET_AMOUNT: {
                        GameClientMessage message = (GameClientMessage) callback.getData();
                        GamePanel panel = MageFrame.getGame(callback.getObjectId());
                        if (panel != null) {
                            appendJsonEvent("GAME_GET_AMOUNT", callback.getObjectId(), message);
                            panel.getAmount(callback.getMessageId(), message.getGameView(), message.getOptions(), message.getMin(), message.getMax(), message.getMessage());
                        }
                        break;
                    }

                    case GAME_GET_MULTI_AMOUNT: {
                        GameClientMessage message = (GameClientMessage) callback.getData();
                        GamePanel panel = MageFrame.getGame(callback.getObjectId());
                        if (panel != null) {
                            appendJsonEvent("GAME_GET_MULTI_AMOUNT", callback.getObjectId(), message);
                            panel.getMultiAmount(callback.getMessageId(), message.getGameView(), message.getMessages(), message.getOptions(), message.getMin(), message.getMax());
                        }
                        break;
                    }

                    case GAME_UPDATE: {
                        GamePanel panel = MageFrame.getGame(callback.getObjectId());
                        if (panel != null) {
                            appendJsonEvent("GAME_UPDATE", callback.getObjectId(), callback.getData());
                            panel.updateGame(callback.getMessageId(), (GameView) callback.getData(), true, null, null); // update after undo wtf?! // TODO: clean dialogs?!
                        }
                        break;
                    }

                    case GAME_REDRAW_GUI: {
                        // re-draw game's gui elements like attack arrows
                        // uses for client side only (example: update after scrollbars support)
                        GamePanel panel = MageFrame.getGame(callback.getObjectId());
                        if (panel != null) {
                            panel.updateGame();
                        }
                        break;
                    }

                    case END_GAME_INFO: {
                        MageFrame.getInstance().showGameEndDialog((GameEndView) callback.getData());
                        break;
                    }

                    case SHOW_USERMESSAGE: {
                        List<String> messageData = (List<String>) callback.getData();
                        if (messageData.size() == 2) {
                            showMessageDialog(null, messageData.get(1), messageData.get(0));
                        }
                        break;
                    }

                    case GAME_UPDATE_AND_INFORM: {
                        GameClientMessage message = (GameClientMessage) callback.getData();
                        GamePanel panel = MageFrame.getGame(callback.getObjectId());
                        if (panel != null) {
                            appendJsonEvent("GAME_INFORM", callback.getObjectId(), message);
                            panel.inform(callback.getMessageId(), message.getGameView(), message.getMessage());
                        }
                        break;
                    }

                    case GAME_INFORM_PERSONAL: {
                        GameClientMessage message = (GameClientMessage) callback.getData();
                        GamePanel panel = MageFrame.getGame(callback.getObjectId());
                        if (panel != null) {
                            showMessageDialog(panel, message.getMessage(), "Game message");
                        }
                        break;
                    }

                    case SIDEBOARD: {
                        TableClientMessage message = (TableClientMessage) callback.getData();
                        DeckView deckView = message.getDeck();
                        Deck deck = DeckUtil.construct(deckView);
                        if (message.getFlag()) {
                            construct_sideboard(deck, message.getCurrentTableId(), message.getParentTableId(), message.getTime());
                        } else {
                            sideboard(deck, message.getCurrentTableId(), message.getParentTableId(), message.getTime());
                        }
                        break;
                    }

                    case VIEW_LIMITED_DECK: {
                        TableClientMessage message = (TableClientMessage) callback.getData();
                        DeckView deckView = message.getDeck();
                        Deck deck = DeckUtil.construct(deckView);
                        viewLimitedDeck(deck, message.getCurrentTableId(), message.getParentTableId(), message.getTime());
                        break;
                    }

                    case VIEW_SIDEBOARD: {
                        TableClientMessage message = (TableClientMessage) callback.getData();
                        viewSideboard(message.getGameId(), message.getPlayerId());
                        break;
                    }

                    case CONSTRUCT: {
                        TableClientMessage message = (TableClientMessage) callback.getData();
                        DeckView deckView = message.getDeck();
                        Deck deck = DeckUtil.construct(deckView);
                        construct(deck, message.getCurrentTableId(), message.getParentTableId(), message.getTime());
                        break;
                    }

                    case START_DRAFT: {
                        TableClientMessage message = (TableClientMessage) callback.getData();
                        draftStarted(callback.getMessageId(), message.getCurrentTableId(), callback.getObjectId(), message.getPlayerId());
                        break;
                    }

                    case DRAFT_OVER: {
                        MageFrame.removeDraft(callback.getObjectId());
                        break;
                    }

                    case DRAFT_INIT:
                    case DRAFT_PICK: {
                        DraftClientMessage message = (DraftClientMessage) callback.getData();
                        DraftPanel panel = MageFrame.getDraft(callback.getObjectId());
                        if (panel != null) {
                            panel.loadBooster(message.getDraftPickView());
                        }
                        break;
                    }

                    case DRAFT_UPDATE: {
                        DraftPanel panel = MageFrame.getDraft(callback.getObjectId());
                        DraftClientMessage message = (DraftClientMessage) callback.getData();
                        if (panel != null) {
                            panel.updateDraft(message.getDraftView());
                        }
                        break;
                    }

                    case TOURNAMENT_INIT: {
                        break;
                    }

                    case USER_REQUEST_DIALOG: {
                        frame.showUserRequestDialog((UserRequestMessage) callback.getData());
                        break;
                    }

                    default: {
                        // TODO: add exception here and process miss events like TOURNAMENT_UPDATE
                        break;
                    }
                }
            } catch (Exception ex) {
                handleException(ex);
            }
        });
    }

    /**
     * Show modal message box, so try to use it only for critical errors or global message. As less as possible.
     */
    private void showMessageDialog(Component parentComponent, String message, String title) {
        // convert to html
        // message - supported
        // title - not supported
        message = ManaSymbols.replaceSymbolsWithHTML(message, ManaSymbols.Type.DIALOG);
        message = GUISizeHelper.textToHtmlWithSize(message, GUISizeHelper.dialogFont);
        JOptionPane.showMessageDialog(parentComponent, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    private ActionData appendJsonEvent(String name, UUID gameId, Object value) {
        Session session = SessionHandler.getSession();
        if (session.isJsonLogActive()) {
            ActionData actionData = new ActionData(name, gameId);
            actionData.value = value;
            session.appendJsonLog(actionData);
            return actionData;
        }
        return null;
    }

    private void createChatStartMessage(ChatPanelBasic chatPanel) {
        chatPanel.setStartMessageDone(true);
        ChatPanelBasic usedPanel = chatPanel;
        if (chatPanel.getConnectedChat() != null) {
            usedPanel = chatPanel.getConnectedChat();
        }
        switch (usedPanel.getChatType()) {
            case GAME:
                usedPanel.receiveMessage("", new StringBuilder()
                                .append("HOTKEYS:")
                                .append("<br/>Turn mousewheel up (ALT-e) - enlarge image of card the mousepointer hovers over")
                                .append("<br/>Turn mousewheel down (ALT-s) - enlarge original/alternate image of card the mousepointer hovers over")
                                .append("<br/><b>")
                                .append(KeyEvent.getKeyText(PreferencesDialog.getCurrentControlKey(PreferencesDialog.KEY_CONTROL_CONFIRM)))
                                .append("</b> - Confirm \"Ok\", \"Yes\" or \"Done\" button")
                                .append("<br/><b>")
                                .append(KeyEvent.getKeyText(PreferencesDialog.getCurrentControlKey(PreferencesDialog.KEY_CONTROL_NEXT_TURN)))
                                .append("</b> - Skip current turn but stop on declare attackers/blockers and something on the stack")
                                .append("<br/><b>")
                                .append(KeyEvent.getKeyText(PreferencesDialog.getCurrentControlKey(PreferencesDialog.KEY_CONTROL_END_STEP)))
                                .append("</b> - Skip to next end step but stop on declare attackers/blockers and something on the stack")
                                .append("<br/><b>")
                                .append(KeyEvent.getKeyText(PreferencesDialog.getCurrentControlKey(PreferencesDialog.KEY_CONTROL_SKIP_STEP)))
                                .append("</b> - Skip current turn but stop on declare attackers/blockers")
                                .append("<br/><b>")
                                .append(KeyEvent.getKeyText(PreferencesDialog.getCurrentControlKey(PreferencesDialog.KEY_CONTROL_MAIN_STEP)))
                                .append("</b> - Skip to next main phase but stop on declare attackers/blockers and something on the stack")
                                .append("<br/><b>")
                                .append(KeyEvent.getKeyText(PreferencesDialog.getCurrentControlKey(PreferencesDialog.KEY_CONTROL_YOUR_TURN)))
                                .append("</b> - Skip everything until your next turn")
                                .append("<br/><b>")
                                .append(KeyEvent.getKeyText(PreferencesDialog.getCurrentControlKey(PreferencesDialog.KEY_CONTROL_PRIOR_END)))
                                .append("</b> - Skip everything until the end step just prior to your turn")
                                .append("<br/><b>")
                                .append(KeyEvent.getKeyText(PreferencesDialog.getCurrentControlKey(PreferencesDialog.KEY_CONTROL_CANCEL_SKIP)))
                                .append("</b> - Undo F4/F5/F7/F9/F11")
                                .append("<br/><b>")
                                .append(KeyEvent.getKeyText(PreferencesDialog.getCurrentControlKey(PreferencesDialog.KEY_CONTROL_SWITCH_CHAT)))
                                .append("</b> - Switch in/out to chat text field")
                                .append("<br/><b>ALT + D</b> - show/hide panel with big card from the right side")
                                .append("<br/><b>Hold ALT + E</b> - force to show card popup in image mode")
                                /*
                                        .append("<br/><b>")
                                        .append(KeyEvent.getKeyText(PreferencesDialog.getCurrentControlKey(PreferencesDialog.KEY_CONTROL_TOGGLE_MACRO)))
                                        .append("</b> - Toggle recording a sequence of actions to repeat. Will not pause if interrupted and can fail if a selected card changes such as when scrying top card to bottom.")
                                        .append("<br/><b>").append(System.getProperty("os.name").contains("Mac OS X") ? "Cmd" : "Ctrl").append(" + click</b> - Hold priority while casting a spell or activating an ability")
                                 */
                                .append("<br/>")
                                .append("<br/>")
                                .append("CHAT COMMANDS:")
                                .append("<br/>").append("<b>/h username </b> - show player's stats (history)")
                                .append("<br/>").append("<b>/w username message</b> - send private message to player (whisper)")
                                .append("<br/>").append("<b>/pings</b> - show players and watchers ping")
                                .append("<br/>").append("<b>/fix</b> - fix frozen game")
                                .append("<br/>").append("<b>[[card name]]</b> - insert card with popup info")
                                .toString(),
                        null, null, MessageType.USER_INFO, ChatMessage.MessageColor.BLUE);
                break;
            case TOURNAMENT:
                usedPanel.receiveMessage("", "On this panel you can see the players, their state and the results of the games of the tournament. Also you can chat with the competitors of the tournament.",
                        null, null, MessageType.USER_INFO, ChatMessage.MessageColor.BLUE);
                break;
            case TABLES:
                String serverAddress = SessionHandler.getSession().getServerHost();
                usedPanel.receiveMessage("", new StringBuilder("Download card images by using the \"Images\" main menu.")
                                .append("<br/>Download icons and symbols by using the \"Symbols\" main menu.")
                                .append("<br/>\\list - show a list of available chat commands.")
                                .append("<br/>").append(IgnoreList.usage(serverAddress))
                                .append("<br/>Type <font color=green>\\w yourUserName profanity 0 (or 1 or 2)</font> to turn off/on the profanity filter").toString(),
                        null, null, MessageType.USER_INFO, ChatMessage.MessageColor.BLUE);
                break;
            default:
                break;

        }
    }

    private void joinedTable(UUID roomId, UUID tableId, boolean isTournament) {
        try {
            frame.showTableWaitingDialog(roomId, tableId, isTournament);
        } catch (Exception ex) {
            handleException(ex);
        }
    }

    protected void gameStarted(final int messageId, final UUID currentTableId, final UUID parentTableId, final UUID gameId, final UUID playerId) {
        try {
            logger.info("Game " + gameId + " started for player " + playerId);
            frame.showGame(currentTableId, parentTableId, gameId, playerId);
        } catch (Exception ex) {
            handleException(ex);
        }

        if (Plugins.instance.isCounterPluginLoaded()) {
            Plugins.instance.addGamesPlayed();
        }
    }

    protected void draftStarted(int messageId, UUID tableId, UUID draftId, UUID playerId) {
        try {
            logger.info("Draft " + draftId + " started for player " + playerId);
            frame.showDraft(tableId, draftId);
        } catch (Exception ex) {
            handleException(ex);
        }
    }

    protected void tournamentStarted(int messageId, UUID tournamentId, UUID tableId, UUID playerId) {
        try {
            logger.info("Tournament " + tournamentId + " started for player " + playerId);
            frame.showTournament(tableId, tournamentId);
            AudioManager.playTournamentStarted();
        } catch (Exception ex) {
            handleException(ex);
        }
    }

    /**
     * Shows the tournament info panel for a tournament
     */
    protected void showTournament(UUID tableId, UUID tournamentId) {
        try {
            logger.info("Showing tournament " + tournamentId);
            frame.showTournament(tableId, tournamentId);
        } catch (Exception ex) {
            handleException(ex);
        }
    }

    protected void watchGame(UUID currentTableId, UUID parentTableId, UUID gameId) {
        try {
            logger.info("Watching game " + gameId);
            frame.watchGame(currentTableId, parentTableId, gameId);
        } catch (Exception ex) {
            handleException(ex);
        }
    }

    protected void replayGame(UUID gameId) {
        try {
            logger.info("Replaying game " + gameId);
            frame.replayGame(gameId);
        } catch (Exception ex) {
            handleException(ex);
        }
    }

    protected void sideboard(Deck deck, UUID currentTableId, UUID parentTableId, int time) {
        frame.showDeckEditor(DeckEditorMode.SIDEBOARDING, deck, currentTableId, parentTableId, time);
    }

    protected void construct(Deck deck, UUID currentTableId, UUID parentTableId, int time) {
        frame.showDeckEditor(DeckEditorMode.LIMITED_BUILDING, deck, currentTableId, parentTableId, time);
    }

    protected void construct_sideboard(Deck deck, UUID currentTableId, UUID parentTableId, int time) {
        frame.showDeckEditor(DeckEditorMode.LIMITED_SIDEBOARD_BUILDING, deck, currentTableId, parentTableId, time);
    }

    protected void viewLimitedDeck(Deck deck, UUID currentTableId, UUID parentTableId, int time) {
        frame.showDeckEditor(DeckEditorMode.VIEW_LIMITED_DECK, deck, currentTableId, parentTableId, time);
    }

    protected void viewSideboard(UUID gameId, UUID playerId) {
        SwingUtilities.invokeLater(() -> {
            GamePanel panel = MageFrame.getGame(gameId);
            if (panel != null) {
                panel.openSideboardWindow(playerId);
            }
        });
    }

    private void handleException(Exception e) {
        logger.fatal("General error\n", e);
        frame.showErrorDialog("General error", e);
    }
}
