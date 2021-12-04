package com.song.castle_in_the_sky.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class LaputaTESynPkt {
    public final boolean isActive;
    public final int posX;
    public final int posY;
    public final int posZ;

    public LaputaTESynPkt(boolean isActive, int posX, int posY, int posZ){
        this.isActive = isActive;
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
    }

    public LaputaTESynPkt(boolean isActive, BlockPos pos){
        this.isActive = isActive;
        this.posX = pos.getX();
        this.posY = pos.getY();
        this.posZ = pos.getZ();
    }

    public static void encode(LaputaTESynPkt pkt, FriendlyByteBuf buffer){
        buffer.writeBoolean(pkt.isActive);
        buffer.writeInt(pkt.posX);
        buffer.writeInt(pkt.posY);
        buffer.writeInt(pkt.posZ);
    }

    public static LaputaTESynPkt decode(FriendlyByteBuf buffer){
        return new LaputaTESynPkt(buffer.readBoolean(), buffer.readInt(), buffer.readInt(), buffer.readInt());
    }

    public static void handle(LaputaTESynPkt pkt, Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(() -> {
            // Make sure it's only executed on the physical client
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientHandlerClass.handleCoreSynPacket(pkt, ctx));
        });
        ctx.get().setPacketHandled(true);
    }
}
