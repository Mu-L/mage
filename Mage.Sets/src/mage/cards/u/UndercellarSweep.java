package mage.cards.u;

import mage.abilities.Ability;
import mage.abilities.common.AttacksWithCreaturesTriggeredAbility;
import mage.abilities.common.EntersBattlefieldTriggeredAbility;
import mage.abilities.condition.Condition;
import mage.abilities.effects.common.CreateTokenEffect;
import mage.abilities.effects.common.TakeTheInitiativeEffect;
import mage.abilities.hint.common.InitiativeHint;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.game.Game;
import mage.game.permanent.token.SoldierToken;

import java.util.UUID;

/**
 * @author TheElk801
 */
public final class UndercellarSweep extends CardImpl {

    public UndercellarSweep(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.ENCHANTMENT}, "{4}{W}");

        // When Undercellar Sweep enters the battlefield, you take the initiative.
        this.addAbility(new EntersBattlefieldTriggeredAbility(new TakeTheInitiativeEffect())
                .addHint(InitiativeHint.instance));

        // Whenever you attack, if you or a player you're attacking has the initiative, you create two 1/1 white Soldier creature token that are tapped and attacking.
        this.addAbility(new AttacksWithCreaturesTriggeredAbility(
                new CreateTokenEffect(new SoldierToken(), 2, true, true)
                        .setText("you create two 1/1 white Soldier creature tokens that are tapped and attacking"), 1
        ).withInterveningIf(UndercellarSweepCondition.instance));
    }

    private UndercellarSweep(final UndercellarSweep card) {
        super(card);
    }

    @Override
    public UndercellarSweep copy() {
        return new UndercellarSweep(this);
    }
}

enum UndercellarSweepCondition implements Condition {
    instance;

    @Override
    public boolean apply(Game game, Ability source) {
        if (game.getInitiativeId() == null) {
            return false;
        }
        return source.isControlledBy(game.getInitiativeId())
                || game
                .getCombat()
                .getAttackers()
                .stream()
                .filter(uuid -> source.isControlledBy(game.getControllerId(uuid)))
                .map(game.getCombat()::getDefenderId)
                .anyMatch(game.getInitiativeId()::equals);
    }

    @Override
    public String toString() {
        return "you or a player you're attacking has the initiative";
    }
}
