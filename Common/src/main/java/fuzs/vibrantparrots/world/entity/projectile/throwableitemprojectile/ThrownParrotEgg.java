package fuzs.vibrantparrots.world.entity.projectile.throwableitemprojectile;

import com.mojang.datafixers.util.Either;
import fuzs.vibrantparrots.init.ModRegistry;
import fuzs.vibrantparrots.world.entity.animal.parrot.ModParrot;
import fuzs.vibrantparrots.world.entity.animal.parrot.ParrotVariant;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.parrot.Parrot;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;
import net.minecraft.world.item.EitherHolder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.function.Consumer;

/**
 * @see net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownEgg
 */
public class ThrownParrotEgg extends ThrowableItemProjectile {
    private static final EntityDimensions ZERO_SIZED_DIMENSIONS = EntityDimensions.fixed(0.0F, 0.0F);

    public ThrownParrotEgg(EntityType<? extends ThrownParrotEgg> entityType, Level level) {
        super(entityType, level);
    }

    public ThrownParrotEgg(Level level, LivingEntity owner, ItemStack item) {
        super(ModRegistry.PARROT_EGG_ENTITY_TYPE.value(), owner, level, item);
    }

    public ThrownParrotEgg(Level level, double x, double y, double z, ItemStack item) {
        super(ModRegistry.PARROT_EGG_ENTITY_TYPE.value(), x, y, z, level, item);
    }

    protected ParticleOptions getParticle() {
        return new ItemParticleOption(ParticleTypes.ITEM, this.getItem());
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 3) {
            ParticleOptions particleOptions = this.getParticle();
            for (int i = 0; i < 8; i++) {
                this.level()
                        .addParticle(particleOptions,
                                this.getX(),
                                this.getY(),
                                this.getZ(),
                                (this.random.nextFloat() - 0.5) * 0.08,
                                (this.random.nextFloat() - 0.5) * 0.08,
                                (this.random.nextFloat() - 0.5) * 0.08);
            }
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult hitResult) {
        super.onHitEntity(hitResult);
        hitResult.getEntity().hurt(this.damageSources().thrown(this, this.getOwner()), 0.0F);
    }

    @Override
    protected void onHit(HitResult hitResult) {
        super.onHit(hitResult);
        if (this.level() instanceof ServerLevel serverLevel) {
            Either<Parrot.Variant, EitherHolder<ParrotVariant>> either = this.getItem()
                    .get(ModRegistry.PARROT_VARIANT_DATA_COMPONENT_TYPE.value());
            if (either != null) {
                either.ifLeft((Parrot.Variant variant) -> {
                    this.spawnEggInhabitant(serverLevel, EntityType.PARROT, (Parrot parrot) -> {
                        parrot.setComponent(DataComponents.PARROT_VARIANT, variant);
                    });
                }).ifRight((EitherHolder<ParrotVariant> holder) -> {
                    this.spawnEggInhabitant(serverLevel, ModRegistry.PARROT_ENTITY_TYPE.value(), (ModParrot parrot) -> {
                        holder.unwrap(this.registryAccess()).ifPresent(parrot::setParrotVariant);
                    });
                });
            }

            serverLevel.broadcastEntityEvent(this, (byte) 3);
            this.discard();
        }
    }

    protected <T extends TamableAnimal> void spawnEggInhabitant(ServerLevel serverLevel, EntityType<T> entityType, Consumer<T> mobConsumer) {
        T mob = entityType.create(this.level(), EntitySpawnReason.TRIGGERED);
        if (mob != null) {
            mob.setAge(AgeableMob.BABY_START_AGE);
            mob.snapTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), 0.0F);
            if (this.getOwner() instanceof LivingEntity livingEntity) {
                mob.setOwner(livingEntity);
            }

            mobConsumer.accept(mob);
            if (mob.fudgePositionAfterSizeChange(ZERO_SIZED_DIMENSIONS)) {
                serverLevel.addFreshEntity(mob);
            }
        }
    }

    @Override
    protected Item getDefaultItem() {
        return Items.AIR;
    }
}
