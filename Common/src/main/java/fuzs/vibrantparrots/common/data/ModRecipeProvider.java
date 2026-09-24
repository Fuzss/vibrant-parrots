package fuzs.vibrantparrots.common.data;

import fuzs.puzzleslib.common.api.data.v3.core.DataProviderContext;
import fuzs.puzzleslib.common.api.data.v3.recipes.AbstractRecipeProvider;
import fuzs.vibrantparrots.common.init.ModRegistry;
import net.minecraft.advancements.Advancement;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.Blocks;

public class ModRecipeProvider extends AbstractRecipeProvider {

    public ModRecipeProvider(BootstrapContext<Recipe<?>> recipeOutput, BootstrapContext<Advancement> advancementOutput) {
        super(recipeOutput, advancementOutput);
    }

    @Override
    public void buildRecipes() {
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.MISC, ModRegistry.BIRD_CAGE_ITEM.value())
                .define('#', Blocks.IRON_BARS)
                .define('X', Blocks.IRON_TRAPDOOR)
                .pattern(" # ")
                .pattern("# #")
                .pattern("#X#")
                .unlockedBy(getHasName(Blocks.IRON_BARS), this.has(Blocks.IRON_BARS))
                .save(this.output);
    }
}
