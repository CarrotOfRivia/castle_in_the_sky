package com.song.castle_in_the_sky.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fmllegacy.network.NetworkEvent;
import java.util.function.Supplier;

public class ServerToClientInfoPacket {
    private final Component info;
    public ServerToClientInfoPacket(Component info){
        this.info = info;
    }

    public static void encode(ServerToClientInfoPacket pkt, FriendlyByteBuf buffer){
        buffer.writeComponent(pkt.info);
    }

    public static ServerToClientInfoPacket decode(FriendlyByteBuf buffer){
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
