package mage.cards.t;

import mage.abilities.Ability;
import mage.abilities.common.ActivateAsSorceryActivatedAbility;
import mage.abilities.common.SimpleStaticAbility;
import mage.abilities.condition.Condition;
import mage.abilities.condition.common.YouGainedLifeCondition;
import mage.abilities.costs.common.ExileSourceCost;
import mage.abilities.costs.common.TapSourceCost;
import mage.abilities.costs.mana.ManaCostsImpl;
import mage.abilities.effects.common.CreateTokenEffect;
import mage.abilities.effects.common.continuous.CantLoseGameSourceControllerEffect;
import mage.abilities.effects.common.continuous.GainAbilityTargetEffect;
import mage.abilities.effects.common.counter.AddCountersTargetEffect;
import mage.abilities.hint.ConditionHint;
import mage.abilities.hint.Hint;
import mage.abilities.triggers.BeginningOfEndStepTriggeredAbility;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.*;
import mage.counters.CounterType;
import mage.filter.FilterPermanent;
import mage.game.permanent.token.Angel33Token;
import mage.target.TargetPermanent;
import mage.watchers.common.PlayerGainedLifeWatcher;

import java.util.UUID;

/**
 * @author TheElk801
 */
public final class TheBookOfExaltedDeeds extends CardImpl {

    private static final Condition condition = new YouGainedLifeCondition(ComparisonType.MORE_THAN, 2);
    private static final Hint hint = new ConditionHint(condition);
    private static final FilterPermanent filter = new FilterPermanent(SubType.ANGEL, "Angel");

    public TheBookOfExaltedDeeds(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.ARTIFACT}, "{W}{W}{W}");

        this.supertype.add(SuperType.LEGENDARY);

        // At the beginning of your end step, if you gained 3 or more life this turn, create a 3/3 white Angel creature token with flying.
        this.addAbility(new BeginningOfEndStepTriggeredAbility(new CreateTokenEffect(new Angel33Token()))
                .withInterveningIf(condition).addHint(hint), new PlayerGainedLifeWatcher());

        // {W}{W}{W}, {T}, Exile The Book of Exalted Deeds: Put an enlightened counter on target Angel. It gains "You can't lose the game and your opponents can't win the game." Activate only as a sorcery.
        Ability ability = new ActivateAsSorceryActivatedAbility(
                new AddCountersTargetEffect(CounterType.ENLIGHTENED.createInstance()), new ManaCostsImpl<>("{W}{W}{W}")
        );
        ability.addEffect(new GainAbilityTargetEffect(
                new SimpleStaticAbility(new CantLoseGameSourceControllerEffect()), Duration.Custom,
                "It gains \"You can't lose the game and your opponents can't win the game.\""
        ));
        ability.addCost(new TapSourceCost());
        ability.addCost(new ExileSourceCost());
        ability.addTarget(new TargetPermanent(filter));
        this.addAbility(ability);
    }

    private TheBookOfExaltedDeeds(final TheBookOfExaltedDeeds card) {
        super(card);
    }

    @Override
    public TheBookOfExaltedDeeds copy() {
        return new TheBookOfExaltedDeeds(this);
    }
}
