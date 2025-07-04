package mage.cards.b;

import mage.MageInt;
import mage.abilities.Ability;
import mage.abilities.condition.common.CelebrationCondition;
import mage.abilities.effects.common.continuous.BoostTargetEffect;
import mage.abilities.effects.common.continuous.GainAbilityTargetEffect;
import mage.abilities.keyword.MenaceAbility;
import mage.abilities.triggers.BeginningOfCombatTriggeredAbility;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.AbilityWord;
import mage.constants.CardType;
import mage.constants.SubType;
import mage.target.common.TargetControlledCreaturePermanent;
import mage.watchers.common.PermanentsEnteredBattlefieldWatcher;

import java.util.UUID;

/**
 * @author Susucr
 */
public final class BelligerentOfTheBall extends CardImpl {

    public BelligerentOfTheBall(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.CREATURE}, "{2}{R}");

        this.subtype.add(SubType.OGRE);
        this.subtype.add(SubType.WARRIOR);
        this.power = new MageInt(3);
        this.toughness = new MageInt(3);

        // Celebration -- At the beginning of combat on your turn, if two or more nonland permanents entered the battlefield under your control this turn, target creature you control gets +1/+0 and gains menace until end of turn.
        Ability ability = new BeginningOfCombatTriggeredAbility(new BoostTargetEffect(1, 0)
                .setText("target creature you control gets +1/+0")).withInterveningIf(CelebrationCondition.instance);
        ability.addEffect(new GainAbilityTargetEffect(new MenaceAbility(false))
                .setText("and gains menace until end of turn"));
        ability.addTarget(new TargetControlledCreaturePermanent());
        ability.setAbilityWord(AbilityWord.CELEBRATION);
        ability.addHint(CelebrationCondition.getHint());
        this.addAbility(ability, new PermanentsEnteredBattlefieldWatcher());
    }

    private BelligerentOfTheBall(final BelligerentOfTheBall card) {
        super(card);
    }

    @Override
    public BelligerentOfTheBall copy() {
        return new BelligerentOfTheBall(this);
    }
}
