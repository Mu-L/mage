package mage.abilities.effects.common;

import mage.abilities.Ability;
import mage.abilities.dynamicvalue.DynamicValue;
import mage.abilities.dynamicvalue.common.StaticValue;
import mage.abilities.effects.OneShotEffect;
import mage.constants.Outcome;
import mage.filter.FilterPermanent;
import mage.game.Game;
import mage.game.permanent.Permanent;
import mage.players.Player;
import mage.target.common.TargetSacrifice;
import mage.util.CardUtil;

import java.util.*;

/**
 * @author BetaSteward_at_googlemail.com, JayDi85
 */
public class SacrificeAllEffect extends OneShotEffect {

    private final DynamicValue amount;
    private final FilterPermanent filter;
    private final boolean onlyOpponents;

    private static final String VALUE_KEY = "sacrificeAllEffect_permanentsList";

    /**
     * Each player sacrifices a permanent
     *
     * @param filter can be generic, will automatically add article and necessary sacrifice predicates
     */
    public SacrificeAllEffect(FilterPermanent filter) {
        this(1, filter);
    }

    /**
     * Each player sacrifices N permanents
     *
     * @param filter can be generic, will automatically add necessary sacrifice predicates
     */
    public SacrificeAllEffect(int amount, FilterPermanent filter) {
        this(StaticValue.get(amount), filter);
    }

    /**
     * Each player sacrifices X permanents
     *
     * @param filter can be generic, will automatically add necessary sacrifice predicates
     */
    public SacrificeAllEffect(DynamicValue amount, FilterPermanent filter) {
        this(amount, filter, false);
    }

    protected SacrificeAllEffect(DynamicValue amount, FilterPermanent filter, boolean onlyOpponents) {
        super(Outcome.Sacrifice);
        this.amount = amount;
        this.filter = filter;
        this.onlyOpponents = onlyOpponents;
        setText();
    }

    protected SacrificeAllEffect(final SacrificeAllEffect effect) {
        super(effect);
        this.amount = effect.amount;
        this.filter = effect.filter.copy();
        this.onlyOpponents = effect.onlyOpponents;
    }

    @Override
    public SacrificeAllEffect copy() {
        return new SacrificeAllEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        int num = amount.calculate(game, source, this);
        if (num < 1) {
            return false;
        }
        Set<UUID> perms = new HashSet<>();
        for (UUID playerId : onlyOpponents ?
                game.getOpponents(source.getControllerId()) :
                game.getState().getPlayersInRange(source.getControllerId(), game)) {
            Player player = game.getPlayer(playerId);
            if (player == null) {
                continue;
            }
            int numTargets = Math.min(num, game.getBattlefield().count(TargetSacrifice.makeFilter(filter), player.getId(), source, game));
            if (numTargets < 1) {
                continue;
            }
            TargetSacrifice target = new TargetSacrifice(numTargets, filter);
            target.choose(Outcome.Sacrifice, player.getId(), source, game);
            perms.addAll(target.getTargets());
        }

        List<Permanent> sacraficedPermanents = new ArrayList<>();
        for (UUID permID : perms) {
            Permanent permanent = game.getPermanent(permID);
            if (permanent != null && permanent.sacrifice(source, game)) {
                sacraficedPermanents.add(permanent.copy());
            }
        }
        saveSacrificedPermanentsList(source.getSourceId(), game, sacraficedPermanents);

        return true;
    }

    public static void saveSacrificedPermanentsList(UUID sourceObjectId, Game game, List<Permanent> list) {
        game.getState().setValue(CardUtil.getCardZoneString(VALUE_KEY, sourceObjectId, game), list);
    }

    /**
     * Get detailed list of sacrificed permanents
     *
     * @param previous if you need to look in detailed list on battlefield, then use previous param to find data from a stack moment
     */
    public static List<Permanent> getSacrificedPermanentsList(UUID sourceObjectId, Game game, boolean previous) {
        return (List<Permanent>) game.getState().getValue(CardUtil.getCardZoneString(VALUE_KEY, sourceObjectId, game, previous));
    }

    private void setText() {
        StringBuilder sb = new StringBuilder();
        sb.append(onlyOpponents ? "each opponent sacrifices " : "each player sacrifices ");
        switch (amount.toString()) {
            case "X":
                sb.append(amount.toString());
                sb.append(' ');
                sb.append(filter.getMessage());
                break;
            case "1":
                sb.append(CardUtil.addArticle(filter.getMessage()));
                break;
            default:
                sb.append(CardUtil.numberToText(amount.toString(), "a"));
                sb.append(' ');
                sb.append(filter.getMessage());
        }
        if (!filter.getMessage().contains("with")) {
            sb.append(" of their choice");
        }
        staticText = sb.toString();
    }

}
