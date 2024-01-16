package com.song.castle_in_the_sky.blocks;

import com.song.castle_in_the_sky.CastleInTheSky;
import com.song.castle_in_the_sky.network.ClientHandlerClass;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;
import java.util.List;

public abstract class LockedDoor extends DoorBlock {
    private final BlockSetType type = BlockSetType.IRON;

    protected LockedDoor(Properties properties) {
        super(properties, BlockSetType.IRON);
    }


    @Override
    public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        Item item = player.getItemInHand(interactionHand).getItem();
        if (isKeyItem(item)){
            blockState = blockState.cycle(OPEN);
            level.setBlock(blockPos, blockState, 10);
            this.playSound(player, level, blockPos, blockState.getValue(OPEN));

            ItemStack itemStack = player.getItemInHand(interactionHand);
            itemStack.shrink(1);
            return InteractionResult.sidedSuccess(level.isClientSide());
        }
        else {
            if(level.isClientSide()){
                ClientHandlerClass.showInfo(Component.translatable("info."+ CastleInTheSky.MOD_ID+".locked_doors"));
            }
            return InteractionResult.PASS;
        }
    }

    private void playSound(@Nullable Entity p_251616_, Level p_249656_, BlockPos p_249439_, boolean p_251628_) {
        p_249656_.playSound(p_251616_, p_249439_, p_251628_ ? this.type.doorOpen() : this.type.doorClose(), SoundSource.BLOCKS, 1.0F, p_249656_.getRandom().nextFloat() * 0.1F + 0.9F);
    }

    protected abstract boolean isKeyItem(Item item);

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable BlockGetter blockReader, List<Component> iTextComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(itemStack, blockReader, iTextComponents, tooltipFlag);
        iTextComponents.add(Component.translatable("info."+ CastleInTheSky.MOD_ID+".locked_doors").withStyle(ChatFormatting.GRAY));
    }
}
