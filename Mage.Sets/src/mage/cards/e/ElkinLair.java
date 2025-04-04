package mage.cards.e;

import java.util.Set;
import java.util.UUID;
import mage.abilities.Ability;
import mage.abilities.DelayedTriggeredAbility;
import mage.abilities.triggers.BeginningOfUpkeepTriggeredAbility;
import mage.abilities.common.delayed.AtTheBeginOfNextEndStepDelayedTriggeredAbility;
import mage.abilities.effects.ContinuousEffect;
import mage.abilities.effects.OneShotEffect;
import mage.abilities.effects.common.asthought.PlayFromNotOwnHandZoneTargetEffect;
import mage.cards.Card;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.constants.Duration;
import mage.constants.Outcome;
import mage.constants.SuperType;
import mage.constants.TargetController;
import mage.constants.Zone;
import mage.filter.FilterCard;
import mage.game.Game;
import mage.game.permanent.Permanent;
import mage.players.Player;
import mage.target.targetpointer.FixedTarget;
import mage.util.RandomUtil;

/**
 *
 * @author L_J
 */
public final class ElkinLair extends CardImpl {

    public ElkinLair(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.ENCHANTMENT}, "{3}{R}");
        this.supertype.add(SuperType.WORLD);

        // At the beginning of each player's upkeep, that player exiles a card at random from their hand.
        // The player may play that card this turn.
        // At the beginning of the next end step, if the player hasn't played the card, they put it into their graveyard.
        this.addAbility(new BeginningOfUpkeepTriggeredAbility(TargetController.EACH_PLAYER, new ElkinLairUpkeepEffect(), false));

    }

    private ElkinLair(final ElkinLair card) {
        super(card);
    }

    @Override
    public ElkinLair copy() {
        return new ElkinLair(this);
    }

}

class ElkinLairUpkeepEffect extends OneShotEffect {

    ElkinLairUpkeepEffect() {
        super(Outcome.Benefit);
        this.staticText = "that player exiles a card at random from their hand. "
                + "The player may play that card this turn. "
                + "At the beginning of the next end step, if the "
                + "player hasn't played the card, they put it into their graveyard";
    }

    private ElkinLairUpkeepEffect(final ElkinLairUpkeepEffect effect) {
        super(effect);
    }

    @Override
    public ElkinLairUpkeepEffect copy() {
        return new ElkinLairUpkeepEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Player player = game.getPlayer(game.getActivePlayerId());
        Permanent sourcePermanent = game.getPermanent(source.getSourceId());
        if (player == null || sourcePermanent == null) {
            return false;
        }

        Card[] cards = player.getHand().getCards(new FilterCard(), game).toArray(new Card[0]);
        if (cards.length == 0) {
            return false;
        }

        Card card = cards[RandomUtil.nextInt(cards.length)];
        if (card == null) {
            return false;
        }

        String exileName = sourcePermanent.getIdName() + " <this card may be played the turn it was exiled";
        player.moveCardsToExile(card, source, game, true, source.getSourceId(), exileName);
        if (game.getState().getZone(card.getId()) != Zone.EXILED) {
            return false;
        }

        ContinuousEffect effect = new PlayFromNotOwnHandZoneTargetEffect(Zone.EXILED, TargetController.OWNER, Duration.EndOfTurn);
        effect.setTargetPointer(new FixedTarget(card, game));
        game.addEffect(effect, source);
        DelayedTriggeredAbility delayed = new AtTheBeginOfNextEndStepDelayedTriggeredAbility(new ElkinLairPutIntoGraveyardEffect());
        game.addDelayedTriggeredAbility(delayed, source);
        return true;
    }
}

class ElkinLairPutIntoGraveyardEffect extends OneShotEffect {

    ElkinLairPutIntoGraveyardEffect() {
        super(Outcome.Neutral);
        staticText = "if the player hasn't played the card, they put it into their graveyard";
    }

    private ElkinLairPutIntoGraveyardEffect(final ElkinLairPutIntoGraveyardEffect effect) {
        super(effect);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Player player = game.getPlayer(game.getActivePlayerId());
        if (player == null) {
            return false;
        }

        Set<Card> cardsInExile = game.getExile().getExileZone(source.getSourceId()).getCards(game);
        if (cardsInExile == null) {
            return false;
        }

        player.moveCardsToGraveyardWithInfo(cardsInExile, source, game, Zone.EXILED);
        return true;
    }

    @Override
    public ElkinLairPutIntoGraveyardEffect copy() {
        return new ElkinLairPutIntoGraveyardEffect(this);
    }
}
