package com.song.castle_in_the_sky.items;

import com.song.castle_in_the_sky.CastleInTheSky;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.function.Consumer;

public class KeyItem extends Item {
    public KeyItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag iTooltipFlag) {
        super.appendHoverText(itemStack, context, display, tooltip, iTooltipFlag);
        tooltip.accept(Component.translatable("tooltip."+CastleInTheSky.MOD_ID+".keys").withStyle(ChatFormatting.GRAY));
    }
}
