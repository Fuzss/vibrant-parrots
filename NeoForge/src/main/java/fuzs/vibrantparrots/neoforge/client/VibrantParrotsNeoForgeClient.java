package fuzs.vibrantparrots.neoforge.client;

import fuzs.puzzleslib.common.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.neoforge.api.data.v2.core.DataProviderHelper;
import fuzs.vibrantparrots.VibrantParrots;
import fuzs.vibrantparrots.client.VibrantParrotsClient;
import fuzs.vibrantparrots.data.client.ModLanguageProvider;
import fuzs.vibrantparrots.data.client.ModModelProvider;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;

@Mod(value = VibrantParrots.MOD_ID, dist = Dist.CLIENT)
public class VibrantParrotsNeoForgeClient {

    public VibrantParrotsNeoForgeClient() {
        ClientModConstructor.construct(VibrantParrots.MOD_ID, VibrantParrotsClient::new);
        DataProviderHelper.registerDataProviders(VibrantParrots.MOD_ID,
                ModLanguageProvider::new,
                ModModelProvider::new);
    }
}
