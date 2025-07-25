
package mage.cards.f;

import mage.MageInt;
import mage.abilities.common.BlocksCreatureTriggeredAbility;
import mage.abilities.common.delayed.AtTheEndOfCombatDelayedTriggeredAbility;
import mage.abilities.effects.Effect;
import mage.abilities.effects.common.CreateDelayedTriggeredAbilityEffect;
import mage.abilities.effects.common.counter.AddCountersSourceEffect;
import mage.abilities.keyword.ReachAbility;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.constants.SubType;
import mage.constants.SuperType;
import mage.counters.CounterType;
import mage.filter.StaticFilters;

import java.util.UUID;

/**
 * @author L_J
 */
public final class FrostwebSpider extends CardImpl {

    public FrostwebSpider(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.CREATURE}, "{2}{G}");
        this.supertype.add(SuperType.SNOW);
        this.subtype.add(SubType.SPIDER);
        this.power = new MageInt(1);
        this.toughness = new MageInt(3);

        // Reach
        this.addAbility(ReachAbility.getInstance());

        // Whenever Frostweb Spider blocks a creature with flying, put a +1/+1 counter on Frostweb Spider at end of combat.
        Effect effect = new CreateDelayedTriggeredAbilityEffect(new AtTheEndOfCombatDelayedTriggeredAbility(new AddCountersSourceEffect(CounterType.P1P1.createInstance())), true);
        effect.setText("put a +1/+1 counter on {this} at end of combat");
        this.addAbility(new BlocksCreatureTriggeredAbility(effect, StaticFilters.FILTER_CREATURE_FLYING, false));
    }

    private FrostwebSpider(final FrostwebSpider card) {
        super(card);
    }

    @Override
    public FrostwebSpider copy() {
        return new FrostwebSpider(this);
    }
}
