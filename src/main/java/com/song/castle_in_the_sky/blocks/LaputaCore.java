package com.song.castle_in_the_sky.blocks;

import com.song.castle_in_the_sky.blocks.tile_entities.LaputaCoreTE;
import com.song.castle_in_the_sky.network.Channel;
import com.song.castle_in_the_sky.network.LaputaTESynPkt;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.fmllegacy.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.function.ToIntFunction;

public class LaputaCore extends Block implements EntityBlock {
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    protected static final VoxelShape BOTTOM_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D);

    public LaputaCore() {
        super(Properties.of(Material.STONE).strength(-1.0F, 3600000.0F).lightLevel(litBlockEmission(15)).noDrops());
    }

    private static ToIntFunction<BlockState> litBlockEmission(int light) {
        return (p_235421_1_) -> p_235421_1_.getValue(BlockStateProperties.POWERED) ? light : 0;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateBuilder) {
        super.createBlockStateDefinition(stateBuilder);
        stateBuilder.add(POWERED);
    }

    @Override
    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block neighbor, BlockPos p_220069_5_, boolean p_220069_6_) {
        if(! world.isClientSide()){
            BlockEntity tileEntity = world.getBlockEntity(pos);
            boolean hasSignal = world.hasNeighborSignal(pos);
            if(hasSignal){
                world.setBlock(pos, state.setValue(POWERED, true), 3);
                if(tileEntity instanceof LaputaCoreTE){
                    ((LaputaCoreTE) tileEntity).setActive(true);
                    tileEntity.setChanged();
                }
            }
            else {
                world.setBlock(pos, state.setValue(POWERED, false), 3);
                if(tileEntity instanceof LaputaCoreTE){
                    ((LaputaCoreTE) tileEntity).setActive(false);
                    tileEntity.setChanged();
                }
            }
            Channel.INSTANCE.send(PacketDistributor.ALL.noArg(), new LaputaTESynPkt(hasSignal, pos));
        }
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext blockItemUseContext) {
        return this.defaultBlockState().setValue(POWERED, false);
    }

    @Override
    public VoxelShape getShape(BlockState p_220053_1_, BlockGetter p_220053_2_, BlockPos p_220053_3_, CollisionContext p_220053_4_) {
        return BOTTOM_AABB;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new LaputaCoreTE(blockPos, blockState);
    }
}
