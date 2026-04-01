package mage.sets;

import mage.cards.ExpansionSet;
import mage.constants.Rarity;
import mage.constants.SetType;

/**
 * @author TheElk801
 */
public final class SecretsOfStrixhaven extends ExpansionSet {

    private static final SecretsOfStrixhaven instance = new SecretsOfStrixhaven();

    public static SecretsOfStrixhaven getInstance() {
        return instance;
    }

    private SecretsOfStrixhaven() {
        super("Secrets of Strixhaven", "SOS", ExpansionSet.buildDate(2026, 4, 24), SetType.EXPANSION);
        this.blockName = "Secrets of Strixhaven"; // for sorting in GUI
        this.hasBasicLands = true;

        cards.add(new SetCardInfo("Arcane Omens", 73, Rarity.UNCOMMON, mage.cards.a.ArcaneOmens.class));
        cards.add(new SetCardInfo("Archaic's Agony", 107, Rarity.UNCOMMON, mage.cards.a.ArchaicsAgony.class));
        cards.add(new SetCardInfo("Banishing Betrayal", 38, Rarity.COMMON, mage.cards.b.BanishingBetrayal.class));
        cards.add(new SetCardInfo("Bogwater Lumaret", 177, Rarity.COMMON, mage.cards.b.BogwaterLumaret.class));
        cards.add(new SetCardInfo("Colorstorm Stallion", 180, Rarity.RARE, mage.cards.c.ColorstormStallion.class, NON_FULL_USE_VARIOUS));
        cards.add(new SetCardInfo("Colorstorm Stallion", 299, Rarity.RARE, mage.cards.c.ColorstormStallion.class, NON_FULL_USE_VARIOUS));
        cards.add(new SetCardInfo("Cuboid Colony", 183, Rarity.UNCOMMON, mage.cards.c.CuboidColony.class));
        cards.add(new SetCardInfo("Deathcap Glade", 253, Rarity.RARE, mage.cards.d.DeathcapGlade.class, NON_FULL_USE_VARIOUS));
        cards.add(new SetCardInfo("Deathcap Glade", 301, Rarity.RARE, mage.cards.d.DeathcapGlade.class, NON_FULL_USE_VARIOUS));
        cards.add(new SetCardInfo("Dreamroot Cascade", 254, Rarity.RARE, mage.cards.d.DreamrootCascade.class, NON_FULL_USE_VARIOUS));
        cards.add(new SetCardInfo("Dreamroot Cascade", 302, Rarity.RARE, mage.cards.d.DreamrootCascade.class, NON_FULL_USE_VARIOUS));
        cards.add(new SetCardInfo("Duel Tactics", 112, Rarity.UNCOMMON, mage.cards.d.DuelTactics.class));
        cards.add(new SetCardInfo("Elemental Mascot", 185, Rarity.COMMON, mage.cards.e.ElementalMascot.class));
        cards.add(new SetCardInfo("Exhibition Tidecaller", 316, Rarity.RARE, mage.cards.e.ExhibitionTidecaller.class, NON_FULL_USE_VARIOUS));
        cards.add(new SetCardInfo("Exhibition Tidecaller", 48, Rarity.RARE, mage.cards.e.ExhibitionTidecaller.class, NON_FULL_USE_VARIOUS));
        cards.add(new SetCardInfo("Forest", 271, Rarity.LAND, mage.cards.basiclands.Forest.class, FULL_ART_BFZ_VARIOUS));
        cards.add(new SetCardInfo("Fractal Mascot", 189, Rarity.COMMON, mage.cards.f.FractalMascot.class));
        cards.add(new SetCardInfo("Graduation Day", 16, Rarity.UNCOMMON, mage.cards.g.GraduationDay.class));
        cards.add(new SetCardInfo("Grapple with Death", 192, Rarity.COMMON, mage.cards.g.GrappleWithDeath.class));
        cards.add(new SetCardInfo("Heated Argument", 118, Rarity.COMMON, mage.cards.h.HeatedArgument.class));
        cards.add(new SetCardInfo("Inkling Mascot", 196, Rarity.COMMON, mage.cards.i.InklingMascot.class));
        cards.add(new SetCardInfo("Island", 268, Rarity.LAND, mage.cards.basiclands.Island.class, FULL_ART_BFZ_VARIOUS));
        cards.add(new SetCardInfo("Lorehold Charm", 200, Rarity.UNCOMMON, mage.cards.l.LoreholdCharm.class, NON_FULL_USE_VARIOUS));
        cards.add(new SetCardInfo("Lorehold Charm", 363, Rarity.UNCOMMON, mage.cards.l.LoreholdCharm.class, NON_FULL_USE_VARIOUS));
        cards.add(new SetCardInfo("Mathemagics", 320, Rarity.MYTHIC, mage.cards.m.Mathemagics.class, NON_FULL_USE_VARIOUS));
        cards.add(new SetCardInfo("Mathemagics", 58, Rarity.MYTHIC, mage.cards.m.Mathemagics.class, NON_FULL_USE_VARIOUS));
        cards.add(new SetCardInfo("Mountain", 270, Rarity.LAND, mage.cards.basiclands.Mountain.class, FULL_ART_BFZ_VARIOUS));
        cards.add(new SetCardInfo("Oracle's Restoration", 156, Rarity.COMMON, mage.cards.o.OraclesRestoration.class));
        cards.add(new SetCardInfo("Pensive Professor", 321, Rarity.RARE, mage.cards.p.PensiveProfessor.class, NON_FULL_USE_VARIOUS));
        cards.add(new SetCardInfo("Pensive Professor", 63, Rarity.RARE, mage.cards.p.PensiveProfessor.class, NON_FULL_USE_VARIOUS));
        cards.add(new SetCardInfo("Pest Mascot", 209, Rarity.COMMON, mage.cards.p.PestMascot.class));
        cards.add(new SetCardInfo("Plains", 267, Rarity.LAND, mage.cards.basiclands.Plains.class, FULL_ART_BFZ_VARIOUS));
        cards.add(new SetCardInfo("Prismari Charm", 211, Rarity.UNCOMMON, mage.cards.p.PrismariCharm.class, NON_FULL_USE_VARIOUS));
        cards.add(new SetCardInfo("Prismari Charm", 364, Rarity.UNCOMMON, mage.cards.p.PrismariCharm.class, NON_FULL_USE_VARIOUS));
        cards.add(new SetCardInfo("Professor Dellian Fel", 214, Rarity.MYTHIC, mage.cards.p.ProfessorDellianFel.class, NON_FULL_USE_VARIOUS));
        cards.add(new SetCardInfo("Professor Dellian Fel", 283, Rarity.MYTHIC, mage.cards.p.ProfessorDellianFel.class, NON_FULL_USE_VARIOUS));
        cards.add(new SetCardInfo("Pull from the Grave", 95, Rarity.COMMON, mage.cards.p.PullFromTheGrave.class));
        cards.add(new SetCardInfo("Shattered Acolyte", 31, Rarity.COMMON, mage.cards.s.ShatteredAcolyte.class));
        cards.add(new SetCardInfo("Shattered Sanctum", 260, Rarity.RARE, mage.cards.s.ShatteredSanctum.class, NON_FULL_USE_VARIOUS));
        cards.add(new SetCardInfo("Shattered Sanctum", 303, Rarity.RARE, mage.cards.s.ShatteredSanctum.class, NON_FULL_USE_VARIOUS));
        cards.add(new SetCardInfo("Silverquill Charm", 225, Rarity.UNCOMMON, mage.cards.s.SilverquillCharm.class, NON_FULL_USE_VARIOUS));
        cards.add(new SetCardInfo("Silverquill Charm", 366, Rarity.UNCOMMON, mage.cards.s.SilverquillCharm.class, NON_FULL_USE_VARIOUS));
        cards.add(new SetCardInfo("Spirit Mascot", 230, Rarity.COMMON, mage.cards.s.SpiritMascot.class));
        cards.add(new SetCardInfo("Stormcarved Coast", 263, Rarity.RARE, mage.cards.s.StormcarvedCoast.class, NON_FULL_USE_VARIOUS));
        cards.add(new SetCardInfo("Stormcarved Coast", 304, Rarity.RARE, mage.cards.s.StormcarvedCoast.class, NON_FULL_USE_VARIOUS));
        cards.add(new SetCardInfo("Stress Dream", 235, Rarity.UNCOMMON, mage.cards.s.StressDream.class));
        cards.add(new SetCardInfo("Sundown Pass", 264, Rarity.RARE, mage.cards.s.SundownPass.class, NON_FULL_USE_VARIOUS));
        cards.add(new SetCardInfo("Sundown Pass", 305, Rarity.RARE, mage.cards.s.SundownPass.class, NON_FULL_USE_VARIOUS));
        cards.add(new SetCardInfo("Swamp", 269, Rarity.LAND, mage.cards.basiclands.Swamp.class, FULL_ART_BFZ_VARIOUS));
        cards.add(new SetCardInfo("Together as One", 307, Rarity.RARE, mage.cards.t.TogetherAsOne.class, NON_FULL_USE_VARIOUS));
        cards.add(new SetCardInfo("Together as One", 4, Rarity.RARE, mage.cards.t.TogetherAsOne.class, NON_FULL_USE_VARIOUS));
        cards.add(new SetCardInfo("Witherbloom Charm", 244, Rarity.UNCOMMON, mage.cards.w.WitherbloomCharm.class, NON_FULL_USE_VARIOUS));
        cards.add(new SetCardInfo("Witherbloom Charm", 367, Rarity.UNCOMMON, mage.cards.w.WitherbloomCharm.class, NON_FULL_USE_VARIOUS));
    }
}
