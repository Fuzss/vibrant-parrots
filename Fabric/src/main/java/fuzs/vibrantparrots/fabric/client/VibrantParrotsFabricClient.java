package fuzs.vibrantparrots.fabric.client;

import fuzs.vibrantparrots.VibrantParrots;
import fuzs.vibrantparrots.client.VibrantParrotsClient;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import net.fabricmc.api.ClientModInitializer;

public class VibrantParrotsFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientModConstructor.construct(VibrantParrots.MOD_ID, VibrantParrotsClient::new);
    }
}
