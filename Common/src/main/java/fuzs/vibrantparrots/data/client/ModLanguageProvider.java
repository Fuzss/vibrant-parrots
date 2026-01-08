package fuzs.vibrantparrots.data.client;

import fuzs.puzzleslib.api.client.data.v2.AbstractLanguageProvider;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import fuzs.vibrantparrots.init.ModRegistry;
import net.minecraft.core.Holder;

import java.util.Objects;

public class ModLanguageProvider extends AbstractLanguageProvider {

    public ModLanguageProvider(DataProviderContext context) {
        super(context);
    }

    @Override
    public void addTranslations(TranslationBuilder translationBuilder) {

    }

    @Override
    protected boolean mustHaveTranslationKey(Holder.Reference<?> holder, String translationKey) {
        return !Objects.equals(holder.key(), ModRegistry.PARROT_ENTITY_TYPE.key());
    }
}
