package fuzs.vibrantparrots.common.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import fuzs.vibrantparrots.common.init.ModRegistry;
import fuzs.vibrantparrots.common.world.entity.animal.parrot.ParrotVariant;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.ParrotOnShoulderLayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;

/**
 * @see net.minecraft.client.renderer.entity.layers.ParrotOnShoulderLayer
 */
public class VibrantParrotOnShoulderLayer<T extends Player> extends ParrotOnShoulderLayer<T> {

    public VibrantParrotOnShoulderLayer(RenderLayerParent<T, PlayerModel<T>> renderer, EntityModelSet modelSet) {
        super(renderer, modelSet);
    }

    public static void addLivingEntityRenderLayers(EntityType<?> entityType, LivingEntityRenderer<?, ?> entityRenderer, EntityRendererProvider.Context context) {
        if (entityRenderer instanceof PlayerRenderer avatarRenderer) {
            avatarRenderer.addLayer(new VibrantParrotOnShoulderLayer<>(avatarRenderer, context.getModelSet()));
        }
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, T livingEntity, float limbSwing, float limbSwingAmount, float netHeadYaw, float headPitch, boolean leftShoulder) {
        this.getShoulderParrot(livingEntity, leftShoulder).ifPresent((Holder<ParrotVariant> variant) -> {
            poseStack.pushPose();
            poseStack.translate(leftShoulder ? 0.4F : -0.4F, livingEntity.isCrouching() ? -1.3F : -1.5F, 0.0F);
            VertexConsumer vertexconsumer = buffer.getBuffer(this.model.renderType(variant.value()
                    .assetInfo()
                    .texturePath()));
            this.model.renderOnShoulder(poseStack,
                    vertexconsumer,
                    packedLight,
                    OverlayTexture.NO_OVERLAY,
                    limbSwing,
                    limbSwingAmount,
                    netHeadYaw,
                    headPitch,
                    livingEntity.tickCount);
            poseStack.popPose();
        });
    }

    private Optional<Holder<ParrotVariant>> getShoulderParrot(T livingEntity, boolean leftShoulder) {
        return (leftShoulder ? ModRegistry.LEFT_SHOULDER_PARROT_ATTACHMENT_TYPE :
                ModRegistry.RIGHT_SHOULDER_PARROT_ATTACHMENT_TYPE).getOrDefault(livingEntity, Optional.empty());
    }
}
