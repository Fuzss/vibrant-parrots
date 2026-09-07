package fuzs.vibrantparrots.common.data.client;

import fuzs.puzzleslib.api.block.v1.ColorCollection;
import fuzs.puzzleslib.api.client.data.v2.AbstractLanguageProvider;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import fuzs.vibrantparrots.common.VibrantParrots;
import fuzs.vibrantparrots.common.init.ModRegistry;
import net.minecraft.core.Holder;

import java.util.Objects;

public class ModLanguageProvider extends AbstractLanguageProvider {

    public ModLanguageProvider(DataProviderContext context) {
        super(context);
    }

    public static ColorCollection<String> prefixWithColor(ColorCollection<String> ids) {
        return ColorCollection.zipMap(COLOR_NAMES, ids, (String color, String id) -> color + " " + id);
    }

    @Override
    public void addTranslations(TranslationBuilder translationBuilder) {
        translationBuilder.add(ModRegistry.CREATIVE_MODE_TAB.value(), VibrantParrots.MOD_NAME);
        translationBuilder.add(ModRegistry.PARROT_EGG_ENTITY_TYPE.value(), "Parrot Egg");
        translationBuilder.add(ModRegistry.BIRD_CAGE_ITEM.value(), "Bird Cage");
        translationBuilder.add(ModRegistry.PARROT_CAGE_ITEM.value(), "Cage of Parrot");
        ColorCollection.zipApply(ModRegistry.PARROT_EGG_ITEM,
                prefixWithColor(ColorCollection.create("Parrot Egg")),
                translationBuilder::addItem);
    }

    @Override
    protected boolean mustHaveTranslationKey(Holder.Reference<?> holder, String translationKey) {
        return !Objects.equals(holder.key(), ModRegistry.PARROT_ENTITY_TYPE.key());
    }
}
