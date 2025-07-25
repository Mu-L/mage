package mage.abilities.effects.common.continuous;

import mage.abilities.Ability;
import mage.abilities.ActivatedAbility;
import mage.abilities.common.SimpleActivatedAbility;
import mage.abilities.costs.Cost;
import mage.abilities.costs.Costs;
import mage.abilities.costs.CostsImpl;
import mage.abilities.costs.UseAttachedCost;
import mage.abilities.effects.ContinuousEffectImpl;
import mage.abilities.effects.Effect;
import mage.abilities.effects.Effects;
import mage.constants.Duration;
import mage.constants.Layer;
import mage.constants.Outcome;
import mage.constants.SubLayer;
import mage.game.Game;
import mage.game.permanent.Permanent;
import mage.target.Target;
import mage.target.Targets;
import mage.target.targetpointer.FixedTarget;

import java.util.Collections;
import java.util.function.Consumer;

/**
 * @author TheElk801
 */
public class GainAbilityWithAttachmentEffect extends ContinuousEffectImpl {

    private final Effects effects = new Effects();
    private final Targets targets = new Targets();
    private final Costs<Cost> costs = new CostsImpl<>();
    protected final UseAttachedCost useAttachedCost;
    private final Consumer<ActivatedAbility> consumer;

    public GainAbilityWithAttachmentEffect(String rule, Effect effect, Target target, UseAttachedCost attachedCost, Cost... costs) {
        this(rule, new Effects(effect), new Targets(target), attachedCost, costs);
    }

    public GainAbilityWithAttachmentEffect(String rule, Effects effects, Targets targets, UseAttachedCost attachedCost, Cost... costs) {
        this(rule, effects, targets, attachedCost, null, costs);
    }

    public GainAbilityWithAttachmentEffect(String rule, Effects effects, Targets targets, UseAttachedCost attachedCost, Consumer<ActivatedAbility> consumer, Cost... costs) {
        super(Duration.WhileOnBattlefield, Layer.AbilityAddingRemovingEffects_6, SubLayer.NA, Outcome.AddAbility);
        this.staticText = rule;
        this.effects.addAll(effects);
        this.targets.addAll(targets);
        Collections.addAll(this.costs, costs);
        this.useAttachedCost = attachedCost;
        this.consumer = consumer;
        this.generateGainAbilityDependencies(makeAbility(null, null), null);
    }

    protected GainAbilityWithAttachmentEffect(final GainAbilityWithAttachmentEffect effect) {
        super(effect);
        this.effects.addAll(effect.effects);
        this.targets.addAll(effect.targets);
        this.costs.addAll(effect.costs);
        this.useAttachedCost = effect.useAttachedCost == null ? null : effect.useAttachedCost.copy();
        this.consumer = effect.consumer;
    }

    @Override
    public GainAbilityWithAttachmentEffect copy() {
        return new GainAbilityWithAttachmentEffect(this);
    }

    @Override
    public void init(Ability source, Game game) {
        if (getAffectedObjectsSetAtInit(source)) {
            Permanent equipment = game.getPermanentOrLKIBattlefield(source.getSourceId());
            if (equipment != null && equipment.getAttachedTo() != null) {
                this.setTargetPointer(new FixedTarget(equipment.getAttachedTo(), game.getState().getZoneChangeCounter(equipment.getAttachedTo())));
            }
        }
        super.init(source, game); // must call at the end due target pointer setup
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Permanent permanent = null;
        if (getAffectedObjectsSet()) {
            permanent = game.getPermanent(getTargetPointer().getFirst(game, source));
            if (permanent == null) {
                discard();
                return true;
            }
        } else {
            Permanent equipment = game.getPermanent(source.getSourceId());
            if (equipment != null && equipment.getAttachedTo() != null) {
                permanent = game.getPermanentOrLKIBattlefield(equipment.getAttachedTo());
            }
        }
        if (permanent == null) {
            return true;
        }
        Ability ability = makeAbility(game, source);
        ability.getEffects().setValue("attachedPermanent", game.getPermanent(source.getSourceId()));
        permanent.addAbility(ability, source.getSourceId(), game);
        return true;
    }

    protected Ability makeAbility(Game game, Ability source) {
        ActivatedAbility ability = new SimpleActivatedAbility(null, null);
        for (Effect effect : effects) {
            if (effect == null) {
                continue;
            }
            ability.addEffect(effect.copy());
        }
        for (Target target : targets) {
            if (target == null) {
                continue;
            }
            ability.addTarget(target);
        }
        for (Cost cost : this.costs) {
            if (cost == null) {
                continue;
            }
            ability.addCost(cost.copy());
        }
        if (source != null && game != null) {
            ability.addCost(useAttachedCost.copy().setMageObjectReference(source, game));
        }
        if (consumer != null) {
            consumer.accept(ability);
        }
        return ability;
    }
}
