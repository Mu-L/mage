
package mage.cards.a;

import mage.MageInt;
import mage.abilities.Ability;
import mage.abilities.common.SimpleStaticAbility;
import mage.abilities.effects.common.continuous.BoostAllEffect;
import mage.abilities.effects.common.continuous.GainControlTargetEffect;
import mage.abilities.triggers.BeginningOfEndStepTriggeredAbility;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.*;
import mage.filter.common.FilterCreaturePermanent;
import mage.filter.common.FilterNonlandPermanent;
import mage.filter.predicate.Predicate;
import mage.game.Game;
import mage.game.events.DamagedPlayerEvent;
import mage.game.events.GameEvent;
import mage.game.permanent.Permanent;
import mage.target.TargetPermanent;
import mage.watchers.Watcher;

import java.util.*;

/**
 * @author TheElk801
 */
public final class AdmiralBeckettBrass extends CardImpl {

    private static final FilterCreaturePermanent filter = new FilterCreaturePermanent("Pirates you control");
    private static final FilterNonlandPermanent filter2 = new FilterNonlandPermanent("nonland permanent controlled by a player who was dealt combat damage by three or more Pirates this turn");

    static {
        filter.add(SubType.PIRATE.getPredicate());
        filter.add(TargetController.YOU.getControllerPredicate());
        filter2.add(ControllerDealtDamageByPiratesPredicate.instance);
    }

    public AdmiralBeckettBrass(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.CREATURE}, "{1}{U}{B}{R}");

        this.supertype.add(SuperType.LEGENDARY);
        this.subtype.add(SubType.HUMAN);
        this.subtype.add(SubType.PIRATE);
        this.power = new MageInt(3);
        this.toughness = new MageInt(3);

        // Other Pirates you control get +1/+1.
        this.addAbility(new SimpleStaticAbility(new BoostAllEffect(1, 1, Duration.WhileOnBattlefield, filter, true)));

        // At the beginning of your end step, gain control of target nonland permanent controlled by a player who was dealt combat damage by three or more Pirates this turn.
        Ability ability = new BeginningOfEndStepTriggeredAbility(new GainControlTargetEffect(Duration.Custom, true));
        ability.addTarget(new TargetPermanent(filter2));
        this.addAbility(ability, new DamagedByPiratesWatcher());
    }

    private AdmiralBeckettBrass(final AdmiralBeckettBrass card) {
        super(card);
    }

    @Override
    public AdmiralBeckettBrass copy() {
        return new AdmiralBeckettBrass(this);
    }
}

class DamagedByPiratesWatcher extends Watcher {

    private final Map<UUID, Set<UUID>> damageSourceIds = new HashMap<>();

    public DamagedByPiratesWatcher() {
        super(WatcherScope.GAME);
    }

    @Override
    public void watch(GameEvent event, Game game) {
        if (event.getType() == GameEvent.EventType.DAMAGED_PLAYER) {
            if (((DamagedPlayerEvent) event).isCombatDamage()) {
                Permanent creature = game.getPermanentOrLKIBattlefield(event.getSourceId());
                if (creature != null && creature.hasSubtype(SubType.PIRATE, game)) {
                    if (damageSourceIds.containsKey(event.getTargetId())) {
                        damageSourceIds.get(event.getTargetId()).add(creature.getId());
                    } else {
                        Set<UUID> creatureSet = new HashSet<>();
                        creatureSet.add(creature.getId());
                        damageSourceIds.put(event.getTargetId(), creatureSet);
                    }
                }
            }
        }
    }

    public boolean damagedByEnoughPirates(UUID sourceId) {
        return damageSourceIds.containsKey(sourceId) && damageSourceIds.get(sourceId).size() > 2;
    }

    @Override
    public void reset() {
        super.reset();
        damageSourceIds.clear();
    }
}

enum ControllerDealtDamageByPiratesPredicate implements Predicate<Permanent> {
    instance;

    @Override
    public boolean apply(Permanent input, Game game) {
        DamagedByPiratesWatcher watcher = game.getState().getWatcher(DamagedByPiratesWatcher.class);
        if (watcher != null) {
            return watcher.damagedByEnoughPirates(input.getControllerId());
        }
        return false;
    }
}
