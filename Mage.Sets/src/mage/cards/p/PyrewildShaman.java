package mage.cards.p;

import mage.MageInt;
import mage.abilities.common.OneOrMoreCombatDamagePlayerTriggeredAbility;
import mage.abilities.condition.common.SourceInGraveyardCondition;
import mage.abilities.costs.mana.ManaCostsImpl;
import mage.abilities.effects.common.DoIfCostPaid;
import mage.abilities.effects.common.ReturnToHandSourceEffect;
import mage.abilities.effects.common.continuous.BoostTargetEffect;
import mage.abilities.keyword.BloodrushAbility;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.*;
import mage.filter.StaticFilters;

import java.util.UUID;

public final class PyrewildShaman extends CardImpl {

    public PyrewildShaman(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.CREATURE}, "{2}{R}");
        this.subtype.add(SubType.GOBLIN);
        this.subtype.add(SubType.SHAMAN);

        this.power = new MageInt(3);
        this.toughness = new MageInt(1);

        // Bloodrush — {1}{R}, Discard Pyrewild Shaman: Target attacking creature gets +3/+1 until end of turn.
        this.addAbility(new BloodrushAbility("{1}{R}", new BoostTargetEffect(3, 1, Duration.EndOfTurn)));

        // Whenever one or more creatures you control deal combat damage to a player, if Pyrewild Shaman is in your graveyard, you may pay {3}. If you do, return Pyrewild Shaman to your hand.
        this.addAbility(new OneOrMoreCombatDamagePlayerTriggeredAbility(Zone.GRAVEYARD, new DoIfCostPaid(
                new ReturnToHandSourceEffect().setText("return this card to your hand"),
                new ManaCostsImpl<>("{3}")
        ), StaticFilters.FILTER_PERMANENT_CREATURES, SetTargetPointer.NONE, false)
                .withInterveningIf(SourceInGraveyardCondition.instance));

    }

    private PyrewildShaman(final PyrewildShaman card) {
        super(card);
    }

    @Override
    public PyrewildShaman copy() {
        return new PyrewildShaman(this);
    }

}
