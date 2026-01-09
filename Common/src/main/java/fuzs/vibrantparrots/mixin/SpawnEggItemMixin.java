package fuzs.vibrantparrots.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import fuzs.vibrantparrots.init.ModRegistry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SpawnEggItem.class)
abstract class SpawnEggItemMixin extends Item {

    public SpawnEggItemMixin(Properties properties) {
        super(properties);
    }

    @ModifyReturnValue(method = "spawnsEntity", at = @At("TAIL"))
    public boolean spawnsEntity(boolean spawnsEntity, ItemStack itemStack, EntityType<?> entityType) {
        return spawnsEntity
                || this.getType(itemStack) == EntityType.PARROT && entityType == ModRegistry.PARROT_ENTITY_TYPE.value();
    }

    @Shadow
    public abstract @Nullable EntityType<?> getType(ItemStack itemStack);
}
