package fuzs.vibrantparrots;

import fuzs.puzzleslib.api.core.v1.ModConstructor;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VibrantParrots implements ModConstructor {
    public static final String MOD_ID = "vibrantparrots";
    public static final String MOD_NAME = "Vibrant Parrots";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }
}
