package fuzs.vibrantparrots.client;

import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.api.client.core.v1.context.EntityRenderersContext;
import fuzs.vibrantparrots.client.renderer.entity.ModParrotRenderer;
import fuzs.vibrantparrots.init.ModRegistry;

public class VibrantParrotsClient implements ClientModConstructor {

    @Override
    public void onRegisterEntityRenderers(EntityRenderersContext context) {
        context.registerEntityRenderer(ModRegistry.PARROT_ENTITY_TYPE.value(), ModParrotRenderer::new);
    }
}
