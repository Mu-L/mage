package mage.cards.v;

import mage.abilities.Ability;
import mage.abilities.effects.OneShotEffect;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.constants.Outcome;
import mage.constants.SubType;
import mage.constants.Zone;
import mage.filter.FilterPermanent;
import mage.filter.StaticFilters;
import mage.filter.common.FilterLandPermanent;
import mage.game.Game;
import mage.game.permanent.Permanent;
import mage.players.Player;
import mage.target.TargetPermanent;
import mage.target.targetadjustment.XTargetsCountAdjuster;

import java.util.List;
import java.util.UUID;

/**
 * @author escplan9 (Derek Monturo - dmontur1 at gmail dot com)
 */
public final class VolcanicEruption extends CardImpl {

    private static final FilterPermanent filter = new FilterLandPermanent(SubType.MOUNTAIN, "Mountain");

    public VolcanicEruption(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.SORCERY}, "{X}{U}{U}{U}");

        // Destroy X target Mountains. Volcanic Eruption deals damage to each creature and each player equal to the number of Mountains put into a graveyard this way.
        this.getSpellAbility().addEffect(new VolcanicEruptionEffect());
        this.getSpellAbility().addTarget(new TargetPermanent(filter));
        this.getSpellAbility().setTargetAdjuster(new XTargetsCountAdjuster());
    }

    private VolcanicEruption(final VolcanicEruption card) {
        super(card);
    }

    @Override
    public VolcanicEruption copy() {
        return new VolcanicEruption(this);
    }
}

class VolcanicEruptionEffect extends OneShotEffect {

    VolcanicEruptionEffect() {
        super(Outcome.DestroyPermanent);
        this.staticText = "Destroy X target Mountains. {this} deals damage to each creature and each player equal to the number of Mountains put into a graveyard this way.";
    }

    private VolcanicEruptionEffect(final VolcanicEruptionEffect effect) {
        super(effect);
    }

    @Override
    public VolcanicEruptionEffect copy() {
        return new VolcanicEruptionEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {

        int destroyedCount = 0;
        for (UUID targetID : this.getTargetPointer().getTargets(game, source)) {
            Permanent permanent = game.getPermanent(targetID);
            if (permanent != null) {
                if (permanent.destroy(source, game, false)) {
                    if (game.getState().getZone(permanent.getId()) == Zone.GRAVEYARD) {
                        destroyedCount++;
                    }
                }
            }
        }

        if (destroyedCount > 0) {
            List<Permanent> permanents = game.getBattlefield().getActivePermanents(StaticFilters.FILTER_PERMANENT_CREATURE, source.getControllerId(), game);
            for (Permanent permanent : permanents) {
                permanent.damage(destroyedCount, source.getSourceId(), source, game, false, true);
            }
            for (UUID playerId : game.getState().getPlayersInRange(source.getControllerId(), game)) {
                Player player = game.getPlayer(playerId);
                if (player != null) {
                    player.damage(destroyedCount, source.getSourceId(), source, game);
                }
            }
        }
        return true;
    }
}
