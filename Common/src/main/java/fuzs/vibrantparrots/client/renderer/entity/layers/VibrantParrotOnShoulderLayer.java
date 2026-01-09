package fuzs.vibrantparrots.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.puzzleslib.api.client.renderer.v1.RenderStateExtraData;
import fuzs.vibrantparrots.VibrantParrots;
import fuzs.vibrantparrots.client.model.geom.ModModelLayers;
import fuzs.vibrantparrots.init.ModRegistry;
import fuzs.vibrantparrots.world.entity.animal.parrot.ParrotVariant;
import net.minecraft.client.model.animal.parrot.ParrotModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.ParrotRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Holder;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import java.util.Optional;

/**
 * @see net.minecraft.client.renderer.entity.layers.ParrotOnShoulderLayer
 */
public class VibrantParrotOnShoulderLayer extends RenderLayer<AvatarRenderState, PlayerModel> {
    public static final ContextKey<Optional<Holder<ParrotVariant>>> PARROT_ON_LEFT_SHOULDER_KEY = new ContextKey<>(
            VibrantParrots.id("parrot_on_left_shoulder"));
    public static final ContextKey<Optional<Holder<ParrotVariant>>> PARROT_ON_RIGHT_SHOULDER_KEY = new ContextKey<>(
            VibrantParrots.id("parrot_on_right_shoulder"));

    private final ParrotModel model;

    public VibrantParrotOnShoulderLayer(RenderLayerParent<AvatarRenderState, PlayerModel> renderer, EntityModelSet modelSet) {
        super(renderer);
        this.model = new ParrotModel(modelSet.bakeLayer(ModModelLayers.PARROT));
    }

    public static void addLivingEntityRenderLayers(EntityType<?> entityType, LivingEntityRenderer<?, ?, ?> entityRenderer, EntityRendererProvider.Context context) {
        if (entityRenderer instanceof AvatarRenderer<?> avatarRenderer) {
            avatarRenderer.addLayer(new VibrantParrotOnShoulderLayer(avatarRenderer, context.getModelSet()));
        }
    }

    public static void onExtractRenderState(Entity entity, EntityRenderState renderState, float partialTick) {
        if (entity instanceof Avatar && renderState instanceof AvatarRenderState avatarRenderState) {
            RenderStateExtraData.set(renderState,
                    PARROT_ON_LEFT_SHOULDER_KEY,
                    ModRegistry.LEFT_SHOULDER_PARROT_ATTACHMENT_TYPE.getOrDefault(entity, Optional.empty()));
            RenderStateExtraData.set(renderState,
                    PARROT_ON_RIGHT_SHOULDER_KEY,
                    ModRegistry.RIGHT_SHOULDER_PARROT_ATTACHMENT_TYPE.getOrDefault(entity, Optional.empty()));
        }
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int packedLight, AvatarRenderState avatarRenderState, float yRot, float xRot) {
        RenderStateExtraData.getOrDefault(avatarRenderState, PARROT_ON_LEFT_SHOULDER_KEY, Optional.empty())
                .ifPresent((Holder<ParrotVariant> holder) -> {
                    this.submitOnShoulder(poseStack,
                            submitNodeCollector,
                            packedLight,
                            avatarRenderState,
                            holder,
                            yRot,
                            xRot,
                            true);
                });
        RenderStateExtraData.getOrDefault(avatarRenderState, PARROT_ON_RIGHT_SHOULDER_KEY, Optional.empty())
                .ifPresent((Holder<ParrotVariant> holder) -> {
                    this.submitOnShoulder(poseStack,
                            submitNodeCollector,
                            packedLight,
                            avatarRenderState,
                            holder,
                            yRot,
                            xRot,
                            false);
                });
    }

    private void submitOnShoulder(PoseStack poseStack, SubmitNodeCollector nodeCollector, int packedLight, AvatarRenderState renderState, Holder<ParrotVariant> variant, float yRot, float xRot, boolean isLeft) {
        poseStack.pushPose();
        poseStack.translate(isLeft ? 0.4F : -0.4F, renderState.isCrouching ? -1.3F : -1.5F, 0.0F);
        ParrotRenderState parrotRenderState = new ParrotRenderState();
        parrotRenderState.pose = ParrotModel.Pose.ON_SHOULDER;
        parrotRenderState.ageInTicks = renderState.ageInTicks;
        parrotRenderState.walkAnimationPos = renderState.walkAnimationPos;
        parrotRenderState.walkAnimationSpeed = renderState.walkAnimationSpeed;
        parrotRenderState.yRot = yRot;
        parrotRenderState.xRot = xRot;
        nodeCollector.submitModel(this.model,
                parrotRenderState,
                poseStack,
                this.model.renderType(variant.value().assetInfo().texturePath()),
                packedLight,
                OverlayTexture.NO_OVERLAY,
                renderState.outlineColor,
                null);
        poseStack.popPose();
    }
}
