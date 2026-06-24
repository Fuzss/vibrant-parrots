package fuzs.vibrantparrots.common.data;

import fuzs.puzzleslib.common.api.data.v2.AbstractRecipeProvider;
import fuzs.puzzleslib.common.api.data.v2.core.DataProviderContext;
import fuzs.vibrantparrots.common.init.ModRegistry;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.level.block.Blocks;

public class ModRecipeProvider extends AbstractRecipeProvider {

    public ModRecipeProvider(DataProviderContext context) {
        super(context);
    }

    @Override
    public void addRecipes(RecipeOutput recipeOutput) {
        ShapedRecipeBuilder.shaped(this.items(), RecipeCategory.MISC, ModRegistry.BIRD_CAGE_ITEM.value())
                .define('#', Blocks.IRON_BARS)
                .define('X', Blocks.IRON_TRAPDOOR)
                .pattern(" # ")
                .pattern("# #")
                .pattern("#X#")
                .unlockedBy(getHasName(Blocks.IRON_BARS), this.has(Blocks.IRON_BARS))
                .save(recipeOutput);
    }
}
