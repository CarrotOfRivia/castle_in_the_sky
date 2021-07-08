package com.song.castle_in_the_sky.items;

import com.song.castle_in_the_sky.CastleInTheSky;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class KeyItem extends Item {
    public KeyItem() {
        super(new Item.Properties().tab(CastleInTheSky.ITEM_GROUP));
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable World world, List<ITextComponent> iTextComponents, ITooltipFlag iTooltipFlag) {
        super.appendHoverText(itemStack, world, iTextComponents, iTooltipFlag);
        iTextComponents.add(new TranslationTextComponent("tooltip."+CastleInTheSky.MOD_ID+".keys").withStyle(TextFormatting.GRAY));
    }
}
