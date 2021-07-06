package com.song.castle_in_the_sky.blocks;

import com.song.castle_in_the_sky.blocks.tile_entities.LaputaCoreTE;
import com.song.castle_in_the_sky.network.Channel;
import com.song.castle_in_the_sky.network.LaputaTESynPkt;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.Random;
import java.util.function.ToIntFunction;

public class LaputaCore extends Block {
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    protected static final VoxelShape BOTTOM_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D);

    public LaputaCore() {
        super(AbstractBlock.Properties.of(Material.STONE).strength(-1.0F, 3600000.0F).lightLevel(litBlockEmission(15)).noDrops());
    }

    private static ToIntFunction<BlockState> litBlockEmission(int light) {
        return (p_235421_1_) -> p_235421_1_.getValue(BlockStateProperties.POWERED) ? light : 0;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new LaputaCoreTE();
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> stateBuilder) {
        super.createBlockStateDefinition(stateBuilder);
        stateBuilder.add(POWERED);
    }

    @Override
    public void neighborChanged(BlockState state, World world, BlockPos pos, Block neighbor, BlockPos p_220069_5_, boolean p_220069_6_) {
        if(! world.isClientSide()){
            TileEntity tileEntity = world.getBlockEntity(pos);
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
    public BlockState getStateForPlacement(BlockItemUseContext blockItemUseContext) {
        return this.defaultBlockState().setValue(POWERED, false);
    }

    @Override
    public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
        return BOTTOM_AABB;
    }
}
