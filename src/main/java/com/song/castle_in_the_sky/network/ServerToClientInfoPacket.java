package com.song.castle_in_the_sky.network;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class ServerToClientInfoPacket {
    private final ITextComponent info;
    public ServerToClientInfoPacket(ITextComponent info){
        this.info = info;
    }

    public static void encode(ServerToClientInfoPacket pkt, PacketBuffer buffer){
        buffer.writeComponent(pkt.info);
    }

    public static ServerToClientInfoPacket decode(PacketBuffer buffer){
        return new ServerToClientInfoPacket(buffer.readComponent());
    }

    public static void handle(ServerToClientInfoPacket pkt, Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(() -> {
            // Make sure it's only executed on the physical client
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientHandlerClass.showInfo(pkt.info));
        });
        ctx.get().setPacketHandled(true);
    }
}
