
package mage.cards.g;

import java.util.UUID;
import mage.MageInt;
import mage.abilities.Ability;
import mage.abilities.common.AttacksTriggeredAbility;
import mage.abilities.costs.mana.ManaCostsImpl;
import mage.abilities.effects.common.DoIfCostPaid;
import mage.abilities.effects.common.continuous.GainAbilityTargetEffect;
import mage.abilities.keyword.DevoidAbility;
import mage.abilities.keyword.FlyingAbility;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.constants.SubType;
import mage.constants.Duration;
import mage.filter.StaticFilters;
import mage.target.TargetPermanent;
import mage.target.common.TargetCreaturePermanent;

import static mage.filter.StaticFilters.FILTER_ANOTHER_TARGET_CREATURE;

/**
 *
 * @author fireshoes
 */
public final class GravityNegator extends CardImpl {

    public GravityNegator(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId,setInfo,new CardType[]{CardType.CREATURE},"{3}{U}");
        this.subtype.add(SubType.ELDRAZI);
        this.subtype.add(SubType.DRONE);
        this.power = new MageInt(2);
        this.toughness = new MageInt(3);

        // Devoid
        this.addAbility(new DevoidAbility(this.color));

        // Flying
        this.addAbility(FlyingAbility.getInstance());

        // Whenenever Gravity Negator attacks, you may pay {C}. If you do, another target creature gains flying until end of turn.
        Ability ability = new AttacksTriggeredAbility(new DoIfCostPaid(new GainAbilityTargetEffect(FlyingAbility.getInstance(), Duration.EndOfTurn), new ManaCostsImpl<>("{C}")), false,
                "Whenever {this} attacks, you may pay {C}. If you do, another target creature gains flying until end of turn.");
        ability.addTarget(new TargetPermanent(FILTER_ANOTHER_TARGET_CREATURE));
        this.addAbility(ability);
    }

    private GravityNegator(final GravityNegator card) {
        super(card);
    }

    @Override
    public GravityNegator copy() {
        return new GravityNegator(this);
    }
}
