package mage.cards.p;

import mage.MageInt;
import mage.ObjectColor;
import mage.abilities.Ability;
import mage.abilities.common.SimpleActivatedAbility;
import mage.abilities.common.SimpleStaticAbility;
import mage.abilities.costs.common.TapSourceCost;
import mage.abilities.costs.mana.ManaCostsImpl;
import mage.abilities.effects.common.continuous.BoostControlledEffect;
import mage.abilities.effects.common.continuous.GainAbilityTargetEffect;
import mage.abilities.keyword.FlyingAbility;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.constants.Duration;
import mage.constants.SubType;
import mage.filter.FilterPermanent;
import mage.filter.common.FilterControlledCreaturePermanent;
import mage.filter.common.FilterCreaturePermanent;
import mage.filter.predicate.mageobject.AnotherPredicate;
import mage.filter.predicate.mageobject.ColorPredicate;
import mage.target.TargetPermanent;

import java.util.UUID;

/**
 * @author LevelX2
 */
public final class ParagonOfGatheringMists extends CardImpl {

    private static final FilterPermanent filterBlue = new FilterCreaturePermanent("blue creatures");
    private static final FilterControlledCreaturePermanent filterBlue2 = new FilterControlledCreaturePermanent("another target blue creature you control");

    static {
        filterBlue.add(new ColorPredicate(ObjectColor.BLUE));
        filterBlue2.add(new ColorPredicate(ObjectColor.BLUE));
        filterBlue2.add(AnotherPredicate.instance);
    }

    public ParagonOfGatheringMists(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.CREATURE}, "{3}{U}");
        this.subtype.add(SubType.HUMAN);
        this.subtype.add(SubType.WIZARD);

        this.power = new MageInt(2);
        this.toughness = new MageInt(2);

        // Other blue creatures you control get +1/+1.
        this.addAbility(new SimpleStaticAbility(new BoostControlledEffect(
                1, 1, Duration.WhileOnBattlefield, filterBlue, true
        )));

        // {U}, {T}: Another target blue creature you control gains flying until end of turn.
        Ability ability = new SimpleActivatedAbility(
                new GainAbilityTargetEffect(FlyingAbility.getInstance(), Duration.EndOfTurn), new ManaCostsImpl<>("{U}")
        );
        ability.addCost(new TapSourceCost());
        ability.addTarget(new TargetPermanent(filterBlue2));
        this.addAbility(ability);
    }

    private ParagonOfGatheringMists(final ParagonOfGatheringMists card) {
        super(card);
    }

    @Override
    public ParagonOfGatheringMists copy() {
        return new ParagonOfGatheringMists(this);
    }
}
