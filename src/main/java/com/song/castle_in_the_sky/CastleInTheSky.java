package com.song.castle_in_the_sky;

import com.song.castle_in_the_sky.blocks.BlockRegister;
import com.song.castle_in_the_sky.blocks.block_entities.LaputaCoreTER;
import com.song.castle_in_the_sky.blocks.block_entities.TERegister;
import com.song.castle_in_the_sky.config.ConfigCommon;
import com.song.castle_in_the_sky.config.ConfigServer;
import com.song.castle_in_the_sky.effects.EffectRegister;
import com.song.castle_in_the_sky.events.ServerEvents;
import com.song.castle_in_the_sky.features.StructureRegister;
import com.song.castle_in_the_sky.items.ItemsRegister;
import com.song.castle_in_the_sky.network.Channel;
import com.song.castle_in_the_sky.network.LaputaTESynPkt;
import com.song.castle_in_the_sky.network.ServerToClientInfoPacket;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
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
 * If you are looking for a structure generation tutorial, check this out: https://github.com/TelepathicGrunt/StructureTutorialMod
 */

// The value here should match an entry in the META-INF/mods.toml file
@Mod(CastleInTheSky.MOD_ID)
public class CastleInTheSky
{
    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "castle_in_the_sky";
    public static final CreativeModeTab ITEM_GROUP = new CreativeModeTab(MOD_ID+".all") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(ItemsRegister.LEVITATION_STONE.get());
        }
    };

    public CastleInTheSky() {
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        // Register the enqueueIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        // Register the processIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
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

        StructureRegister.STRUCTURES.register(FMLJavaModLoadingContext.get().getModEventBus());
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
        // do something that can only be done on the client
        // TODO door render issue
        ItemBlockRenderTypes.setRenderLayer(BlockRegister.RED_DOOR.get(), RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(BlockRegister.BLUE_DOOR.get(), RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(BlockRegister.YELLOW_DOOR.get(), RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(BlockRegister.FAKE_BEACON.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(BlockRegister.LAPUTA_CORE.get(), RenderType.solid());

        BlockEntityRenderers.register(TERegister.LAPUTA_CORE_TE_TYPE.get(), LaputaCoreTER::new);
    }

    private void enqueueIMC(final InterModEnqueueEvent event)
    {
        // some example code to dispatch IMC to another mod
        InterModComms.sendTo("examplemod", "helloworld", () -> { LOGGER.info("Hello world from the MDK"); return "Hello world";});
    }

    private void processIMC(final InterModProcessEvent event)
    {
        // some example code to receive and process InterModComms from other mods
        LOGGER.info("Got IMC {}", event.getIMCStream().
                map(m->m.getMessageSupplier().get()).
                collect(Collectors.toList()));
    }
    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
    // Event bus for receiving Registry Events)
    @Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent) {
            // register a new block here
            LOGGER.info("HELLO from Register Block");
        }
    }
}
