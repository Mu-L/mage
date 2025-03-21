package mage.cards.m;

import mage.MageInt;
import mage.MageObject;
import mage.abilities.Ability;
import mage.abilities.SpellAbility;
import mage.abilities.common.AsEntersBattlefieldAbility;
import mage.abilities.common.SimpleStaticAbility;
import mage.abilities.effects.ContinuousRuleModifyingEffectImpl;
import mage.abilities.effects.common.ChooseACardNameEffect;
import mage.cards.Card;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.*;
import mage.game.Game;
import mage.game.events.GameEvent;
import mage.util.CardUtil;

import java.util.UUID;

/**
 * @author Plopman
 */
public final class MeddlingMage extends CardImpl {

    public MeddlingMage(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.CREATURE}, "{W}{U}");
        this.subtype.add(SubType.HUMAN);
        this.subtype.add(SubType.WIZARD);

        this.power = new MageInt(2);
        this.toughness = new MageInt(2);

        // As Meddling Mage enters the battlefield, choose a nonland card name.
        this.addAbility(new AsEntersBattlefieldAbility(new ChooseACardNameEffect(ChooseACardNameEffect.TypeOfName.NON_LAND_NAME)));

        // Spells with the chosen name can't be cast.
        this.addAbility(new SimpleStaticAbility(new MeddlingMageReplacementEffect()));
    }

    private MeddlingMage(final MeddlingMage card) {
        super(card);
    }

    @Override
    public MeddlingMage copy() {
        return new MeddlingMage(this);
    }
}

class MeddlingMageReplacementEffect extends ContinuousRuleModifyingEffectImpl {

    MeddlingMageReplacementEffect() {
        super(Duration.WhileOnBattlefield, Outcome.Detriment);
        staticText = "Spells with the chosen name can't be cast";
    }

    private MeddlingMageReplacementEffect(final MeddlingMageReplacementEffect effect) {
        super(effect);
    }

    @Override
    public MeddlingMageReplacementEffect copy() {
        return new MeddlingMageReplacementEffect(this);
    }

    @Override
    public String getInfoMessage(Ability source, GameEvent event, Game game) {
        MageObject mageObject = game.getObject(source);
        if (mageObject != null) {
            return "You can't cast a spell with that name (" + mageObject.getName() + " in play).";
        }
        return null;
    }

    @Override
    public boolean checksEventType(GameEvent event, Game game) {
        return event.getType() == GameEvent.EventType.CAST_SPELL_LATE;
    }

    @Override
    public boolean applies(GameEvent event, Ability source, Game game) {
        SpellAbility spellAbility = SpellAbility.getSpellAbilityFromEvent(event, game);
        if (spellAbility == null) {
            return false;
        }
        Card card = spellAbility.getCharacteristics(game);
        if (card == null) {
            return false;
        }
        String cardName = (String) game.getState().getValue(source.getSourceId().toString() + ChooseACardNameEffect.INFO_KEY);
        return CardUtil.haveSameNames(card, cardName, game);
    }
}
