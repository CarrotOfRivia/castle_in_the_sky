package com.song.castle_in_the_sky;

import com.song.castle_in_the_sky.blocks.BlockRegister;
import com.song.castle_in_the_sky.blocks.block_entities.TERegister;
import com.song.castle_in_the_sky.client.ClientEvents;
import com.song.castle_in_the_sky.config.ConfigCommon;
import com.song.castle_in_the_sky.config.ConfigServer;
import com.song.castle_in_the_sky.effects.EffectRegister;
import com.song.castle_in_the_sky.events.ServerEvents;
import com.song.castle_in_the_sky.items.ItemsRegister;
import com.song.castle_in_the_sky.network.Channel;
import com.song.castle_in_the_sky.structures.StructureRegister;
import com.song.castle_in_the_sky.utils.CapabilityCastle;
import com.mojang.logging.LogUtils;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

/**
 * If you are looking for a structure generation tutorial, check this out: <a href="https://github.com/TelepathicGrunt/StructureTutorialMod">StructureTutorialMod</a>
 */

// The value here should match an entry in the generated META-INF/neoforge.mods.toml file.
@Mod(CastleInTheSky.MOD_ID)
public class CastleInTheSky
{
    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final String MOD_ID = "castle_in_the_sky";

    public CastleInTheSky(IEventBus modEventBus, ModContainer modContainer) {
        // Register the setup method for modloading
        modEventBus.addListener(this::setup);
        modEventBus.addListener(Channel::registerPayloads);

        // Register ourselves for server and other game events we are interested in
        NeoForge.EVENT_BUS.register(new ServerEvents());

        ItemsRegister.ITEMS.register(modEventBus);
        ItemsRegister.CREATIVE_TABS.register(modEventBus);
        BlockRegister.BLOCKS.register(modEventBus);
        TERegister.TILE_ENTITIES.register(modEventBus);
        EffectRegister.EFFECT.register(modEventBus);
        CapabilityCastle.ATTACHMENT_TYPES.register(modEventBus);

        modContainer.registerConfig(ModConfig.Type.COMMON, ConfigCommon.COMMON);
        modContainer.registerConfig(ModConfig.Type.SERVER, ConfigServer.SERVER);

        StructureRegister.DEFERRED_REGISTRY_STRUCTURE.register(modEventBus);

        if (FMLEnvironment.getDist() == Dist.CLIENT) {
            ClientEvents.register(modEventBus);
        }
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        // some preinit code
        LOGGER.info("HELLO FROM PREINIT");

    }

}
