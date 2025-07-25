
package mage.cards.a;

import mage.MageInt;
import mage.abilities.Ability;
import mage.abilities.common.SimpleActivatedAbility;
import mage.abilities.costs.common.TapSourceCost;
import mage.abilities.costs.mana.ManaCostsImpl;
import mage.abilities.dynamicvalue.common.GetXValue;
import mage.abilities.effects.common.DamageAllEffect;
import mage.abilities.effects.common.DamageTargetEffect;
import mage.abilities.keyword.ChannelAbility;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.constants.SubType;
import mage.constants.SuperType;
import mage.filter.StaticFilters;
import mage.target.TargetPermanent;

import java.util.UUID;

/**
 * @author LevelX2
 */
public final class ArashiTheSkyAsunder extends CardImpl {

    public ArashiTheSkyAsunder(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.CREATURE}, "{3}{G}{G}");
        this.supertype.add(SuperType.LEGENDARY);
        this.subtype.add(SubType.SPIRIT);

        this.power = new MageInt(5);
        this.toughness = new MageInt(5);

        // {X}{G}, {tap}: Arashi, the Sky Asunder deals X damage to target creature with flying.
        Ability ability = new SimpleActivatedAbility(new DamageTargetEffect(GetXValue.instance), new ManaCostsImpl<>("{X}{G}"));
        ability.addCost(new TapSourceCost());
        ability.addTarget(new TargetPermanent(StaticFilters.FILTER_CREATURE_FLYING));
        this.addAbility(ability);

        // Channel - {X}{G}{G}, Discard Arashi: Arashi deals X damage to each creature with flying.
        this.addAbility(new ChannelAbility("{X}{G}{G}", new DamageAllEffect(GetXValue.instance, StaticFilters.FILTER_CREATURE_FLYING)));
    }

    private ArashiTheSkyAsunder(final ArashiTheSkyAsunder card) {
        super(card);
    }

    @Override
    public ArashiTheSkyAsunder copy() {
        return new ArashiTheSkyAsunder(this);
    }
}
