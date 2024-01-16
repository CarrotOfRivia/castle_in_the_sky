package com.song.castle_in_the_sky.blocks;

import com.song.castle_in_the_sky.blocks.block_entities.LaputaCoreBE;
import com.song.castle_in_the_sky.blocks.block_entities.TERegister;
import com.song.castle_in_the_sky.network.Channel;
import com.song.castle_in_the_sky.network.LaputaTESynPkt;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.function.ToIntFunction;

public class LaputaCore extends BaseEntityBlock{
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    protected static final VoxelShape BOTTOM_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D);

    public LaputaCore() {
        super(Properties.of().mapColor(MapColor.METAL).strength(-1.0F, 3600000.0F).lightLevel(litBlockEmission(15)).noLootTable().noOcclusion());
    }

    private static ToIntFunction<BlockState> litBlockEmission(int light) {
        return (blockState) -> blockState.getValue(BlockStateProperties.POWERED) ? light : 0;
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
                if(tileEntity instanceof LaputaCoreBE){
                    ((LaputaCoreBE) tileEntity).setActive(true);
                    tileEntity.setChanged();
                }
            }
            else {
                world.setBlock(pos, state.setValue(POWERED, false), 3);
                if(tileEntity instanceof LaputaCoreBE){
                    ((LaputaCoreBE) tileEntity).setActive(false);
                    tileEntity.setChanged();
                }
            }
            if(tileEntity instanceof LaputaCoreBE){
                Channel.INSTANCE.send(PacketDistributor.ALL.noArg(), new LaputaTESynPkt(((LaputaCoreBE) tileEntity).isDestroying(), hasSignal, pos));
            }
        }
    }

    @Override
    public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        if(!level.isClientSide() && player.getItemInHand(interactionHand).getItem()== Items.BEDROCK){
            BlockEntity blockEntity = level.getBlockEntity(blockPos);
            if (blockEntity instanceof LaputaCoreBE){
                ((LaputaCoreBE) blockEntity).setDestroying(true);
            }
        }
        return super.use(blockState, level, blockPos, player, interactionHand, blockHitResult);
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

    @Override
    public RenderShape getRenderShape(BlockState p_49232_) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        LaputaCoreBE result =  new LaputaCoreBE(blockPos, blockState);
        result.setActive(blockState.getValue(POWERED));
        result.setChanged();
        return result;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> blockEntityType) {
        return createTickerHelper(blockEntityType, TERegister.LAPUTA_CORE_TE_TYPE.get(), LaputaCoreBE::tick);
    }
}
