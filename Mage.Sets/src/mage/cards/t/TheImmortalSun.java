package mage.cards.t;

import mage.MageObject;
import mage.abilities.Ability;
import mage.abilities.LoyaltyAbility;
import mage.abilities.triggers.BeginningOfDrawTriggeredAbility;
import mage.abilities.common.SimpleStaticAbility;
import mage.abilities.effects.ContinuousRuleModifyingEffectImpl;
import mage.abilities.effects.common.DrawCardSourceControllerEffect;
import mage.abilities.effects.common.continuous.BoostControlledEffect;
import mage.abilities.effects.common.cost.SpellsCostReductionControllerEffect;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.*;
import mage.filter.FilterCard;
import mage.game.Game;
import mage.game.events.GameEvent;
import mage.game.permanent.Permanent;

import java.util.Optional;
import java.util.UUID;

/**
 * @author LevelX2
 */
public final class TheImmortalSun extends CardImpl {

    public TheImmortalSun(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.ARTIFACT}, "{6}");

        this.supertype.add(SuperType.LEGENDARY);

        // Players can't activate planeswalkers' loyalty abilities.
        this.addAbility(new SimpleStaticAbility(new TheImmortalSunCantActivateEffect()));

        // At the beginning of your draw step, draw an additional card.
        this.addAbility(new BeginningOfDrawTriggeredAbility(new DrawCardSourceControllerEffect(1)
                .setText("draw an additional card"), false));

        // Spells you cast cost {1} less to cast.
        this.addAbility(new SimpleStaticAbility(new SpellsCostReductionControllerEffect(new FilterCard("Spells"), 1)));

        // Creatures you control get +1/+1.
        this.addAbility(new SimpleStaticAbility(new BoostControlledEffect(1, 1, Duration.WhileOnBattlefield)));
    }

    private TheImmortalSun(final TheImmortalSun card) {
        super(card);
    }

    @Override
    public TheImmortalSun copy() {
        return new TheImmortalSun(this);
    }
}

class TheImmortalSunCantActivateEffect extends ContinuousRuleModifyingEffectImpl {

    TheImmortalSunCantActivateEffect() {
        super(Duration.WhileOnBattlefield, Outcome.Detriment);
        staticText = "Players can't activate planeswalkers' loyalty abilities";
    }

    private TheImmortalSunCantActivateEffect(final TheImmortalSunCantActivateEffect effect) {
        super(effect);
    }

    @Override
    public TheImmortalSunCantActivateEffect copy() {
        return new TheImmortalSunCantActivateEffect(this);
    }

    @Override
    public String getInfoMessage(Ability source, GameEvent event, Game game) {
        MageObject mageObject = game.getObject(source);
        if (mageObject != null) {
            return "You can't activate loyalty abilities of planeswalkers (" + mageObject.getIdName() + ").";
        }
        return null;
    }

    @Override
    public boolean checksEventType(GameEvent event, Game game) {
        return event.getType() == GameEvent.EventType.ACTIVATE_ABILITY;
    }

    @Override
    public boolean applies(GameEvent event, Ability source, Game game) {
        Permanent permanent = game.getPermanentOrLKIBattlefield(event.getSourceId());
        if (permanent == null) {
            return false;
        }
        if (permanent.isPlaneswalker(game)) {
            Optional<Ability> ability = game.getAbility(event.getTargetId(), event.getSourceId());
            return ability.isPresent() && (ability.get() instanceof LoyaltyAbility);
        }
        return false;
    }
}
