package fuzs.vibrantparrots.fabric.client;

import fuzs.vibrantparrots.common.VibrantParrots;
import fuzs.vibrantparrots.common.client.VibrantParrotsClient;
import fuzs.puzzleslib.common.api.client.core.v1.ClientModConstructor;
import net.fabricmc.api.ClientModInitializer;

public class VibrantParrotsFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientModConstructor.construct(VibrantParrots.MOD_ID, VibrantParrotsClient::new);
    }
}
