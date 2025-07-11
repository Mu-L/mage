package mage.cards.r;

import mage.MageInt;
import mage.abilities.effects.common.ReturnToHandChosenControlledPermanentEffect;
import mage.abilities.triggers.BeginningOfUpkeepTriggeredAbility;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.constants.SubType;
import mage.filter.StaticFilters;

import java.util.UUID;

/**
 * @author North
 */
public final class RoaringPrimadox extends CardImpl {

    public RoaringPrimadox(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.CREATURE}, "{3}{G}");
        this.subtype.add(SubType.BEAST);

        this.power = new MageInt(4);
        this.toughness = new MageInt(4);

        // At the beginning of your upkeep, return a creature you control to its owner's hand.
        this.addAbility(new BeginningOfUpkeepTriggeredAbility(
                new ReturnToHandChosenControlledPermanentEffect(StaticFilters.FILTER_CONTROLLED_CREATURE)
        ));
    }

    private RoaringPrimadox(final RoaringPrimadox card) {
        super(card);
    }

    @Override
    public RoaringPrimadox copy() {
        return new RoaringPrimadox(this);
    }
}
