package fuzs.vibrantparrots.common.init;

import com.mojang.datafixers.util.Either;
import fuzs.vibrantparrots.common.world.entity.animal.parrot.ParrotVariant;
import net.minecraft.core.ClientAsset;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.animal.parrot.Parrot;
import net.minecraft.world.entity.variant.SpawnPrioritySelectors;
import net.minecraft.world.level.block.ColorCollection;

public class ParrotVariants {
    public static final ResourceKey<ParrotVariant> WHITE = register("white");
    public static final ResourceKey<ParrotVariant> ORANGE = register("orange");
    public static final ResourceKey<ParrotVariant> MAGENTA = register("magenta");
    public static final ResourceKey<ParrotVariant> YELLOW = register("yellow");
    public static final ResourceKey<ParrotVariant> PINK = register("pink");
    public static final ResourceKey<ParrotVariant> GRAY = register("gray");
    public static final ResourceKey<ParrotVariant> CYAN = register("cyan");
    public static final ResourceKey<ParrotVariant> PURPLE = register("purple");
    public static final ResourceKey<ParrotVariant> BROWN = register("brown");
    public static final ResourceKey<ParrotVariant> GREEN = register("green");
    public static final ResourceKey<ParrotVariant> BLACK = register("black");
    public static final ResourceKey<ParrotVariant> DEFAULT = WHITE;
    public static final ColorCollection<Either<Parrot.Variant, ResourceKey<ParrotVariant>>> VARIANTS = new ColorCollection<>(
            Either.right(WHITE),
            Either.right(ORANGE),
            Either.right(MAGENTA),
            Either.left(Parrot.Variant.YELLOW_BLUE),
            Either.right(YELLOW),
            Either.left(Parrot.Variant.GREEN),
            Either.right(PINK),
            Either.right(GRAY),
            Either.left(Parrot.Variant.GRAY),
            Either.right(CYAN),
            Either.right(PURPLE),
            Either.left(Parrot.Variant.BLUE),
            Either.right(BROWN),
            Either.right(GREEN),
            Either.left(Parrot.Variant.RED_BLUE),
            Either.right(BLACK));

    private static ResourceKey<ParrotVariant> register(String path) {
        return ModRegistry.REGISTRIES.makeResourceKey(ModRegistry.PARROT_VARIANT_REGISTRY, path);
    }

    public static void bootstrap(BootstrapContext<ParrotVariant> context) {
        register(context, WHITE);
        register(context, ORANGE);
        register(context, MAGENTA);
        register(context, YELLOW);
        register(context, PINK);
        register(context, GRAY);
        register(context, CYAN);
        register(context, PURPLE);
        register(context, BROWN);
        register(context, GREEN);
        register(context, BLACK);
    }

    private static void register(BootstrapContext<ParrotVariant> context, ResourceKey<ParrotVariant> resourceKey) {
        context.register(resourceKey,
                new ParrotVariant(new ClientAsset.ResourceTexture(resourceKey.identifier()
                        .withPrefix("entity/parrot/parrot_")), SpawnPrioritySelectors.fallback(0)));
    }
}
