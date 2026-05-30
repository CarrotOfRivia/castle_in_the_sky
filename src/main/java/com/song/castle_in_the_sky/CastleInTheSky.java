package com.song.castle_in_the_sky;

import com.song.castle_in_the_sky.blocks.BlockRegister;
import com.song.castle_in_the_sky.blocks.block_entities.LaputaCoreTER;
import com.song.castle_in_the_sky.blocks.block_entities.TERegister;
import com.song.castle_in_the_sky.config.ConfigCommon;
import com.song.castle_in_the_sky.config.ConfigServer;
import com.song.castle_in_the_sky.effects.EffectRegister;
import com.song.castle_in_the_sky.events.ServerEvents;
import com.song.castle_in_the_sky.items.ItemsRegister;
import com.song.castle_in_the_sky.structures.StructureRegister;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * If you are looking for a structure generation tutorial, check this out: <a href="https://github.com/TelepathicGrunt/StructureTutorialMod">StructureTutorialMod</a>
 */

// The value here should match an entry in the META-INF/mods.toml file
@Mod(CastleInTheSky.MOD_ID)
public class CastleInTheSky
{
    // TODO: 1. castle_in_the_sky advancement; 2. trading levitation stone; 3. generation
    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "castle_in_the_sky";

    public CastleInTheSky(IEventBus modBus, ModContainer modContainer) {
        modBus.addListener(this::setup);
        modBus.addListener(this::doClientStuff);

        // Register ourselves for server and other game events we are interested in
        NeoForge.EVENT_BUS.register(new ServerEvents());

        ItemsRegister.ITEMS.register(modBus);
        ItemsRegister.CREATIVE_TABS.register(modBus);
        BlockRegister.BLOCKS.register(modBus);
        TERegister.TILE_ENTITIES.register(modBus);
        EffectRegister.EFFECT.register(modBus);

        modContainer.registerConfig(ModConfig.Type.COMMON, ConfigCommon.COMMON);
        modContainer.registerConfig(ModConfig.Type.SERVER, ConfigServer.SERVER);

        StructureRegister.DEFERRED_REGISTRY_STRUCTURE.register(modBus);
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        // some preinit code
        LOGGER.info("HELLO FROM PREINIT");

    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> BlockEntityRenderers.register(TERegister.LAPUTA_CORE_TE_TYPE.get(), LaputaCoreTER::new));
    }

}
