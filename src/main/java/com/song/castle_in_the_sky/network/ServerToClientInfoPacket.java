package com.song.castle_in_the_sky.network;

import com.song.castle_in_the_sky.CastleInTheSky;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ServerToClientInfoPacket(Component info) implements CustomPacketPayload {
    public static final Type<ServerToClientInfoPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(CastleInTheSky.MOD_ID, "client_info"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerToClientInfoPacket> STREAM_CODEC = StreamCodec.ofMember(ServerToClientInfoPacket::encode, ServerToClientInfoPacket::decode);

    public void encode(RegistryFriendlyByteBuf buffer) {
        ComponentSerialization.STREAM_CODEC.encode(buffer, this.info);
    }

    public static ServerToClientInfoPacket decode(RegistryFriendlyByteBuf buffer) {
        return new ServerToClientInfoPacket(ComponentSerialization.STREAM_CODEC.decode(buffer));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
