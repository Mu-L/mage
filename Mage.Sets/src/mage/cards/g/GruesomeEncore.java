package mage.cards.g;

import mage.abilities.Ability;
import mage.abilities.DelayedTriggeredAbility;
import mage.abilities.common.delayed.AtTheBeginOfNextEndStepDelayedTriggeredAbility;
import mage.abilities.effects.ContinuousEffect;
import mage.abilities.effects.OneShotEffect;
import mage.abilities.effects.common.ExileTargetEffect;
import mage.abilities.effects.common.continuous.GainAbilityTargetEffect;
import mage.abilities.effects.common.replacement.LeaveBattlefieldExileTargetReplacementEffect;
import mage.abilities.keyword.HasteAbility;
import mage.cards.Card;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.constants.Duration;
import mage.constants.Outcome;
import mage.constants.Zone;
import mage.filter.common.FilterCreatureCard;
import mage.game.Game;
import mage.game.permanent.Permanent;
import mage.players.Player;
import mage.target.common.TargetCardInOpponentsGraveyard;
import mage.target.targetpointer.FixedTarget;
import mage.util.CardUtil;

import java.util.UUID;

/**
 * @author North
 */
public final class GruesomeEncore extends CardImpl {

    private static final FilterCreatureCard filter = new FilterCreatureCard("creature card from an opponent's graveyard");

    public GruesomeEncore(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.SORCERY}, "{2}{B}");

        // Put target creature card from an opponent's graveyard onto the battlefield under your control. It gains haste.
        this.getSpellAbility().addEffect(new GruesomeEncoreEffect());

        // Exile it at the beginning of the next end step. If that creature would leave the battlefield, exile it instead of putting it anywhere else.
        this.getSpellAbility().addEffect(new LeaveBattlefieldExileTargetReplacementEffect("that creature"));
        this.getSpellAbility().addTarget(new TargetCardInOpponentsGraveyard(filter));
    }

    private GruesomeEncore(final GruesomeEncore card) {
        super(card);
    }

    @Override
    public GruesomeEncore copy() {
        return new GruesomeEncore(this);
    }
}

class GruesomeEncoreEffect extends OneShotEffect {

    GruesomeEncoreEffect() {
        super(Outcome.PutCreatureInPlay);
        this.staticText = "Put target creature card from an opponent's graveyard onto the battlefield under your control. It gains haste. Exile it at the beginning of the next end step";
    }

    private GruesomeEncoreEffect(final GruesomeEncoreEffect effect) {
        super(effect);
    }

    @Override
    public GruesomeEncoreEffect copy() {
        return new GruesomeEncoreEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Player controller = game.getPlayer(source.getControllerId());
        if (controller == null) {
            return false;
        }
        Card card = game.getCard(getTargetPointer().getFirst(game, source));
        if (card != null) {
            controller.moveCards(card, Zone.BATTLEFIELD, source, game);
            Permanent permanent = CardUtil.getPermanentFromCardPutToBattlefield(card, game);
            if (permanent != null) {
                ContinuousEffect effect = new GainAbilityTargetEffect(HasteAbility.getInstance(), Duration.Custom);
                effect.setTargetPointer(new FixedTarget(permanent, game));
                game.addEffect(effect, source);
                ExileTargetEffect exileEffect = new ExileTargetEffect();
                exileEffect.setTargetPointer(new FixedTarget(permanent, game));
                DelayedTriggeredAbility delayedAbility = new AtTheBeginOfNextEndStepDelayedTriggeredAbility(exileEffect);
                game.addDelayedTriggeredAbility(delayedAbility, source);
            }
            return true;
        }

        return false;
    }
}
