package fuzs.vibrantparrots.common.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.authlib.GameProfile;
import fuzs.vibrantparrots.common.VibrantParrots;
import fuzs.vibrantparrots.common.config.ServerConfig;
import fuzs.vibrantparrots.common.init.ModRegistry;
import fuzs.vibrantparrots.common.world.entity.animal.parrot.ParrotVariant;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayer.class)
abstract class ServerPlayerMixin extends Player {

    public ServerPlayerMixin(Level level, GameProfile gameProfile) {
        super(level, gameProfile);
    }

    @Inject(method = "setEntityOnShoulder", at = @At("HEAD"), cancellable = true)
    public void setEntityOnShoulder(CallbackInfoReturnable<Boolean> callback) {
        if (!VibrantParrots.CONFIG.get(ServerConfig.class).dismountParrotsWhenSneaking) {
            return;
        }

        if (this.isSecondaryUseActive()) {
            callback.setReturnValue(false);
        }
    }

    @ModifyExpressionValue(method = "handleShoulderEntities",
                           at = @At(value = "FIELD",
                                    target = "Lnet/minecraft/server/level/ServerPlayer;fallDistance:D",
                                    opcode = Opcodes.GETFIELD))
    public double handleShoulderEntities$0(double fallDistance) {
        if (!VibrantParrots.CONFIG.get(ServerConfig.class).parrotsStayWhenFalling) {
            return fallDistance;
        } else {
            return 0.0;
        }
    }

    @ModifyExpressionValue(method = "handleShoulderEntities",
                           at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;isInWater()Z"))
    public boolean handleShoulderEntities$1(boolean isInWater) {
        if (!VibrantParrots.CONFIG.get(ServerConfig.class).dismountParrotsOnlyWhenSubmerged) {
            return isInWater;
        } else {
            return isInWater && !this.isInShallowWater();
        }
    }

    @ModifyExpressionValue(method = "handleShoulderEntities",
                           at = @At(value = "FIELD",
                                    target = "Lnet/minecraft/world/entity/player/Abilities;flying:Z",
                                    opcode = Opcodes.GETFIELD))
    public boolean handleShoulderEntities$2(boolean flying) {
        if (!VibrantParrots.CONFIG.get(ServerConfig.class).parrotsStayWhenFlying) {
            return flying;
        }

        return false;
    }

    @Inject(method = "handleShoulderEntities", at = @At("TAIL"))
    public void handleShoulderEntities$3(CallbackInfo callback) {
        if (!VibrantParrots.CONFIG.get(ServerConfig.class).dismountParrotsWhenSneaking) {
            return;
        }

        if (this.isCrouching()) {
            this.removeEntitiesOnShoulder();
        }
    }

    @Inject(method = "setShoulderEntityLeft", at = @At("TAIL"))
    protected void setShoulderEntityLeft(CompoundTag compoundTag, CallbackInfo callback) {
        ModRegistry.LEFT_SHOULDER_PARROT_ATTACHMENT_TYPE.set(this,
                ParrotVariant.extractParrotVariant(this, compoundTag));
    }

    @Inject(method = "setShoulderEntityRight", at = @At("TAIL"))
    protected void setShoulderEntityRight(CompoundTag compoundTag, CallbackInfo callback) {
        ModRegistry.RIGHT_SHOULDER_PARROT_ATTACHMENT_TYPE.set(this,
                ParrotVariant.extractParrotVariant(this, compoundTag));
    }
}
