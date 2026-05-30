package com.song.castle_in_the_sky.network;

import com.song.castle_in_the_sky.CastleInTheSky;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public class ClientHandlerClass {
    public static void showSacredCastleInfoBreak(){
        Minecraft.getInstance().gui.setOverlayMessage(
                Component.translatable("info." + CastleInTheSky.MOD_ID + ".sacred_castle_effect.break")
                        .withStyle(ChatFormatting.RED, ChatFormatting.BOLD),
                false
        );
    }

    public static void showInfo(Component component){
        Minecraft.getInstance().gui.setOverlayMessage(component, false);
    }
}
