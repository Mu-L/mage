package mage.cards.r;

import mage.MageInt;
import mage.abilities.Ability;
import mage.abilities.condition.common.SourceTappedCondition;
import mage.abilities.costs.mana.GenericManaCost;
import mage.abilities.effects.OneShotEffect;
import mage.abilities.keyword.TrampleAbility;
import mage.abilities.keyword.WardAbility;
import mage.abilities.triggers.BeginningOfCombatTriggeredAbility;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.constants.Outcome;
import mage.constants.SubType;
import mage.constants.TargetController;
import mage.filter.StaticFilters;
import mage.filter.common.FilterControlledCreaturePermanent;
import mage.filter.predicate.permanent.TappedPredicate;
import mage.game.Game;
import mage.game.permanent.Permanent;
import mage.game.permanent.token.FishToken;
import mage.players.Player;
import mage.target.TargetPermanent;

import java.util.UUID;

/**
 * @author weirddan455
 */
public final class ReservoirKraken extends CardImpl {

    public ReservoirKraken(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.CREATURE}, "{2}{U}{U}");

        this.subtype.add(SubType.KRAKEN);
        this.power = new MageInt(6);
        this.toughness = new MageInt(6);

        // Trample
        this.addAbility(TrampleAbility.getInstance());

        // Ward {2}
        this.addAbility(new WardAbility(new GenericManaCost(2), false));

        // At the beginning of each combat, if Reservoir Kraken is untapped, any opponent may tap an untapped creature they control. If they do, tap Reservoir Kraken and create a 1/1 blue Fish creature token with "This creature can't be blocked."
        this.addAbility(new BeginningOfCombatTriggeredAbility(
                TargetController.ANY, new ReservoirKrakenEffect(), false
        ).withInterveningIf(SourceTappedCondition.UNTAPPED));
    }

    private ReservoirKraken(final ReservoirKraken card) {
        super(card);
    }

    @Override
    public ReservoirKraken copy() {
        return new ReservoirKraken(this);
    }
}

class ReservoirKrakenEffect extends OneShotEffect {

    private static final FilterControlledCreaturePermanent filter = new FilterControlledCreaturePermanent("untapped creature you control");

    static {
        filter.add(TappedPredicate.UNTAPPED);
    }

    public ReservoirKrakenEffect() {
        super(Outcome.Tap);
        this.staticText = "any opponent may tap an untapped creature they control. If they do, tap {this} and create a 1/1 blue Fish creature token with \"This token can't be blocked.\"";
    }

    private ReservoirKrakenEffect(final ReservoirKrakenEffect effect) {
        super(effect);
    }

    @Override
    public ReservoirKrakenEffect copy() {
        return new ReservoirKrakenEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        boolean opponentTapped = false;
        for (UUID opponentId : game.getOpponents(source.getControllerId())) {
            Player opponent = game.getPlayer(opponentId);
            if (opponent == null) {
                continue;
            }
            TargetPermanent target = new TargetPermanent(StaticFilters.FILTER_CONTROLLED_UNTAPPED_CREATURE);
            target.withNotTarget(true);
            if (!target.canChoose(opponentId, source, game)
                    || !opponent.chooseUse(Outcome.AIDontUseIt, "Tap an untapped creature you control?", source, game)) {
                continue;
            }
            opponent.chooseTarget(Outcome.Tap, target, source, game);
            Permanent permanent = game.getPermanent(target.getFirstTarget());
            if (permanent != null && permanent.tap(source, game)) {
                opponentTapped = true;
            }
        }
        if (opponentTapped) {
            Permanent kraken = source.getSourcePermanentIfItStillExists(game);
            if (kraken != null) {
                kraken.tap(source, game);
            }
            new FishToken().putOntoBattlefield(1, game, source);
        }
        return true;
    }
}
