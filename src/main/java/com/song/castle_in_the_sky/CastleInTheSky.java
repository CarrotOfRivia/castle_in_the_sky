package com.song.castle_in_the_sky;

import com.song.castle_in_the_sky.blocks.BlockRegister;
import com.song.castle_in_the_sky.blocks.block_entities.LaputaCoreTER;
import com.song.castle_in_the_sky.blocks.block_entities.TERegister;
import com.song.castle_in_the_sky.config.ConfigCommon;
import com.song.castle_in_the_sky.config.ConfigServer;
import com.song.castle_in_the_sky.effects.EffectRegister;
import com.song.castle_in_the_sky.events.ServerEvents;
import com.song.castle_in_the_sky.items.ItemsRegister;
import com.song.castle_in_the_sky.network.Channel;
import com.song.castle_in_the_sky.network.LaputaTESynPkt;
import com.song.castle_in_the_sky.network.ServerToClientInfoPacket;
import com.song.castle_in_the_sky.structures.StructureRegister;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.stream.Collectors;

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

    public CastleInTheSky() {
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        // Register the doClientStuff method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new ServerEvents());

        ItemsRegister.ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        BlockRegister.BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        TERegister.TILE_ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());
        EffectRegister.EFFECT.register(FMLJavaModLoadingContext.get().getModEventBus());

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ConfigCommon.COMMON);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ConfigServer.SERVER);

        StructureRegister.DEFERRED_REGISTRY_STRUCTURE.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        // some preinit code
        LOGGER.info("HELLO FROM PREINIT");

        int id = 0;
        Channel.INSTANCE.registerMessage(id++, LaputaTESynPkt.class, LaputaTESynPkt::encode, LaputaTESynPkt::decode, LaputaTESynPkt::handle);
        Channel.INSTANCE.registerMessage(id++, ServerToClientInfoPacket.class, ServerToClientInfoPacket::encode, ServerToClientInfoPacket::decode, ServerToClientInfoPacket::handle);

    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        BlockEntityRenderers.register(TERegister.LAPUTA_CORE_TE_TYPE.get(), LaputaCoreTER::new);
    }

}
