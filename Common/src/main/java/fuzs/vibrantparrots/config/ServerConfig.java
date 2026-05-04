package fuzs.vibrantparrots.config;

import fuzs.puzzleslib.common.api.config.v3.Config;
import fuzs.puzzleslib.common.api.config.v3.ConfigCore;
import net.minecraft.util.RandomSource;
import net.minecraft.util.TimeUtil;

public class ServerConfig implements ConfigCore {
    @Config(description = "Keep all parrots sitting on a player's shoulders when that player is falling.")
    public boolean parrotsStayWhenFalling = true;
    @Config(description = "Keep all parrots sitting on a player's shoulders when that player is flying in creative mode.")
    public boolean parrotsStayWhenFlying = true;
    @Config(description = "Keep all parrots sitting on a player's shoulders when that player is touching water. Only eject the parrots when the player is fully submerged.")
    public boolean dismountParrotsOnlyWhenSubmerged = true;
    @Config(description = "Remove all parrots sitting on a player's shoulders when that player is sneaking.")
    public boolean dismountParrotsWhenSneaking = true;
    @Config(description = "The minimum time after breeding two parrots after which one of them will lay an egg for hatching a parrot chick.")
    @Config.IntRange(min = 0)
    public int minEggLayTimeInMinutes = 10;
    @Config(description = "The maximum time after breeding two parrots after which one of them will lay an egg for hatching a parrot chick.")
    @Config.IntRange(min = 0)
    public int maxEggLayTimeInMinutes = 20;

    public int sampleEggLayTime(RandomSource randomSource) {
        return TimeUtil.rangeOfSeconds(this.minEggLayTimeInMinutes * TimeUtil.SECONDS_PER_MINUTE,
                (this.maxEggLayTimeInMinutes < this.minEggLayTimeInMinutes ? this.minEggLayTimeInMinutes :
                        this.maxEggLayTimeInMinutes) * TimeUtil.SECONDS_PER_MINUTE).sample(randomSource);
    }
}
