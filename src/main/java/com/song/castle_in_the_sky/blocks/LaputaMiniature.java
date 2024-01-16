package com.song.castle_in_the_sky.blocks;

import com.song.castle_in_the_sky.CastleInTheSky;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.MapColor;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LaputaMiniature extends Block {
    public LaputaMiniature() {
        super(Properties.of().mapColor(MapColor.DIRT).strength(3.0F).lightLevel((p)->15).noOcclusion());
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable BlockGetter p_49817_, List<Component> components, TooltipFlag tooltipFlag) {
        super.appendHoverText(itemStack, p_49817_, components, tooltipFlag);
        components.add(Component.translatable("tooltip."+ CastleInTheSky.MOD_ID+".laputa_miniature").withStyle(ChatFormatting.GRAY));
    }
}
