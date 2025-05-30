package mage.cards.g;

import mage.MageInt;
import mage.abilities.Ability;
import mage.abilities.common.EntersBattlefieldTriggeredAbility;
import mage.abilities.common.GodEternalDiesTriggeredAbility;
import mage.abilities.effects.OneShotEffect;
import mage.abilities.keyword.MenaceAbility;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.constants.Outcome;
import mage.constants.SubType;
import mage.constants.SuperType;
import mage.filter.StaticFilters;
import mage.game.Game;
import mage.game.permanent.Permanent;
import mage.players.Player;
import mage.target.Target;
import mage.target.common.TargetSacrifice;

import java.util.UUID;

/**
 * @author TheElk801
 */
public final class GodEternalBontu extends CardImpl {

    public GodEternalBontu(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.CREATURE}, "{3}{B}{B}");

        this.supertype.add(SuperType.LEGENDARY);
        this.subtype.add(SubType.ZOMBIE);
        this.subtype.add(SubType.GOD);
        this.power = new MageInt(5);
        this.toughness = new MageInt(6);

        // Menace
        this.addAbility(new MenaceAbility(false));

        // When God-Eternal Bontu enters the battlefield, sacrifice any number of other permanents, then draw that many cards.
        this.addAbility(new EntersBattlefieldTriggeredAbility(new GodEternalBontuEffect()));

        // When God-Eternal Bontu dies or is put into exile from the battlefield, you may put it into its owner's library third from the top.
        this.addAbility(new GodEternalDiesTriggeredAbility());
    }

    private GodEternalBontu(final GodEternalBontu card) {
        super(card);
    }

    @Override
    public GodEternalBontu copy() {
        return new GodEternalBontu(this);
    }
}

class GodEternalBontuEffect extends OneShotEffect {

    GodEternalBontuEffect() {
        super(Outcome.Benefit);
        staticText = "sacrifice any number of other permanents, then draw that many cards.";
    }

    private GodEternalBontuEffect(final GodEternalBontuEffect effect) {
        super(effect);
    }

    @Override
    public GodEternalBontuEffect copy() {
        return new GodEternalBontuEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Player player = game.getPlayer(source.getControllerId());
        if (player == null) {
            return false;
        }
        Target target = new TargetSacrifice(0, Integer.MAX_VALUE, StaticFilters.FILTER_OTHER_CONTROLLED_PERMANENTS);
        if (!player.choose(outcome, target, source, game)) {
            return false;
        }
        int counter = 0;
        for (UUID permanentId : target.getTargets()) {
            Permanent permanent = game.getPermanent(permanentId);
            if (permanent != null && permanent.sacrifice(source, game)) {
                counter++;
            }
        }
        return player.drawCards(counter, source, game) > 0;
    }
}
