
package mage.cards.c;

import mage.abilities.Ability;
import mage.abilities.DelayedTriggeredAbility;
import mage.abilities.common.delayed.AtTheBeginOfNextEndStepDelayedTriggeredAbility;
import mage.abilities.effects.ContinuousEffect;
import mage.abilities.effects.OneShotEffect;
import mage.abilities.effects.common.ExileTargetEffect;
import mage.abilities.effects.common.continuous.GainAbilityTargetEffect;
import mage.abilities.keyword.BuybackAbility;
import mage.abilities.keyword.HasteAbility;
import mage.cards.Card;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.constants.Duration;
import mage.constants.Outcome;
import mage.constants.Zone;
import mage.game.Game;
import mage.game.permanent.Permanent;
import mage.players.Player;
import mage.target.targetpointer.FixedTarget;
import mage.util.CardUtil;

import java.util.UUID;

/**
 * @author LevelX2
 */
public final class CorpseDance extends CardImpl {

    public CorpseDance(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.INSTANT}, "{2}{B}");

        // Buyback {2}
        this.addAbility(new BuybackAbility("{2}"));

        // Return the top creature card of your graveyard to the battlefield. That creature gains haste until end of turn. Exile it at the beginning of the next end step.
        this.getSpellAbility().addEffect(new CorpseDanceEffect());
    }

    private CorpseDance(final CorpseDance card) {
        super(card);
    }

    @Override
    public CorpseDance copy() {
        return new CorpseDance(this);
    }
}

class CorpseDanceEffect extends OneShotEffect {

    CorpseDanceEffect() {
        super(Outcome.Benefit);
        this.staticText = "Return the top creature card of your graveyard to the battlefield. That creature gains haste until end of turn. Exile it at the beginning of the next end step";
    }

    private CorpseDanceEffect(final CorpseDanceEffect effect) {
        super(effect);
    }

    @Override
    public CorpseDanceEffect copy() {
        return new CorpseDanceEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Player controller = game.getPlayer(source.getControllerId());
        if (controller == null) {
            return false;
        }
        Card lastCreatureCard = null;
        for (Card card : controller.getGraveyard().getCards(game)) {
            if (card.isCreature(game)) {
                lastCreatureCard = card;
            }
        }
        if (lastCreatureCard != null && controller.moveCards(lastCreatureCard, Zone.BATTLEFIELD, source, game)) {
            Permanent creature = CardUtil.getPermanentFromCardPutToBattlefield(lastCreatureCard, game);
            if (creature != null) {
                FixedTarget blueprintTarget = new FixedTarget(creature, game);
                // Gains Haste
                ContinuousEffect hasteEffect = new GainAbilityTargetEffect(HasteAbility.getInstance(), Duration.EndOfTurn);
                hasteEffect.setTargetPointer(blueprintTarget.copy());
                game.addEffect(hasteEffect, source);
                // Exile it at end of turn
                ExileTargetEffect exileEffect = new ExileTargetEffect(null, "", Zone.BATTLEFIELD);
                exileEffect.setTargetPointer(blueprintTarget.copy());
                DelayedTriggeredAbility delayedAbility = new AtTheBeginOfNextEndStepDelayedTriggeredAbility(exileEffect);
                game.addDelayedTriggeredAbility(delayedAbility, source);
            }
        }
        return true;
    }
}
