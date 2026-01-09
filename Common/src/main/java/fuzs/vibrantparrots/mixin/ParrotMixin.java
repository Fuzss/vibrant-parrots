package fuzs.vibrantparrots.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.parrot.Parrot;
import net.minecraft.world.entity.animal.parrot.ShoulderRidingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Parrot.class)
abstract class ParrotMixin extends ShoulderRidingEntity {

    protected ParrotMixin(EntityType<? extends ShoulderRidingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @ModifyReturnValue(method = "isBaby", at = @At("RETURN"))
    public boolean isBaby(boolean isBaby) {
        return this.getAge() < DEFAULT_AGE;
    }

    @ModifyReturnValue(method = "isFood", at = @At("RETURN"))
    public boolean isFood(boolean isFood, ItemStack itemStack) {
        return itemStack.is(ItemTags.PARROT_FOOD);
    }
}
