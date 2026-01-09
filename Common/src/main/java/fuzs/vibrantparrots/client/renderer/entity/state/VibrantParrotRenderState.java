package fuzs.vibrantparrots.client.renderer.entity.state;

import fuzs.vibrantparrots.world.entity.animal.parrot.ParrotVariant;
import net.minecraft.client.renderer.entity.state.ParrotRenderState;
import org.jspecify.annotations.Nullable;

public class VibrantParrotRenderState extends ParrotRenderState {
    @Nullable
    public ParrotVariant variant;
}
