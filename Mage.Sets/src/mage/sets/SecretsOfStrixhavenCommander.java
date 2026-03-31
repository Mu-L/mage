package mage.sets;

import mage.cards.ExpansionSet;
import mage.constants.Rarity;
import mage.constants.SetType;

/**
 * @author TheElk801
 */
public final class SecretsOfStrixhavenCommander extends ExpansionSet {

    private static final SecretsOfStrixhavenCommander instance = new SecretsOfStrixhavenCommander();

    public static SecretsOfStrixhavenCommander getInstance() {
        return instance;
    }

    private SecretsOfStrixhavenCommander() {
        super("Secrets of Strixhaven Commander", "SOC", ExpansionSet.buildDate(2026, 4, 24), SetType.EXPANSION);
        this.hasBasicLands = false;

        cards.add(new SetCardInfo("Sol Ring", 427, Rarity.MYTHIC, mage.cards.s.SolRing.class));
        cards.add(new SetCardInfo("Talisman of Conviction", 428, Rarity.MYTHIC, mage.cards.t.TalismanOfConviction.class));
        cards.add(new SetCardInfo("Talisman of Creativity", 429, Rarity.MYTHIC, mage.cards.t.TalismanOfCreativity.class));
        cards.add(new SetCardInfo("Talisman of Curiosity", 430, Rarity.MYTHIC, mage.cards.t.TalismanOfCuriosity.class));
        cards.add(new SetCardInfo("Talisman of Hierarchy", 431, Rarity.MYTHIC, mage.cards.t.TalismanOfHierarchy.class));
        cards.add(new SetCardInfo("Talisman of Resilience", 432, Rarity.MYTHIC, mage.cards.t.TalismanOfResilience.class));
    }
}
