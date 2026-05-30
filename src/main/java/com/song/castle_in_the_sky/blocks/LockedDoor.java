package com.song.castle_in_the_sky.blocks;

import com.song.castle_in_the_sky.CastleInTheSky;
import com.song.castle_in_the_sky.network.ClientHandlerClass;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.phys.BlockHitResult;

public abstract class LockedDoor extends DoorBlock {
    protected LockedDoor(Properties properties) {
        super(BlockSetType.IRON, properties);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        if (isKeyItem(stack.getItem())) {
            if (!level.isClientSide()) {
                this.setOpen(player, level, blockState, blockPos, !blockState.getValue(OPEN));
                stack.shrink(1);
            }
            return InteractionResult.SUCCESS_SERVER;
        }
        if (level.isClientSide()) {
            ClientHandlerClass.showInfo(Component.translatable("info." + CastleInTheSky.MOD_ID + ".locked_doors"));
        }
        return InteractionResult.PASS;
    }

    protected abstract boolean isKeyItem(Item item);
}
