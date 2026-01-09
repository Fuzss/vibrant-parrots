package fuzs.vibrantparrots.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.vibrantparrots.client.model.geom.ModModelLayers;
import net.minecraft.client.model.AdultAndBabyModelPair;
import net.minecraft.client.model.animal.parrot.ParrotModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ParrotRenderer;
import net.minecraft.client.renderer.entity.state.ParrotRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;

public class VanillaParrotRenderer extends ParrotRenderer {
    private final AdultAndBabyModelPair<ParrotModel> models;

    public VanillaParrotRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.models = new AdultAndBabyModelPair<>(this.model,
                new ParrotModel(context.bakeLayer(ModModelLayers.PARROT_BABY)));
    }

    @Override
    public void submit(ParrotRenderState renderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {
        this.model = this.models.getModel(renderState.isBaby);
        super.submit(renderState, poseStack, submitNodeCollector, cameraRenderState);
    }
}
