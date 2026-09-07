package fuzs.vibrantparrots.common.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.puzzleslib.api.client.renderer.v1.model.AdultAndBabyModelPair;
import fuzs.vibrantparrots.common.client.model.geom.ModModelLayers;
import fuzs.vibrantparrots.common.world.entity.animal.parrot.VibrantParrot;
import net.minecraft.client.model.ParrotModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ParrotRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Parrot;

public class VibrantParrotRenderer extends ParrotRenderer {
    private final AdultAndBabyModelPair<ParrotModel> models;

    public VibrantParrotRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.models = new AdultAndBabyModelPair<>(this.model,
                new ParrotModel(context.bakeLayer(ModModelLayers.PARROT_BABY)));
    }

    @Override
    public ResourceLocation getTextureLocation(Parrot parrot) {
        if (parrot instanceof VibrantParrot) {
            return ((VibrantParrot) parrot).getParrotVariant().value().assetInfo().texturePath();
        } else {
            return super.getTextureLocation(parrot);
        }
    }

    @Override
    public void render(Parrot parrot, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        this.model = this.models.getModel(parrot.isBaby());
        super.render(parrot, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }
}
