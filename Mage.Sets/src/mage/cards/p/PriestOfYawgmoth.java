
package mage.cards.p;

import mage.MageInt;
import mage.Mana;
import mage.abilities.Ability;
import mage.abilities.costs.common.SacrificeTargetCost;
import mage.abilities.costs.common.TapSourceCost;
import mage.abilities.dynamicvalue.common.GreatestAmongPermanentsValue;
import mage.abilities.dynamicvalue.common.SacrificeCostManaValue;
import mage.abilities.mana.DynamicManaAbility;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.constants.SubType;
import mage.filter.StaticFilters;

import java.util.UUID;

/**
 * @author LoneFox
 */
public final class PriestOfYawgmoth extends CardImpl {

    public PriestOfYawgmoth(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.CREATURE}, "{1}{B}");
        this.subtype.add(SubType.PHYREXIAN);
        this.subtype.add(SubType.HUMAN);
        this.subtype.add(SubType.CLERIC);
        this.power = new MageInt(1);
        this.toughness = new MageInt(2);

        // {T}, Sacrifice an artifact: Add an amount of {B} equal to the sacrificed artifact's converted mana cost.     
        Ability ability = new DynamicManaAbility(Mana.BlackMana(1), SacrificeCostManaValue.ARTIFACT,
                new TapSourceCost(),
                "add an amount of {B} equal to the sacrificed artifact's mana value",
                false,
                GreatestAmongPermanentsValue.MANAVALUE_OTHER_CONTROLLED_ARTIFACTS
        );
        ability.addCost(new SacrificeTargetCost(StaticFilters.FILTER_CONTROLLED_PERMANENT_ARTIFACT));
        this.addAbility(ability);
    }

    private PriestOfYawgmoth(final PriestOfYawgmoth card) {
        super(card);
    }

    @Override
    public PriestOfYawgmoth copy() {
        return new PriestOfYawgmoth(this);
    }
}
