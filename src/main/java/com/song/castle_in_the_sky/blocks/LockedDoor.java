package com.song.castle_in_the_sky.blocks;

import com.song.castle_in_the_sky.CastleInTheSky;
import com.song.castle_in_the_sky.network.ClientHandlerClass;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;
import java.util.List;

public abstract class LockedDoor extends DoorBlock {

    protected LockedDoor(Properties properties) {
        super(properties);
    }


    @Override
    public InteractionResult use(BlockState blockState, Level world, BlockPos blockPos, Player playerEntity, InteractionHand hand, BlockHitResult blockRayTraceResult) {
        Item item = playerEntity.getItemInHand(hand).getItem();
        if (isKeyItem(item)){
            blockState = blockState.cycle(OPEN);
            world.setBlock(blockPos, blockState, 10);
            world.levelEvent(playerEntity, blockState.getValue(OPEN) ? this.getOpenSound() : this.getCloseSound(), blockPos, 0);

            ItemStack itemStack = playerEntity.getItemInHand(hand);
            itemStack.shrink(1);
            return InteractionResult.sidedSuccess(world.isClientSide());
        }
        else {
            if(world.isClientSide()){
                ClientHandlerClass.showInfo(new TranslatableComponent("info."+ CastleInTheSky.MOD_ID+".locked_doors"));
            }
            return InteractionResult.PASS;
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
    public void appendHoverText(ItemStack itemStack, @Nullable BlockGetter blockReader, List<Component> iTextComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(itemStack, blockReader, iTextComponents, tooltipFlag);
        iTextComponents.add(new TranslatableComponent("info."+ CastleInTheSky.MOD_ID+".locked_doors").withStyle(ChatFormatting.GRAY));
    }
}
