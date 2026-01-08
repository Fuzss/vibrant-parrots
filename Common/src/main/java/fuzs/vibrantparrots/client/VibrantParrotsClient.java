package fuzs.vibrantparrots.client;

import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.api.client.core.v1.context.EntityRenderersContext;
import fuzs.puzzleslib.api.client.event.v1.renderer.AddLivingEntityRenderLayersCallback;
import fuzs.puzzleslib.api.client.event.v1.renderer.ExtractRenderStateCallback;
import fuzs.vibrantparrots.client.renderer.entity.ModParrotRenderer;
import fuzs.vibrantparrots.client.renderer.entity.layers.ModParrotOnShoulderLayer;
import fuzs.vibrantparrots.init.ModRegistry;

public class VibrantParrotsClient implements ClientModConstructor {

    @Override
    public void onConstructMod() {
        registerEventHandlers();
    }

    private static void registerEventHandlers() {
        AddLivingEntityRenderLayersCallback.EVENT.register(ModParrotOnShoulderLayer::addLivingEntityRenderLayers);
        ExtractRenderStateCallback.EVENT.register(ModParrotOnShoulderLayer::onExtractRenderState);
    }

    @Override
    public void onRegisterEntityRenderers(EntityRenderersContext context) {
        context.registerEntityRenderer(ModRegistry.PARROT_ENTITY_TYPE.value(), ModParrotRenderer::new);
    }
}
