package fuzs.vibrantparrots.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.vibrantparrots.client.model.geom.ModModelLayers;
import fuzs.vibrantparrots.client.renderer.entity.state.VibrantParrotRenderState;
import fuzs.vibrantparrots.world.entity.animal.parrot.VibrantParrot;
import fuzs.vibrantparrots.world.entity.animal.parrot.ParrotVariant;
import net.minecraft.client.model.AdultAndBabyModelPair;
import net.minecraft.client.model.animal.parrot.ParrotModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ParrotRenderer;
import net.minecraft.client.renderer.entity.state.ParrotRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.animal.parrot.Parrot;

public class VibrantParrotRenderer extends ParrotRenderer {
    private final AdultAndBabyModelPair<ParrotModel> models;

    public VibrantParrotRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.models = new AdultAndBabyModelPair<>(new ParrotModel(context.bakeLayer(ModModelLayers.PARROT)),
                new ParrotModel(context.bakeLayer(ModModelLayers.PARROT_BABY)));
    }

    @Override
    public Identifier getTextureLocation(ParrotRenderState parrotRenderState) {
        ParrotVariant variant = ((VibrantParrotRenderState) parrotRenderState).variant;
        return variant == null ? MissingTextureAtlasSprite.getLocation() : variant.assetInfo().texturePath();
    }

    @Override
    public ParrotRenderState createRenderState() {
        return new VibrantParrotRenderState();
    }

    @Override
    public void extractRenderState(Parrot parrot, ParrotRenderState parrotRenderState, float partialTick) {
        super.extractRenderState(parrot, parrotRenderState, partialTick);
        ((VibrantParrotRenderState) parrotRenderState).variant = ((VibrantParrot) parrot).getParrotVariant().value();
    }

    @Override
    public void submit(ParrotRenderState renderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {
        this.model = this.models.getModel(renderState.isBaby);
        super.submit(renderState, poseStack, submitNodeCollector, cameraRenderState);
    }
}
