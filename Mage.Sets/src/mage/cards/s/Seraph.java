package mage.cards.s;

import mage.MageInt;
import mage.abilities.Ability;
import mage.abilities.DelayedTriggeredAbility;
import mage.abilities.common.DealtDamageAndDiedTriggeredAbility;
import mage.abilities.common.delayed.AtTheBeginOfNextEndStepDelayedTriggeredAbility;
import mage.abilities.effects.Effect;
import mage.abilities.effects.OneShotEffect;
import mage.abilities.effects.common.CreateDelayedTriggeredAbilityEffect;
import mage.abilities.effects.common.SacrificeTargetEffect;
import mage.abilities.keyword.FlyingAbility;
import mage.cards.Card;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.*;
import mage.game.Game;
import mage.game.events.GameEvent;
import mage.game.permanent.Permanent;
import mage.players.Player;
import mage.target.targetpointer.FixedTarget;
import mage.util.CardUtil;

import java.util.UUID;

/**
 *
 * @author jeffwadsworth
 */
public final class Seraph extends CardImpl {

    public Seraph(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.CREATURE}, "{6}{W}");

        this.subtype.add(SubType.ANGEL);
        this.power = new MageInt(4);
        this.toughness = new MageInt(4);

        // Flying
        this.addAbility(FlyingAbility.getInstance());

        // Whenever a creature dealt damage by Seraph this turn dies, put that card onto the battlefield under your control at the beginning of the next end step. Sacrifice the creature when you lose control of Seraph.
        Effect effect = new CreateDelayedTriggeredAbilityEffect(
                new AtTheBeginOfNextEndStepDelayedTriggeredAbility(
                        new SeraphEffect()));
        effect.setText("put that card onto the battlefield under your control at the beginning of the next end step. Sacrifice the creature when you lose control of {this}");
        this.addAbility(new DealtDamageAndDiedTriggeredAbility(effect));

    }

    private Seraph(final Seraph card) {
        super(card);
    }

    @Override
    public Seraph copy() {
        return new Seraph(this);
    }
}

class SeraphEffect extends OneShotEffect {

    SeraphEffect() {
        super(Outcome.Neutral);
        staticText = "put that card onto the battlefield under your control. Sacrifice it when you lose control of {this}";
    }

    private SeraphEffect(final SeraphEffect effect) {
        super(effect);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Player controller = game.getPlayer(source.getControllerId());
        Card creatureCard = game.getCard(getTargetPointer().getFirst(game, source));
        if (controller == null || creatureCard == null) {
            return false;
        }
        controller.moveCards(creatureCard, Zone.BATTLEFIELD, source, game, false, false, false, null);
        Permanent permanent = CardUtil.getPermanentFromCardPutToBattlefield(creatureCard, game);
        if (permanent != null) {
            SacrificeTargetEffect effect = new SacrificeTargetEffect();
            effect.setText("Sacrifice this if Seraph leaves the battlefield or its current controller loses control of it.");
            effect.setTargetPointer(new FixedTarget(permanent, game));
            SeraphDelayedTriggeredAbility dTA = new SeraphDelayedTriggeredAbility(effect, source.getSourceId());
            game.addDelayedTriggeredAbility(dTA, source);
        }
        return true;
    }

    @Override
    public SeraphEffect copy() {
        return new SeraphEffect(this);
    }
}

class SeraphDelayedTriggeredAbility extends DelayedTriggeredAbility {

    UUID seraph;

    SeraphDelayedTriggeredAbility(Effect effect, UUID seraph) {
        super(effect, Duration.EndOfGame, true);
        this.seraph = seraph;
    }

    private SeraphDelayedTriggeredAbility(final SeraphDelayedTriggeredAbility ability) {
        super(ability);
        this.seraph = ability.seraph;
    }

    @Override
    public boolean checkEventType(GameEvent event, Game game) {
        return event.getType() == GameEvent.EventType.LOST_CONTROL
                || event.getType() == GameEvent.EventType.ZONE_CHANGE;
    }

    @Override
    public boolean checkTrigger(GameEvent event, Game game) {
        return event.getTargetId().equals(seraph);
    }

    @Override
    public SeraphDelayedTriggeredAbility copy() {
        return new SeraphDelayedTriggeredAbility(this);
    }

    @Override
    public String getRule() {
        return "Control of Seraph was lost, sacrifice this.";
    }
}
