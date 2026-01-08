package fuzs.vibrantparrots.neoforge;

import fuzs.vibrantparrots.VibrantParrots;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import net.neoforged.fml.common.Mod;

@Mod(VibrantParrots.MOD_ID)
public class VibrantParrotsNeoForge {

    public VibrantParrotsNeoForge() {
        ModConstructor.construct(VibrantParrots.MOD_ID, VibrantParrots::new);
    }
}
