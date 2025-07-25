package mage.cards.c;

import mage.MageInt;
import mage.abilities.Ability;
import mage.abilities.common.SimpleActivatedAbility;
import mage.abilities.condition.common.ThresholdCondition;
import mage.abilities.costs.common.TapSourceCost;
import mage.abilities.costs.mana.ManaCostsImpl;
import mage.abilities.common.ActivateIfConditionActivatedAbility;
import mage.abilities.effects.common.DamageTargetEffect;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.AbilityWord;
import mage.constants.CardType;
import mage.constants.SubType;
import mage.target.common.TargetAnyTarget;

import java.util.UUID;

/**
 * @author LevelX2
 */
public final class Chainflinger extends CardImpl {

    public Chainflinger(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.CREATURE}, "{3}{R}");
        this.subtype.add(SubType.BEAST);

        this.power = new MageInt(2);
        this.toughness = new MageInt(2);

        // {1}{R}, {tap}: Chainflinger deals 1 damage to any target.
        Ability ability = new SimpleActivatedAbility(new DamageTargetEffect(1), new ManaCostsImpl<>("{1}{R}"));
        ability.addCost(new TapSourceCost());
        ability.addTarget(new TargetAnyTarget());
        this.addAbility(ability);

        // Threshold - {2}{R}, {tap}: Chainflinger deals 2 damage to any target. Activate this ability only if seven or more cards are in your graveyard.
        Ability thresholdAbility = new ActivateIfConditionActivatedAbility(
                new DamageTargetEffect(2), new ManaCostsImpl<>("{2}{R}"), ThresholdCondition.instance
        );
        thresholdAbility.addCost(new TapSourceCost());
        thresholdAbility.addTarget(new TargetAnyTarget());
        thresholdAbility.setAbilityWord(AbilityWord.THRESHOLD);
        this.addAbility(thresholdAbility);
    }

    private Chainflinger(final Chainflinger card) {
        super(card);
    }

    @Override
    public Chainflinger copy() {
        return new Chainflinger(this);
    }
}
