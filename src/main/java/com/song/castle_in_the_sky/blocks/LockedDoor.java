package com.song.castle_in_the_sky.blocks;

import com.song.castle_in_the_sky.CastleInTheSky;
import com.song.castle_in_the_sky.network.ClientHandlerClass;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public abstract class LockedDoor extends DoorBlock {

    protected LockedDoor(AbstractBlock.Properties properties) {
        super(properties);
    }

    @Override
    public ActionResultType use(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockRayTraceResult blockRayTraceResult) {
        Item item = playerEntity.getItemInHand(hand).getItem();
        if (isKeyItem(item)){
            blockState = blockState.cycle(OPEN);
            world.setBlock(blockPos, blockState, 10);
            world.levelEvent(playerEntity, blockState.getValue(OPEN) ? this.getOpenSound() : this.getCloseSound(), blockPos, 0);

            ItemStack itemStack = playerEntity.getItemInHand(hand);
            itemStack.shrink(1);
            return ActionResultType.sidedSuccess(world.isClientSide());
        }
        else {
            if(world.isClientSide()){
                ClientHandlerClass.showInfo(new TranslationTextComponent("info."+ CastleInTheSky.MOD_ID+".locked_doors"));
            }
            return ActionResultType.PASS;
        }
    }

    protected abstract boolean isKeyItem(Item item);

    private int getCloseSound() {
        return this.material == Material.METAL ? 1011 : 1012;
    }

    private int getOpenSound() {
        return this.material == Material.METAL ? 1005 : 1006;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable IBlockReader blockReader, List<ITextComponent> iTextComponents, ITooltipFlag tooltipFlag) {
        super.appendHoverText(itemStack, blockReader, iTextComponents, tooltipFlag);
        iTextComponents.add(new TranslationTextComponent("info."+ CastleInTheSky.MOD_ID+".locked_doors").withStyle(TextFormatting.GRAY));
    }
}
