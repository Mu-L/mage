package mage.cards.c;

import mage.MageObjectReference;
import mage.abilities.Ability;
import mage.abilities.common.CaseAbility;
import mage.abilities.common.EntersBattlefieldTriggeredAbility;
import mage.abilities.condition.Condition;
import mage.abilities.condition.common.SolvedSourceCondition;
import mage.abilities.costs.common.SacrificeSourceCost;
import mage.abilities.common.ActivateIfConditionActivatedAbility;
import mage.abilities.effects.common.DamageTargetEffect;
import mage.abilities.effects.common.ExileTopXMayPlayUntilEffect;
import mage.abilities.hint.common.CaseSolvedHint;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.constants.Duration;
import mage.constants.SubType;
import mage.constants.WatcherScope;
import mage.game.Game;
import mage.game.events.GameEvent;
import mage.target.common.TargetOpponentsCreaturePermanent;
import mage.watchers.Watcher;

import java.util.*;

/**
 * Case of the Burning Masks {1}{R}{R}
 * Enchantment - Case
 * When this Case enters the battlefield, it deals 3 damage to target creature an opponent controls.
 * To solve -- Three or more sources you controlled dealt damage this turn.
 * Solved -- Sacrifice this Case: Exile the top three cards of your library. Choose one of them. You may play that card this turn.
 *
 * @author DominionSpy
 */
public final class CaseOfTheBurningMasks extends CardImpl {

    public CaseOfTheBurningMasks(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.ENCHANTMENT}, "{1}{R}{R}");

        this.subtype.add(SubType.CASE);

        // When this Case enters the battlefield, it deals 3 damage to target creature an opponent controls.
        Ability initialAbility = new EntersBattlefieldTriggeredAbility(new DamageTargetEffect(3));
        initialAbility.addTarget(new TargetOpponentsCreaturePermanent());
        // To solve -- Three or more sources you controlled dealt damage this turn.
        // Solved -- Sacrifice this Case: Exile the top three cards of your library. Choose one of them. You may play that card this turn.
        Ability solvedAbility = new ActivateIfConditionActivatedAbility(
                new ExileTopXMayPlayUntilEffect(3, true, Duration.EndOfTurn),
                new SacrificeSourceCost().setText("sacrifice this Case"), SolvedSourceCondition.SOLVED
        );

        this.addAbility(new CaseAbility(
                initialAbility, CaseOfTheBurningMasksCondition.instance, solvedAbility
        ).addHint(new CaseOfTheBurningMasksHint()), new CaseOfTheBurningMasksWatcher());
    }

    private CaseOfTheBurningMasks(final CaseOfTheBurningMasks card) {
        super(card);
    }

    @Override
    public CaseOfTheBurningMasks copy() {
        return new CaseOfTheBurningMasks(this);
    }
}

enum CaseOfTheBurningMasksCondition implements Condition {
    instance;

    @Override
    public boolean apply(Game game, Ability source) {
        CaseOfTheBurningMasksWatcher watcher = game.getState().getWatcher(CaseOfTheBurningMasksWatcher.class);
        return watcher != null && watcher.damagingCountByController(source.getControllerId()) >= 3;
    }

    @Override
    public String toString() {
        return "Three or more sources you controlled dealt damage this turn";
    }
}

class CaseOfTheBurningMasksHint extends CaseSolvedHint {

    CaseOfTheBurningMasksHint() {
        super(CaseOfTheBurningMasksCondition.instance);
    }

    private CaseOfTheBurningMasksHint(final CaseOfTheBurningMasksHint hint) {
        super(hint);
    }

    @Override
    public CaseOfTheBurningMasksHint copy() {
        return new CaseOfTheBurningMasksHint(this);
    }

    @Override
    public String getConditionText(Game game, Ability ability) {
        int sources = game.getState()
                .getWatcher(CaseOfTheBurningMasksWatcher.class)
                .damagingCountByController(ability.getControllerId());
        return "Sources that dealt damage: " + sources + " (need 3).";
    }
}

class CaseOfTheBurningMasksWatcher extends Watcher {

    private final Map<UUID, Set<MageObjectReference>> damagingObjects;

    CaseOfTheBurningMasksWatcher() {
        super(WatcherScope.GAME);
        this.damagingObjects = new HashMap<>();
    }

    @Override
    public void watch(GameEvent event, Game game) {
        switch (event.getType()) {
            case DAMAGED_PERMANENT:
            case DAMAGED_PLAYER: {
                damagingObjects
                        .computeIfAbsent(game.getControllerId(event.getSourceId()), k -> new HashSet<>())
                        .add(new MageObjectReference(event.getSourceId(), game));
            }
        }
    }

    @Override
    public void reset() {
        super.reset();
        damagingObjects.clear();
    }

    public int damagingCountByController(UUID controllerId) {
        return damagingObjects.getOrDefault(controllerId, Collections.emptySet()).size();
    }
}
