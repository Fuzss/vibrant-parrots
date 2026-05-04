package fuzs.vibrantparrots.common.client.renderer.entity.state;

import fuzs.vibrantparrots.common.world.entity.animal.parrot.ParrotVariant;
import net.minecraft.client.renderer.entity.state.ParrotRenderState;
import org.jspecify.annotations.Nullable;

public class VibrantParrotRenderState extends ParrotRenderState {
    @Nullable
    public ParrotVariant variant;
}
