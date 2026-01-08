package fuzs.vibrantparrots.client.model.geom;

import fuzs.puzzleslib.api.client.init.v1.ModelLayerFactory;
import fuzs.vibrantparrots.VibrantParrots;
import net.minecraft.client.model.geom.ModelLayerLocation;

public class ModModelLayers {
    static final ModelLayerFactory MODEL_LAYERS = ModelLayerFactory.from(VibrantParrots.MOD_ID);
    public static final ModelLayerLocation PARROT = MODEL_LAYERS.registerModelLayer("parrot");
    public static final ModelLayerLocation PARROT_BABY = MODEL_LAYERS.registerModelLayer("parrot_baby");
}
