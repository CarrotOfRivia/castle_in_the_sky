package com.song.castle_in_the_sky.network;

import com.song.castle_in_the_sky.CastleInTheSky;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

public class LaputaTESynPkt implements CustomPacketPayload {
    public static final Type<LaputaTESynPkt> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(CastleInTheSky.MOD_ID, "laputa_core_sync"));
    public static final StreamCodec<RegistryFriendlyByteBuf, LaputaTESynPkt> STREAM_CODEC = StreamCodec.ofMember(LaputaTESynPkt::encode, LaputaTESynPkt::decode);

    public final boolean isActive;
    public final boolean isDestroying;
    public final int posX;
    public final int posY;
    public final int posZ;
    public final Vec3 activatedInitPos;
    public final int destroyingProcess;

    public LaputaTESynPkt(boolean isDestroying, boolean isActive, int posX, int posY, int posZ) {
        this(isDestroying, isActive, new BlockPos(posX, posY, posZ), new Vec3(0, -9999, 0), -9999);
    }

    public LaputaTESynPkt(boolean isDestroying, boolean isActive, BlockPos pos) {
        this(isDestroying, isActive, pos, new Vec3(0, -9999, 0), -9999);
    }

    public LaputaTESynPkt(boolean isDestroying, boolean isActive, BlockPos pos, Vec3 activatedInitPos, int destroyingProcess) {
        this.isDestroying = isDestroying;
        this.isActive = isActive;
        this.posX = pos.getX();
        this.posY = pos.getY();
        this.posZ = pos.getZ();
        this.activatedInitPos = activatedInitPos;
        this.destroyingProcess = destroyingProcess;
    }

    public void encode(RegistryFriendlyByteBuf buffer) {
        buffer.writeBoolean(this.isDestroying);
        buffer.writeBoolean(this.isActive);
        buffer.writeInt(this.posX);
        buffer.writeInt(this.posY);
        buffer.writeInt(this.posZ);
        buffer.writeDouble(this.activatedInitPos.x());
        buffer.writeDouble(this.activatedInitPos.y());
        buffer.writeDouble(this.activatedInitPos.z());
        buffer.writeInt(this.destroyingProcess);
    }

    public static LaputaTESynPkt decode(RegistryFriendlyByteBuf buffer) {
        return new LaputaTESynPkt(buffer.readBoolean(), buffer.readBoolean(), new BlockPos(buffer.readInt(), buffer.readInt(), buffer.readInt()), new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble()), buffer.readInt());
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
