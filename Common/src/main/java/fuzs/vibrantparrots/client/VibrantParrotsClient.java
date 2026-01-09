package fuzs.vibrantparrots.client;

import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.api.client.core.v1.context.EntityRenderersContext;
import fuzs.puzzleslib.api.client.core.v1.context.LayerDefinitionsContext;
import fuzs.puzzleslib.api.client.event.v1.renderer.AddLivingEntityRenderLayersCallback;
import fuzs.puzzleslib.api.client.event.v1.renderer.ExtractRenderStateCallback;
import fuzs.vibrantparrots.client.model.geom.ModModelLayers;
import fuzs.vibrantparrots.client.renderer.entity.VibrantParrotRenderer;
import fuzs.vibrantparrots.client.renderer.entity.VanillaParrotRenderer;
import fuzs.vibrantparrots.client.renderer.entity.layers.VibrantParrotOnShoulderLayer;
import fuzs.vibrantparrots.init.ModRegistry;
import net.minecraft.client.model.animal.parrot.ParrotModel;
import net.minecraft.client.model.geom.builders.MeshTransformer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.world.entity.EntityType;

public class VibrantParrotsClient implements ClientModConstructor {

    @Override
    public void onConstructMod() {
        registerEventHandlers();
    }

    private static void registerEventHandlers() {
        AddLivingEntityRenderLayersCallback.EVENT.register(VibrantParrotOnShoulderLayer::addLivingEntityRenderLayers);
        ExtractRenderStateCallback.EVENT.register(VibrantParrotOnShoulderLayer::onExtractRenderState);
    }

    @Override
    public void onRegisterEntityRenderers(EntityRenderersContext context) {
        context.registerEntityRenderer(EntityType.PARROT, VanillaParrotRenderer::new);
        context.registerEntityRenderer(ModRegistry.PARROT_ENTITY_TYPE.value(), VibrantParrotRenderer::new);
        context.registerEntityRenderer(ModRegistry.PARROT_EGG_ENTITY_TYPE.value(), ThrownItemRenderer::new);
    }

    @Override
    public void onRegisterLayerDefinitions(LayerDefinitionsContext context) {
        context.registerLayerDefinition(ModModelLayers.PARROT, ParrotModel::createBodyLayer);
        context.registerLayerDefinition(ModModelLayers.PARROT_BABY,
                () -> ParrotModel.createBodyLayer().apply(MeshTransformer.scaling(0.5F)));
    }
}
