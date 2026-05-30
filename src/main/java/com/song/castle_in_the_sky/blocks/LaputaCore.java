package com.song.castle_in_the_sky.blocks;

import com.mojang.serialization.MapCodec;
import com.song.castle_in_the_sky.blocks.block_entities.LaputaCoreBE;
import com.song.castle_in_the_sky.blocks.block_entities.TERegister;
import com.song.castle_in_the_sky.config.ConfigCommon;
import com.song.castle_in_the_sky.items.ItemsRegister;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class LaputaCore extends BaseEntityBlock {
    public static final MapCodec<LaputaCore> CODEC = simpleCodec(LaputaCore::new);
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty DESTROYING = BooleanProperty.create("destroying");
    private static final int ANIMATION_TIME = 200;
    private static final int DESTRUCTION_TICKS = 200;
    private static final int DESTRUCTION_TIME_PER_TICK = 10;
    private static final int DESTROY_TOTAL_TICKS = ANIMATION_TIME + DESTRUCTION_TICKS * DESTRUCTION_TIME_PER_TICK;
    private static final int DESTRUCTION_RADIUS = 80;
    private static final int DESTRUCTION_RADIUS_SQUARED = DESTRUCTION_RADIUS * DESTRUCTION_RADIUS;
    private static final int HEIGHT_MIN = -14;
    private static final int HEIGHT_MAX = 100;
    private static final ArrayList<ArrayList<Integer>> DESTRUCTION_PATTERN = new ArrayList<>();
    private static final int PROGRESS_EACH_TICK;
    private static final Set<String> DESTRUCTION_BLACKLIST = new HashSet<>(Arrays.asList(
            "castle_in_the_sky:laputa_core",
            "minecraft:spruce_log",
            "minecraft:spruce_wood",
            "minecraft:shroomlight",
            "minecraft:spawner",
            "minecraft:chest",
            "minecraft:barrel"
    ));
    private static final Map<String, Integer> destroyProgress = new HashMap<>();

    static {
        for (int dx = -DESTRUCTION_RADIUS; dx <= DESTRUCTION_RADIUS; dx++) {
            for (int dy = HEIGHT_MIN; dy <= HEIGHT_MAX; dy++) {
                for (int dz = -DESTRUCTION_RADIUS; dz <= DESTRUCTION_RADIUS; dz++) {
                    if (dx * dx + dz * dz <= DESTRUCTION_RADIUS_SQUARED && !(dx == 0 && dy == -1 && dz == 0)) {
                        DESTRUCTION_PATTERN.add(new ArrayList<>(Arrays.asList(dx, dy, dz)));
                    }
                }
            }
        }
        Collections.shuffle(DESTRUCTION_PATTERN);
        PROGRESS_EACH_TICK = DESTRUCTION_PATTERN.size() / DESTRUCTION_TICKS + 1;
    }

    public LaputaCore(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(POWERED, false).setValue(DESTROYING, false));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> stateBuilder) {
        super.createBlockStateDefinition(stateBuilder);
        stateBuilder.add(POWERED, DESTROYING);
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, net.minecraft.world.level.block.Block neighborBlock, Orientation orientation, boolean movedByPiston) {
        if (level.isClientSide()) {
            return;
        }
        boolean hasSignal = level.hasNeighborSignal(pos);
        BlockState updated = state.setValue(POWERED, hasSignal);
        if (updated != state) {
            level.setBlock(pos, updated, net.minecraft.world.level.block.Block.UPDATE_ALL);
        }
        if (hasSignal || state.getValue(DESTROYING)) {
            level.scheduleTick(pos, this, 20);
        }
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!level.isClientSide() && stack.is(Items.BEDROCK) && !state.getValue(DESTROYING)) {
            level.setBlock(pos, state.setValue(DESTROYING, true), net.minecraft.world.level.block.Block.UPDATE_ALL);
            level.scheduleTick(pos, this, 1);
            return InteractionResult.SUCCESS_SERVER;
        }
        return InteractionResult.PASS;
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        String key = level.dimension().identifier() + "|" + pos.toShortString();

        if (!state.getValue(DESTROYING)) {
            destroyProgress.remove(key);
            return;
        }

        int progress = destroyProgress.getOrDefault(key, 0);
        if (progress == 0) {
            // Clear water/lava first so the collapse does not leave floating fluids behind.
            for (ArrayList<Integer> offset : DESTRUCTION_PATTERN) {
                BlockPos target = pos.offset(offset.get(0), offset.get(1), offset.get(2));
                if (level.getFluidState(target) != Fluids.EMPTY.defaultFluidState()) {
                    level.setBlockAndUpdate(target, Blocks.AIR.defaultBlockState());
                }
            }
            level.setBlockAndUpdate(pos.below(), BlockRegister.FAKE_BEACON.get().defaultBlockState());
        }

        if (progress >= DESTROY_TOTAL_TICKS) {
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    level.setBlockAndUpdate(pos.offset(dx, -1, dz), BlockRegister.FAKE_BEACON.get().defaultBlockState());
                }
            }
            level.destroyBlock(pos, false);
            level.addFreshEntity(new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, new ItemStack(ItemsRegister.LAPUTA_MINIATURE.get())));
            var laputaShardItem = BuiltInRegistries.ITEM.getValue(Identifier.parse("botania:laputa_shard"));
            if (laputaShardItem != null && laputaShardItem != Items.AIR) {
                ItemStack laputaShard = new ItemStack(laputaShardItem);
                level.addFreshEntity(new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, laputaShard));
            }
            destroyProgress.remove(key);
            return;
        }

        if (progress == ANIMATION_TIME) {
            level.explode(null, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, 1.5F, Level.ExplosionInteraction.NONE);
        }

        if (progress >= ANIMATION_TIME && (progress - ANIMATION_TIME) % DESTRUCTION_TIME_PER_TICK == 0) {
            int processTick = (progress - ANIMATION_TIME) / DESTRUCTION_TIME_PER_TICK;
            int start = processTick * PROGRESS_EACH_TICK;
            int end = Math.min((processTick + 1) * PROGRESS_EACH_TICK, DESTRUCTION_PATTERN.size());
            for (int i = start; i < end; i++) {
                ArrayList<Integer> offset = DESTRUCTION_PATTERN.get(i);
                BlockPos target = pos.offset(offset.get(0), offset.get(1), offset.get(2));
                BlockState targetState = level.getBlockState(target);
                String targetId = BuiltInRegistries.BLOCK.getKey(targetState.getBlock()).toString();
                if (!targetState.isAir() && !DESTRUCTION_BLACKLIST.contains(targetId)) {
                    level.destroyBlock(target, ConfigCommon.DESTRUCTION_DROPS.get());
                }
            }
        }

        destroyProgress.put(key, progress + 1);
        level.scheduleTick(pos, this, 1);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new LaputaCoreBE(pos, state);
    }

    @Override
    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, TERegister.LAPUTA_CORE_TE_TYPE.get(), LaputaCoreBE::tick);
    }
}
