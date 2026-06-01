package com.song.castle_in_the_sky.client;

import com.song.castle_in_the_sky.blocks.block_entities.LaputaCoreTER;
import com.song.castle_in_the_sky.blocks.block_entities.TERegister;
import com.song.castle_in_the_sky.network.ClientHandlerClass;
import com.song.castle_in_the_sky.network.LaputaTESynPkt;
import com.song.castle_in_the_sky.network.ServerToClientInfoPacket;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.network.event.RegisterClientPayloadHandlersEvent;

public class ClientEvents {
    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(ClientEvents::registerRenderers);
        modEventBus.addListener(ClientEvents::registerClientPayloadHandlers);
    }

    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(TERegister.LAPUTA_CORE_TE_TYPE.get(), LaputaCoreTER::new);
    }

    public static void registerClientPayloadHandlers(RegisterClientPayloadHandlersEvent event) {
        event.register(LaputaTESynPkt.TYPE, ClientHandlerClass::handleCoreSynPacket);
        event.register(ServerToClientInfoPacket.TYPE, ClientHandlerClass::showInfo);
    }
}
