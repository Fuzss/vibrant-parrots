package fuzs.vibrantparrots.neoforge.init;

import fuzs.puzzleslib.api.init.v3.registry.RegistryManager;
import fuzs.vibrantparrots.common.VibrantParrots;
import fuzs.vibrantparrots.common.init.ModRegistry;
import fuzs.vibrantparrots.common.world.item.ParrotCageItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class NeoForgeModRegistry {
    static final RegistryManager REGISTRIES = RegistryManager.from(VibrantParrots.MOD_ID);
    public static final Holder.Reference<Item> PARROT_CAGE_ITEM = REGISTRIES.registerItem("parrot_cage",
            (Item.Properties properties) -> {
                return new ParrotCageItem(properties) {
                    @Override
                    public boolean emptyContents(@Nullable Player player, Level level, BlockPos blockPos, @Nullable BlockHitResult hitResult, @Nullable ItemStack itemStack) {
                        return true;
                    }
                };
            },
            ModRegistry::parrotCageProperties);

    public static void bootstrap() {
        // NO-OP
    }
}
