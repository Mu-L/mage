package mage.cards.g;

import mage.abilities.common.SimpleStaticAbility;
import mage.abilities.effects.common.EnterUntappedAllEffect;
import mage.abilities.mana.AnyColorLandsProduceManaAbility;
import mage.abilities.mana.ColorlessManaAbility;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.constants.SubType;
import mage.constants.TargetController;
import mage.filter.FilterPermanent;
import mage.filter.common.FilterControlledPermanent;

import java.util.UUID;

/**
 * @author TheElk801
 */
public final class GondGate extends CardImpl {

    private static final FilterPermanent filter = new FilterControlledPermanent(SubType.GATE, "Gates you control");
    private static final FilterPermanent filter2 = new FilterControlledPermanent(SubType.GATE, "Gate");

    public GondGate(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.LAND}, "");

        this.subtype.add(SubType.GATE);

        // Gates you control enter the battlefield untapped.
        this.addAbility(new SimpleStaticAbility(new EnterUntappedAllEffect(filter)));

        // {T}: Add {C}.
        this.addAbility(new ColorlessManaAbility());

        // {T}: Add one mana of any color that a Gate you control could produce.
        this.addAbility(new AnyColorLandsProduceManaAbility(TargetController.YOU, true, filter2));
    }

    private GondGate(final GondGate card) {
        super(card);
    }

    @Override
    public GondGate copy() {
        return new GondGate(this);
    }
}
