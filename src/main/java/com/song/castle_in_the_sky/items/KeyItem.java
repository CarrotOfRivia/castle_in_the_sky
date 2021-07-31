package com.song.castle_in_the_sky.items;

import com.song.castle_in_the_sky.CastleInTheSky;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class KeyItem extends Item {
    public KeyItem() {
        super(new Item.Properties().tab(CastleInTheSky.ITEM_GROUP));
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level world, List<Component> iTextComponents, TooltipFlag iTooltipFlag) {
        super.appendHoverText(itemStack, world, iTextComponents, iTooltipFlag);
        iTextComponents.add(new TranslatableComponent("tooltip."+CastleInTheSky.MOD_ID+".keys").withStyle(ChatFormatting.GRAY));
    }
}
