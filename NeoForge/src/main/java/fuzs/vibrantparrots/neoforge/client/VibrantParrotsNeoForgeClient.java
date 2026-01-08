package fuzs.vibrantparrots.neoforge.client;

import fuzs.vibrantparrots.VibrantParrots;
import fuzs.vibrantparrots.client.VibrantParrotsClient;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;

@Mod(value = VibrantParrots.MOD_ID, dist = Dist.CLIENT)
public class VibrantParrotsNeoForgeClient {

    public VibrantParrotsNeoForgeClient() {
        ClientModConstructor.construct(VibrantParrots.MOD_ID, VibrantParrotsClient::new);
    }
}
