package fuzs.vibrantparrots.config;

import fuzs.puzzleslib.api.config.v3.Config;
import fuzs.puzzleslib.api.config.v3.ConfigCore;

public class ServerConfig implements ConfigCore {
    @Config(description = "Remove all parrots sitting on a player's shoulders when that player is sneaking.")
    public boolean dismountParrotsWhenSneaking = true;
}
