package com.song.castle_in_the_sky.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class LaputaTESynPkt {
    public final boolean isActive;
    public final boolean isDestroying;
    public final int posX;
    public final int posY;
    public final int posZ;
    public final Vec3 activatedInitPos;
    public final int destroyingProcess;

    public LaputaTESynPkt(boolean isDestroying, boolean isActive, int posX, int posY, int posZ){
        this.isDestroying = isDestroying;
        this.isActive = isActive;
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.activatedInitPos = new Vec3(0, -9999, 0);
        this.destroyingProcess = -9999;
    }

    public LaputaTESynPkt(boolean isDestroying, boolean isActive, BlockPos pos){
        this.isDestroying = isDestroying;
        this.isActive = isActive;
        this.posX = pos.getX();
        this.posY = pos.getY();
        this.posZ = pos.getZ();
        this.activatedInitPos = new Vec3(0, -9999, 0);
        this.destroyingProcess = -9999;
    }

    public LaputaTESynPkt(boolean isDestroying, boolean isActive, BlockPos pos, Vec3 activatedInitPos, int destroyingProcess){
        this.isDestroying = isDestroying;
        this.isActive = isActive;
        this.posX = pos.getX();
        this.posY = pos.getY();
        this.posZ = pos.getZ();
        this.activatedInitPos = activatedInitPos;
        this.destroyingProcess = destroyingProcess;
    }

    public static void encode(LaputaTESynPkt pkt, FriendlyByteBuf buffer){
        buffer.writeBoolean(pkt.isDestroying);
        buffer.writeBoolean(pkt.isActive);
        buffer.writeInt(pkt.posX);
        buffer.writeInt(pkt.posY);
        buffer.writeInt(pkt.posZ);
        buffer.writeDouble(pkt.activatedInitPos.x());
        buffer.writeDouble(pkt.activatedInitPos.y());
        buffer.writeDouble(pkt.activatedInitPos.z());
        buffer.writeInt(pkt.destroyingProcess);
    }

    public static LaputaTESynPkt decode(FriendlyByteBuf buffer){
        return new LaputaTESynPkt(buffer.readBoolean(), buffer.readBoolean(), new BlockPos(buffer.readInt(), buffer.readInt(), buffer.readInt()), new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble()), buffer.readInt());
    }

    public static void handle(LaputaTESynPkt pkt, Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(() -> {
            // Make sure it's only executed on the physical client
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientHandlerClass.handleCoreSynPacket(pkt, ctx));
        });
        ctx.get().setPacketHandled(true);
    }
}
