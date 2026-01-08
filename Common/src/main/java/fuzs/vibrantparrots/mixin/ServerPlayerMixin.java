package fuzs.vibrantparrots.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.authlib.GameProfile;
import fuzs.vibrantparrots.VibrantParrots;
import fuzs.vibrantparrots.config.ServerConfig;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerPlayer.class)
abstract class ServerPlayerMixin extends Player {

    public ServerPlayerMixin(Level level, GameProfile gameProfile) {
        super(level, gameProfile);
    }

    @ModifyExpressionValue(method = "handleShoulderEntities",
                           at = @At(value = "FIELD",
                                    target = "Lnet/minecraft/server/level/ServerPlayer;fallDistance:D",
                                    opcode = Opcodes.GETFIELD))
    public double handleShoulderEntities$0(double fallDistance) {
        return 0.0;
    }

    @ModifyExpressionValue(method = "handleShoulderEntities",
                           at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;isInWater()Z"))
    public boolean handleShoulderEntities$1(boolean isInWater) {
        return isInWater && !this.isInShallowWater();
    }

    @ModifyExpressionValue(method = "handleShoulderEntities",
                           at = @At(value = "FIELD",
                                    target = "Lnet/minecraft/world/entity/player/Abilities;flying:Z",
                                    opcode = Opcodes.GETFIELD))
    public boolean handleShoulderEntities$2(boolean flying) {
        return VibrantParrots.CONFIG.get(ServerConfig.class).dismountParrotsWhenSneaking && this.isCrouching();
    }
}
