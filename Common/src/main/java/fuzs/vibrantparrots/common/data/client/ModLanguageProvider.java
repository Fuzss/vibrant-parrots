package fuzs.vibrantparrots.common.data.client;

import fuzs.puzzleslib.common.api.client.data.v2.AbstractLanguageProvider;
import fuzs.puzzleslib.common.api.data.v2.core.DataProviderContext;
import fuzs.vibrantparrots.common.VibrantParrots;
import fuzs.vibrantparrots.common.init.ModRegistry;
import net.minecraft.core.Holder;

import java.util.Objects;

public class ModLanguageProvider extends AbstractLanguageProvider {

    public ModLanguageProvider(DataProviderContext context) {
        super(context);
    }

    @Override
    public void addTranslations(TranslationBuilder translationBuilder) {
        translationBuilder.add(ModRegistry.CREATIVE_MODE_TAB.value(), VibrantParrots.MOD_NAME);
        translationBuilder.add(ModRegistry.PARROT_EGG_ENTITY_TYPE.value(), "Parrot Egg");
        translationBuilder.add(ModRegistry.CAGE_ITEM.value(), "Cage");
        translationBuilder.add(ModRegistry.PARROT_CAGE_ITEM.value(), "Parrot Cage");
        translationBuilder.add(ModRegistry.WHITE_PARROT_EGG_ITEM.value(), "White Parrot Egg");
        translationBuilder.add(ModRegistry.ORANGE_PARROT_EGG_ITEM.value(), "Orange Parrot Egg");
        translationBuilder.add(ModRegistry.MAGENTA_PARROT_EGG_ITEM.value(), "Magenta Parrot Egg");
        translationBuilder.add(ModRegistry.LIGHT_BLUE_PARROT_EGG_ITEM.value(), "Light Blue Parrot Egg");
        translationBuilder.add(ModRegistry.YELLOW_PARROT_EGG_ITEM.value(), "Yellow Parrot Egg");
        translationBuilder.add(ModRegistry.LIME_PARROT_EGG_ITEM.value(), "Lime Parrot Egg");
        translationBuilder.add(ModRegistry.PINK_PARROT_EGG_ITEM.value(), "Pink Parrot Egg");
        translationBuilder.add(ModRegistry.GRAY_PARROT_EGG_ITEM.value(), "Gray Parrot Egg");
        translationBuilder.add(ModRegistry.LIGHT_GRAY_PARROT_EGG_ITEM.value(), "Light Gray Parrot Egg");
        translationBuilder.add(ModRegistry.CYAN_PARROT_EGG_ITEM.value(), "Cyan Parrot Egg");
        translationBuilder.add(ModRegistry.PURPLE_PARROT_EGG_ITEM.value(), "Purple Parrot Egg");
        translationBuilder.add(ModRegistry.BLUE_PARROT_EGG_ITEM.value(), "Blue Parrot Egg");
        translationBuilder.add(ModRegistry.BROWN_PARROT_EGG_ITEM.value(), "Brown Parrot Egg");
        translationBuilder.add(ModRegistry.GREEN_PARROT_EGG_ITEM.value(), "Green Parrot Egg");
        translationBuilder.add(ModRegistry.RED_PARROT_EGG_ITEM.value(), "Red Parrot Egg");
        translationBuilder.add(ModRegistry.BLACK_PARROT_EGG_ITEM.value(), "Black Parrot Egg");
    }

    @Override
    protected boolean mustHaveTranslationKey(Holder.Reference<?> holder, String translationKey) {
        return !Objects.equals(holder.key(), ModRegistry.PARROT_ENTITY_TYPE.key());
    }
}
