package com.song.castle_in_the_sky.client;

import com.song.castle_in_the_sky.blocks.block_entities.LaputaCoreTER;
import com.song.castle_in_the_sky.blocks.block_entities.TERegister;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

public class ClientEvents {
    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(ClientEvents::registerRenderers);
    }

    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(TERegister.LAPUTA_CORE_TE_TYPE.get(), LaputaCoreTER::new);
    }
}
