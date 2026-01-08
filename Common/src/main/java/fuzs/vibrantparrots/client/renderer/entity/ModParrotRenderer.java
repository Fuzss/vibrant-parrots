package fuzs.vibrantparrots.client.renderer.entity;

import fuzs.vibrantparrots.client.renderer.entity.state.ModParrotRenderState;
import fuzs.vibrantparrots.world.entity.animal.parrot.ModParrot;
import fuzs.vibrantparrots.world.entity.animal.parrot.ParrotVariant;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ParrotRenderer;
import net.minecraft.client.renderer.entity.state.ParrotRenderState;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.animal.parrot.Parrot;

public class ModParrotRenderer extends ParrotRenderer {

    public ModParrotRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public Identifier getTextureLocation(ParrotRenderState parrotRenderState) {
        ParrotVariant variant = ((ModParrotRenderState) parrotRenderState).variant;
        return variant == null ? MissingTextureAtlasSprite.getLocation() : variant.assetInfo().texturePath();
    }

    @Override
    public ParrotRenderState createRenderState() {
        return new ModParrotRenderState();
    }

    @Override
    public void extractRenderState(Parrot parrot, ParrotRenderState parrotRenderState, float partialTick) {
        super.extractRenderState(parrot, parrotRenderState, partialTick);
        ((ModParrotRenderState) parrotRenderState).variant = ((ModParrot) parrot).getParrotVariant().value();
    }
}
