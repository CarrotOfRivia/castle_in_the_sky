package com.song.castle_in_the_sky.blocks;

import com.song.castle_in_the_sky.CastleInTheSky;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.phys.BlockHitResult;

public abstract class LockedDoor extends DoorBlock {
    private final BlockSetType type = BlockSetType.IRON;

    protected LockedDoor(Properties properties) {
        super(BlockSetType.IRON, properties);
    }


    @Override
    protected InteractionResult useItemOn(ItemStack itemStack, BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        Item item = itemStack.getItem();
        if (isKeyItem(item)){
            blockState = blockState.cycle(OPEN);
            level.setBlock(blockPos, blockState, 10);
            this.playSound(player, level, blockPos, blockState.getValue(OPEN));

            itemStack.shrink(1);
            return InteractionResult.SUCCESS;
        }
        else {
            if(level.isClientSide()){
                player.displayClientMessage(Component.translatable("info."+ CastleInTheSky.MOD_ID+".locked_doors"), true);
            }
            return super.useItemOn(itemStack, blockState, level, blockPos, player, interactionHand, blockHitResult);
        }
    }

    private void playSound(Entity p_251616_, Level p_249656_, BlockPos p_249439_, boolean p_251628_) {
        p_249656_.playSound(p_251616_, p_249439_, p_251628_ ? this.type.doorOpen() : this.type.doorClose(), SoundSource.BLOCKS, 1.0F, p_249656_.getRandom().nextFloat() * 0.1F + 0.9F);
    }

    protected abstract boolean isKeyItem(Item item);
}
