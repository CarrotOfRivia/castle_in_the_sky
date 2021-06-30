package com.song.castle_in_the_sky.blocks;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;

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
}
