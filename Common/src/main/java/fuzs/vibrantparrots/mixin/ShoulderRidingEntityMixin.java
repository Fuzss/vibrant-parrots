package fuzs.vibrantparrots.mixin;

import fuzs.vibrantparrots.VibrantParrots;
import fuzs.vibrantparrots.config.ServerConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.parrot.ShoulderRidingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ShoulderRidingEntity.class)
abstract class ShoulderRidingEntityMixin extends TamableAnimal {

    protected ShoulderRidingEntityMixin(EntityType<? extends TamableAnimal> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "setEntityOnShoulder",
            at = @At(value = "INVOKE",
                     target = "Lnet/minecraft/world/entity/animal/parrot/ShoulderRidingEntity;discard()V"))
    public void setEntityOnShoulder(ServerPlayer serverPlayer, CallbackInfoReturnable<Boolean> callbackInfo) {
        if (VibrantParrots.CONFIG.get(ServerConfig.class).dismountParrotsWhenSneaking) {
            Component component = Component.translatable("mount.onboard", Component.keybind("key.sneak"));
            serverPlayer.sendOverlayMessage(component);
        }
    }
}
