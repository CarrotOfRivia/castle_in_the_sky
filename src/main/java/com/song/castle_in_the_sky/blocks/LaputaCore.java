package com.song.castle_in_the_sky.blocks;

import com.song.castle_in_the_sky.blocks.tile_entities.LaputaCoreTE;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.Random;
import java.util.function.ToIntFunction;

public class LaputaCore extends Block {
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public LaputaCore() {
        super(AbstractBlock.Properties.of(Material.STONE).strength(-1.0F, 3600000.0F).lightLevel(litBlockEmission(15)).noDrops());
    }

    private static ToIntFunction<BlockState> litBlockEmission(int p_235420_0_) {
        return (p_235421_1_) -> p_235421_1_.getValue(BlockStateProperties.POWERED) ? p_235420_0_ : 0;
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
    public void animateTick(BlockState p_180655_1_, World p_180655_2_, BlockPos p_180655_3_, Random p_180655_4_) {
        super.animateTick(p_180655_1_, p_180655_2_, p_180655_3_, p_180655_4_);
    }
}
