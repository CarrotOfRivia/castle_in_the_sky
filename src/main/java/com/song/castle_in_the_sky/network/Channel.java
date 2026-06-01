package com.song.castle_in_the_sky.network;

import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class Channel {
    private static final String PROTOCOL_VERSION = "1";

    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(PROTOCOL_VERSION);
        registrar.playToClient(LaputaTESynPkt.TYPE, LaputaTESynPkt.STREAM_CODEC);
        registrar.playToClient(ServerToClientInfoPacket.TYPE, ServerToClientInfoPacket.STREAM_CODEC);
    }
}
