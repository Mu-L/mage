package mage.cards.t;

import mage.abilities.Ability;
import mage.abilities.LoyaltyAbility;
import mage.abilities.common.SimpleStaticAbility;
import mage.abilities.effects.common.ExileTargetEffect;
import mage.abilities.effects.common.PreventAllNonCombatDamageToAllEffect;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.constants.ComparisonType;
import mage.constants.Duration;
import mage.constants.SuperType;
import mage.filter.FilterPermanent;
import mage.filter.StaticFilters;
import mage.filter.common.FilterCreaturePermanent;
import mage.filter.predicate.mageobject.PowerPredicate;
import mage.target.TargetPermanent;

import java.util.UUID;

/**
 * @author TheElk801
 */
public final class TheWanderer extends CardImpl {

    private static final FilterPermanent filter
            = new FilterCreaturePermanent("creature with power 4 or greater");

    static {
        filter.add(new PowerPredicate(ComparisonType.MORE_THAN, 3));
    }

    public TheWanderer(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.PLANESWALKER}, "{3}{W}");

        this.supertype.add(SuperType.LEGENDARY);
        this.setStartingLoyalty(5);

        // Prevent all noncombat damage that would be dealt to you and other permanents you control.
        this.addAbility(new SimpleStaticAbility(new PreventAllNonCombatDamageToAllEffect(
                Duration.WhileOnBattlefield, StaticFilters.FILTER_OTHER_CONTROLLED_PERMANENTS, true
        )));

        // -2: Exile target creature with power 4 or greater.
        Ability ability = new LoyaltyAbility(new ExileTargetEffect(), -2);
        ability.addTarget(new TargetPermanent(filter));
        this.addAbility(ability);
    }

    private TheWanderer(final TheWanderer card) {
        super(card);
    }

    @Override
    public TheWanderer copy() {
        return new TheWanderer(this);
    }
}
