package mage.cards.b;

import mage.MageItem;
import mage.abilities.Ability;
import mage.abilities.effects.OneShotEffect;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.constants.Outcome;
import mage.counters.CounterType;
import mage.filter.FilterPermanent;
import mage.filter.common.FilterCreaturePermanent;
import mage.filter.predicate.ObjectSourcePlayer;
import mage.filter.predicate.ObjectSourcePlayerPredicate;
import mage.filter.predicate.other.AnotherTargetPredicate;
import mage.game.Game;
import mage.game.permanent.Permanent;
import mage.game.stack.StackObject;
import mage.players.Player;
import mage.target.TargetPermanent;
import mage.target.common.TargetCreaturePermanent;

import java.util.UUID;

/**
 * @author LevelX2
 */
public final class Bioshift extends CardImpl {

    private static final FilterPermanent filter = new FilterCreaturePermanent("another target creature with the same controller");

    static {
        filter.add(new AnotherTargetPredicate(2));
        filter.add(new SameControllerPredicate());
    }

    public Bioshift(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.INSTANT}, "{G/U}");

        // Move any number of +1/+1 counters from target creature onto another target creature with the same controller.
        this.getSpellAbility().addEffect(new MoveCounterFromTargetToTargetEffect());

        this.getSpellAbility().addTarget(new TargetCreaturePermanent().withChooseHint("to take counters from").setTargetTag(1));
        this.getSpellAbility().addTarget(new TargetPermanent(filter).withChooseHint("to put counter on").setTargetTag(2));
    }


    private Bioshift(final Bioshift card) {
        super(card);
    }

    @Override
    public Bioshift copy() {
        return new Bioshift(this);
    }
}

class MoveCounterFromTargetToTargetEffect extends OneShotEffect {

    MoveCounterFromTargetToTargetEffect() {
        super(Outcome.Detriment);
        this.staticText = "Move any number of +1/+1 counters from target creature onto another target creature with the same controller";
    }

    private MoveCounterFromTargetToTargetEffect(final MoveCounterFromTargetToTargetEffect effect) {
        super(effect);
    }

    @Override
    public MoveCounterFromTargetToTargetEffect copy() {
        return new MoveCounterFromTargetToTargetEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Player controller = game.getPlayer(source.getControllerId());
        if (controller != null) {
            Permanent fromPermanent = game.getPermanent(getTargetPointer().getFirst(game, source));
            Permanent toPermanent = null;
            if (source.getTargets().size() > 1) {
                toPermanent = game.getPermanent(source.getTargets().get(1).getFirstTarget());
            }
            if (fromPermanent == null || toPermanent == null || !fromPermanent.isControlledBy(toPermanent.getControllerId())) {
                return false;
            }
            int amountCounters = fromPermanent.getCounters(game).getCount(CounterType.P1P1);
            if (amountCounters > 0) {
                int amountToMove = controller.getAmount(0, amountCounters, "Choose how many counters to move", source, game);
                if (amountToMove > 0) {
                    fromPermanent.removeCounters(CounterType.P1P1.createInstance(amountToMove), source, game);
                    toPermanent.addCounters(CounterType.P1P1.createInstance(amountToMove), source.getControllerId(), source, game);
                }
            }
            return true;
        }
        return false;

    }
}

class SameControllerPredicate implements ObjectSourcePlayerPredicate<MageItem> {

    @Override
    public boolean apply(ObjectSourcePlayer<MageItem> input, Game game) {
        StackObject source = game.getStack().getStackObject(input.getSourceId());
        if (source != null) {
            if (source.getStackAbility().getTargets().isEmpty()
                    || source.getStackAbility().getTargets().get(0).getTargets().isEmpty()) {
                return true;
            }
            Permanent firstTarget = game.getPermanent(
                    source.getStackAbility().getTargets().get(0).getTargets().get(0));
            Permanent inputPermanent = game.getPermanent(input.getObject().getId());
            if (firstTarget != null && inputPermanent != null) {
                return firstTarget.isControlledBy(inputPermanent.getControllerId());
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return "Target with the same controller";
    }

}
