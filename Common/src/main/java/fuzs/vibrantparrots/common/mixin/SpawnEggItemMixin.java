package fuzs.vibrantparrots.common.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import fuzs.vibrantparrots.common.init.ModRegistry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
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
    private static boolean spawnsEntity(boolean spawnsEntity, ItemStack itemStack, EntityType<?> entityType) {
        return spawnsEntity
                || getType(itemStack) == EntityTypes.PARROT && entityType == ModRegistry.PARROT_ENTITY_TYPE.value();
    }

    @Shadow
    private static @Nullable EntityType<?> getType(ItemStack itemStack) {
        throw new UnsupportedOperationException();
    }
}
