package mage.game.permanent.token;

import mage.abilities.Ability;
import mage.abilities.common.SimpleActivatedAbility;
import mage.abilities.costs.common.SacrificeSourceCost;
import mage.abilities.costs.mana.ManaCostsImpl;
import mage.abilities.effects.common.DamageTargetEffect;
import mage.abilities.keyword.FlyingAbility;
import mage.constants.CardType;
import mage.constants.Zone;
import mage.filter.common.FilterAttackingCreature;
import mage.filter.predicate.Predicates;
import mage.filter.predicate.mageobject.AbilityPredicate;
import mage.target.TargetPermanent;
import mage.target.common.TargetCreaturePermanent;

/**
 * @author spjspj
 */
public final class LandMineToken extends TokenImpl {

    private static final FilterAttackingCreature filter = new FilterAttackingCreature("attacking creature without flying");

    static {
        filter.add(Predicates.not(new AbilityPredicate(FlyingAbility.class)));
    }

    public LandMineToken() {
        super("Land Mine", "colorless artifact token named Land Mine with \"{R}, Sacrifice this artifact: This artifact deals 2 damage to target attacking creature without flying.\"");
        cardType.add(CardType.ARTIFACT);
        Ability ability = new SimpleActivatedAbility(new DamageTargetEffect(2), new ManaCostsImpl<>("{R}"));
        ability.addCost(new SacrificeSourceCost());
        ability.addTarget(new TargetPermanent(filter));
        this.addAbility(ability);
    }

    private LandMineToken(final LandMineToken token) {
        super(token);
    }

    public LandMineToken copy() {
        return new LandMineToken(this);
    }
}
