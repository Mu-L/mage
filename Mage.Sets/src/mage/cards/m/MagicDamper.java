package mage.cards.m;

import mage.abilities.effects.common.UntapTargetEffect;
import mage.abilities.effects.common.continuous.BoostTargetEffect;
import mage.abilities.effects.common.continuous.GainAbilityTargetEffect;
import mage.abilities.keyword.HexproofAbility;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.constants.Duration;
import mage.target.common.TargetControlledCreaturePermanent;

import java.util.UUID;

/**
 * @author TheElk801
 */
public final class MagicDamper extends CardImpl {

    public MagicDamper(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.INSTANT}, "{U}");

        // Target creature you control gets +1/+1 and gains hexproof until end of turn. Untap it.
        this.getSpellAbility().addEffect(new BoostTargetEffect(
                1, 1, Duration.EndOfTurn
        ).setText("target creature you control gets +1/+1"));
        this.getSpellAbility().addEffect(new GainAbilityTargetEffect(
                HexproofAbility.getInstance(), Duration.EndOfTurn
        ).setText("and gains hexproof until end of turn"));
        this.getSpellAbility().addTarget(new TargetControlledCreaturePermanent());
        this.getSpellAbility().addEffect(new UntapTargetEffect("Untap it"));
    }

    private MagicDamper(final MagicDamper card) {
        super(card);
    }

    @Override
    public MagicDamper copy() {
        return new MagicDamper(this);
    }
}
