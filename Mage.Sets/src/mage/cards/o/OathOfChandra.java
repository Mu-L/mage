package mage.cards.o;

import mage.abilities.Ability;
import mage.abilities.common.EntersBattlefieldTriggeredAbility;
import mage.abilities.condition.Condition;
import mage.abilities.dynamicvalue.common.StaticValue;
import mage.abilities.effects.common.DamagePlayersEffect;
import mage.abilities.effects.common.DamageTargetEffect;
import mage.abilities.triggers.BeginningOfEndStepTriggeredAbility;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.*;
import mage.filter.StaticFilters;
import mage.game.Game;
import mage.game.events.GameEvent;
import mage.game.events.ZoneChangeEvent;
import mage.target.TargetPermanent;
import mage.target.common.TargetCreaturePermanent;
import mage.watchers.Watcher;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static mage.filter.StaticFilters.FILTER_OPPONENTS_PERMANENT_CREATURE;

/**
 * @author LevelX2
 */
public final class OathOfChandra extends CardImpl {

    public OathOfChandra(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.ENCHANTMENT}, "{1}{R}");
        this.supertype.add(SuperType.LEGENDARY);

        // When Oath of Chandra enters the battlefield, it deals 3 damage to target creature an opponent controls.
        Ability ability = new EntersBattlefieldTriggeredAbility(new DamageTargetEffect(3, "it"));
        ability.addTarget(new TargetPermanent(FILTER_OPPONENTS_PERMANENT_CREATURE));
        this.addAbility(ability);

        // At the beginning of each end step, if a planeswalker entered the battlefield under your control this turn, Oath of Chandra deals 2 damage to each opponent.
        this.addAbility(new BeginningOfEndStepTriggeredAbility(
                TargetController.ANY,
                new DamagePlayersEffect(Outcome.Damage, StaticValue.get(2), TargetController.OPPONENT),
                false, OathOfChandraCondition.instance
        ), new OathOfChandraWatcher());
    }

    private OathOfChandra(final OathOfChandra card) {
        super(card);
    }

    @Override
    public OathOfChandra copy() {
        return new OathOfChandra(this);
    }
}

enum OathOfChandraCondition implements Condition {
    instance;

    @Override
    public boolean apply(Game game, Ability source) {
        OathOfChandraWatcher watcher = game.getState().getWatcher(OathOfChandraWatcher.class);
        return watcher != null && watcher.enteredPlaneswalkerForPlayer(source.getControllerId());
    }

    @Override
    public String toString() {
        return "a planeswalker entered the battlefield under your control this turn";
    }

}

class OathOfChandraWatcher extends Watcher {

    private final Set<UUID> players = new HashSet<>();

    public OathOfChandraWatcher() {
        super(WatcherScope.GAME);
    }

    @Override
    public void watch(GameEvent event, Game game) {
        if (event.getType() == GameEvent.EventType.ZONE_CHANGE) {
            ZoneChangeEvent zEvent = (ZoneChangeEvent) event;
            if (zEvent.getToZone() == Zone.BATTLEFIELD
                    && zEvent.getTarget().isPlaneswalker(game)) {
                players.add(zEvent.getTarget().getControllerId());
            }
        }
    }

    @Override
    public void reset() {
        players.clear();
    }

    public boolean enteredPlaneswalkerForPlayer(UUID playerId) {
        return players.contains(playerId);
    }
}
