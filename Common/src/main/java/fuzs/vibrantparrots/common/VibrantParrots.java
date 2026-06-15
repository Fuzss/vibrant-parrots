package fuzs.vibrantparrots.common;

import fuzs.puzzleslib.common.api.config.v3.ConfigHolder;
import fuzs.puzzleslib.common.api.core.v1.ModConstructor;
import fuzs.puzzleslib.common.api.core.v1.context.DataPackRegistriesContext;
import fuzs.puzzleslib.common.api.core.v1.context.EntityAttributesContext;
import fuzs.puzzleslib.common.api.core.v1.context.SpawnPlacementsContext;
import fuzs.puzzleslib.common.api.event.v1.BuildCreativeModeTabContentsCallback;
import fuzs.puzzleslib.common.api.event.v1.entity.ServerEntityEvents;
import fuzs.puzzleslib.common.api.event.v1.entity.player.PlayerInteractEvents;
import fuzs.vibrantparrots.common.config.ServerConfig;
import fuzs.vibrantparrots.common.handler.ParrotBehaviorHandler;
import fuzs.vibrantparrots.common.handler.ParrotSpawningHandler;
import fuzs.vibrantparrots.common.init.ModRegistry;
import fuzs.vibrantparrots.common.world.entity.animal.parrot.ParrotVariant;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.animal.parrot.Parrot;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.levelgen.Heightmap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VibrantParrots implements ModConstructor {
    public static final String MOD_ID = "vibrantparrots";
    public static final String MOD_NAME = "Vibrant Parrots";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    public static final ConfigHolder CONFIG = ConfigHolder.builder(MOD_ID).server(ServerConfig.class);

    @Override
    public void onConstructMod() {
        ModRegistry.bootstrap();
        registerEventHandlers();
    }

    private static void registerEventHandlers() {
        ServerEntityEvents.LOAD.register(ParrotSpawningHandler::onEntityLoad);
        PlayerInteractEvents.USE_ENTITY.register(ParrotBehaviorHandler::onUseEntity);
        BuildCreativeModeTabContentsCallback.buildCreativeModeTabContents(ModRegistry.CREATIVE_MODE_TAB.key())
                .register((CreativeModeTab creativeModeTab, CreativeModeTab.ItemDisplayParameters itemDisplayParameters, CreativeModeTab.Output output) -> {
                    output.accept(Items.PARROT_SPAWN_EGG);
                });

    }

    @Override
    public void onRegisterEntityAttributes(EntityAttributesContext context) {
        context.registerAttributes(ModRegistry.PARROT_ENTITY_TYPE.value(), Parrot.createAttributes());
    }

    @Override
    public void onRegisterSpawnPlacements(SpawnPlacementsContext context) {
        context.registerSpawnPlacement((EntityType<Parrot>) (EntityType<?>) ModRegistry.PARROT_ENTITY_TYPE.value(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING,
                Parrot::checkParrotSpawnRules);
    }

    @Override
    public void onRegisterDataPackRegistries(DataPackRegistriesContext context) {
        context.registerSyncedRegistry(ModRegistry.PARROT_VARIANT_REGISTRY,
                ParrotVariant.DIRECT_CODEC,
                ParrotVariant.NETWORK_CODEC);
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }
}
