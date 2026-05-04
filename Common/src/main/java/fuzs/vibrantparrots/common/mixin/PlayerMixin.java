package fuzs.vibrantparrots.common.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import fuzs.vibrantparrots.common.init.ModRegistry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Player.class)
abstract class PlayerMixin extends LivingEntity {

    protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @WrapWithCondition(method = "hurtServer",
                       at = @At(value = "INVOKE",
                                target = "Lnet/minecraft/world/entity/player/Player;removeEntitiesOnShoulder()V"))
    public boolean hurtServer(Player player, ServerLevel serverLevel, DamageSource damageSource, float damageAmount) {
        return damageSource.is(ModRegistry.DISMOUNTS_PARROTS_DAMAGE_TYPE_TAG);
    }
}
