package fuzs.vibrantparrots.neoforge.client;

import fuzs.puzzleslib.common.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.neoforge.api.data.v3.core.DataProviderBuilder;
import fuzs.vibrantparrots.common.VibrantParrots;
import fuzs.vibrantparrots.common.client.VibrantParrotsClient;
import fuzs.vibrantparrots.common.data.client.ModLanguageProvider;
import fuzs.vibrantparrots.common.data.client.ModModelProvider;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;

@Mod(value = VibrantParrots.MOD_ID, dist = Dist.CLIENT)
public class VibrantParrotsNeoForgeClient {

    public VibrantParrotsNeoForgeClient() {
        ClientModConstructor.construct(VibrantParrots.MOD_ID, VibrantParrotsClient::new);
        DataProviderBuilder.of(VibrantParrots.MOD_ID)
                .addProvider(ModLanguageProvider::new, ModModelProvider::new);
    }
}
