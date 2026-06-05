package com.song.castle_in_the_sky.items;

import com.song.castle_in_the_sky.CastleInTheSky;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class KeyItem extends Item {
    public KeyItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext context, List<Component> tooltip, TooltipFlag iTooltipFlag) {
        super.appendHoverText(itemStack, context, tooltip, iTooltipFlag);
        tooltip.add(Component.translatable("tooltip."+CastleInTheSky.MOD_ID+".keys").withStyle(ChatFormatting.GRAY));
    }
}
