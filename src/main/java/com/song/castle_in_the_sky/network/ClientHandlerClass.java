package com.song.castle_in_the_sky.network;

import com.song.castle_in_the_sky.CastleInTheSky;
import com.song.castle_in_the_sky.blocks.tile_entities.LaputaCoreTE;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class ClientHandlerClass {
    public static void handleCoreSynPacket(LaputaTESynPkt pkt, Supplier<NetworkEvent.Context> ctx) {
        BlockPos pos = new BlockPos(pkt.posX, pkt.posY, pkt.posZ);
        assert Minecraft.getInstance().level != null;
        TileEntity tileEntity = Minecraft.getInstance().level.getBlockEntity(pos);
        if(tileEntity instanceof LaputaCoreTE){
            ((LaputaCoreTE) tileEntity).setActive(pkt.isActive);
        }
    }

    public static void showSacredCastleInfoBreak(){
        Minecraft.getInstance().gui.setOverlayMessage(new TranslationTextComponent(String.format("info.%s.sacred_castle_effect.break", CastleInTheSky.MOD_ID)).withStyle(TextFormatting.RED).withStyle(TextFormatting.BOLD), false);
    }

    public static void showInfo(ITextComponent component){
        Minecraft.getInstance().gui.setOverlayMessage(component, false);
    }
}
