package com.song.castle_in_the_sky.network;

import com.song.castle_in_the_sky.CastleInTheSky;
import com.song.castle_in_the_sky.blocks.block_entities.LaputaCoreBE;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ClientHandlerClass {
    public static void handleCoreSynPacket(LaputaTESynPkt pkt, Supplier<NetworkEvent.Context> ctx) {
        BlockPos pos = new BlockPos(pkt.posX, pkt.posY, pkt.posZ);
        assert Minecraft.getInstance().level != null;
        BlockEntity tileEntity = Minecraft.getInstance().level.getBlockEntity(pos);
        if(tileEntity instanceof LaputaCoreBE){
            ((LaputaCoreBE) tileEntity).setActive(pkt.isActive);
            ((LaputaCoreBE) tileEntity).setDestroying(pkt.isDestroying);
            if (pkt.activatedInitPos.y != -9999){
                ((LaputaCoreBE) tileEntity).setActivatedInitPos(pkt.activatedInitPos);
            }
            if(pkt.destroyingProcess != -9999){
                ((LaputaCoreBE) tileEntity).setDestroyProgress(pkt.destroyingProcess);
            }
        }
    }

    public static void showSacredCastleInfoBreak(){
        Minecraft.getInstance().gui.setOverlayMessage(new TranslatableComponent(String.format("info.%s.sacred_castle_effect.break", CastleInTheSky.MOD_ID)).withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD), false);
    }

    public static void showInfo(Component component){
        Minecraft.getInstance().gui.setOverlayMessage(component, false);
    }
}
