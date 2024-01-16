package com.song.castle_in_the_sky.blocks.block_entities;

import com.song.castle_in_the_sky.blocks.BlockRegister;
import com.song.castle_in_the_sky.blocks.LaputaCore;
import com.song.castle_in_the_sky.config.ConfigCommon;
import com.song.castle_in_the_sky.effects.EffectRegister;
import com.song.castle_in_the_sky.items.ItemsRegister;
import com.song.castle_in_the_sky.network.Channel;
import com.song.castle_in_the_sky.network.LaputaTESynPkt;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class LaputaCoreBE extends BlockEntity {
    private boolean isActive=false;
    private boolean isDestroying=false;
    private int destroyProgress = 0;
    // Manually update the active state based on powered state.
    private boolean initialUpdated = false;
    private Vec3 activatedInitPos = new Vec3(0, 0, 0);

    public static final int ANIMATION_TIME = 200;
    private static final int DESTRUCTION_TICKS = 200;
    private static final int DESTRUCTION_TIME_PER_TICK = 10;
    private static final int DESTROY_MAX = ANIMATION_TIME + DESTRUCTION_TICKS * DESTRUCTION_TIME_PER_TICK;
    private static final int RADIUS = 80;
    private static final int RADIUS2 = RADIUS * RADIUS;
    private static final int HEIGHT_MIN = -14;
    private static final int HEIGHT_MAX = 100;
    private static final ArrayList<ArrayList<Integer>> DESTRUCTION_PATTERN = new ArrayList<>();
    private static final int PROGRESS_EACH_TICK;

    static {
        for(int dx = -RADIUS; dx<=RADIUS; dx++){
            for(int dy = HEIGHT_MIN; dy<=HEIGHT_MAX; dy++){
                for(int dz = -RADIUS; dz<=RADIUS; dz++){
                    if (dx*dx + dz*dz <= RADIUS2){
                        if(dx==0 && dy==-1 && dz==0){
                            continue;
                        }
                        ArrayList<Integer> tmp = new ArrayList<>(Arrays.asList(dx, dy, dz));
                        DESTRUCTION_PATTERN.add(tmp);
                    }
                }
            }
        }

        Collections.shuffle(DESTRUCTION_PATTERN);

        PROGRESS_EACH_TICK = DESTRUCTION_PATTERN.size() / DESTRUCTION_TICKS + 1;
    }
    private static final Set<String> DESTRUCTION_BLACKLIST = new HashSet<>(Arrays.asList("castle_in_the_sky:laputa_core", "minecraft:spruce_log", "minecraft:spruce_wood", "minecraft:shroomlight", "minecraft:spawner", "minecraft:chest", "minecraft:barrel"));

    public LaputaCoreBE(BlockPos pos, BlockState state){
        super(TERegister.LAPUTA_CORE_TE_TYPE.get(), pos, state);
    }

    public static void tick(Level level, BlockPos blockPos, BlockState blockState, LaputaCoreBE laputaCoreTE) {
        if(!level.isClientSide()){
            if (!laputaCoreTE.initialUpdated){
                laputaCoreTE.setActive(blockState.getValue(LaputaCore.POWERED));
                laputaCoreTE.initialUpdated = true;
            }

            if (laputaCoreTE.isDestroying){
                boolean drops = ConfigCommon.DESTRUCTION_DROPS.get();
                if(laputaCoreTE.destroyProgress % 3 == 0){
                    // update to client
                    Channel.INSTANCE.send(PacketDistributor.NEAR.with(PacketDistributor.TargetPoint.p(laputaCoreTE.getBlockPos().getX(), laputaCoreTE.getBlockPos().getY(), laputaCoreTE.getBlockPos().getZ(), 20, Level.OVERWORLD)),
                            new LaputaTESynPkt(laputaCoreTE.isDestroying, laputaCoreTE.isActive(), laputaCoreTE.getBlockPos(), laputaCoreTE.getActivatedInitPos(), laputaCoreTE.destroyProgress));
                }

                if(laputaCoreTE.destroyProgress == 0){
                    // remove all fluids and plants
                    for (ArrayList<Integer> pos: DESTRUCTION_PATTERN){
                        BlockPos target = blockPos.offset(pos.get(0), pos.get(1), pos.get(2));
                        if(level.getFluidState(target) != Fluids.EMPTY.defaultFluidState()){
                            level.setBlockAndUpdate(target, Blocks.AIR.defaultBlockState());
                        }
//                        if (level.getBlockState(target).getBlock() instanceof TwistingVinesBlock ){
//                            level.destroyBlock(target, drops);
//                        }
                    }
                    level.setBlockAndUpdate(blockPos.offset(0, -1, 0), BlockRegister.FAKE_BEACON.get().defaultBlockState());
                }


                if (laputaCoreTE.destroyProgress >= ANIMATION_TIME){
                    if(laputaCoreTE.destroyProgress >= DESTROY_MAX){
                        // add fake beacon platform after destruction
                        for (int dx = -1; dx <=1; dx++){
                            for (int dz = -1; dz <=1; dz++){
                                level.setBlockAndUpdate(blockPos.offset(dx, -1, dz), BlockRegister.FAKE_BEACON.get().defaultBlockState());
                            }
                        }
                        level.destroyBlock(blockPos, false);
                        level.addFreshEntity(new ItemEntity(level, blockPos.getX(), blockPos.getY(), blockPos.getZ(), new ItemStack(ItemsRegister.LAPUTA_MINIATURE.get())));

                        if (ModList.get().isLoaded("botania")) {
                            ItemStack itemStack = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation("botania:laputa_shard")));
                            itemStack.getOrCreateTag().putInt("level", 20);
                            level.addFreshEntity(new ItemEntity(level, blockPos.getX(), blockPos.getY(), blockPos.getZ(), itemStack));
                        }
                    }
                    else {
                        if(laputaCoreTE.destroyProgress == ANIMATION_TIME){
                            // animation end, explode.
                            level.explode(null, blockPos.getX()+0.5, blockPos.getY()+1, blockPos.getZ()+0.5, 1.5f, Level.ExplosionInteraction.NONE);
                        }

                        // Destruction in progress
                        int exceed = (laputaCoreTE.destroyProgress - ANIMATION_TIME);

                        if (exceed % DESTRUCTION_TIME_PER_TICK == 0){
                            int process_tick = exceed / DESTRUCTION_TIME_PER_TICK;
                            for (int i=process_tick*PROGRESS_EACH_TICK; i<(process_tick+1)*PROGRESS_EACH_TICK; i++){
                                if (i >= DESTRUCTION_PATTERN.size()){
                                    break;
                                }
                                ArrayList<Integer> pos = DESTRUCTION_PATTERN.get(i);
                                BlockPos target = blockPos.offset(pos.get(0), pos.get(1), pos.get(2));
                                if(! DESTRUCTION_BLACKLIST.contains(ForgeRegistries.BLOCKS.getKey(level.getBlockState(target).getBlock()).toString())){
                                    level.destroyBlock(target, drops);
                                }
                            }
                        }
                    }
                }

                laputaCoreTE.destroyProgress ++;
                laputaCoreTE.setChanged();
            }
            else {
                if(laputaCoreTE.isActive()){
                    if(ConfigCommon.NO_GRIEF_IN_CASTLE.get() && level.getGameTime() % 40 == 0){
                        for (Player playerEntity: level.players()){
                            if(playerEntity.level().dimension().location().toString().equals("minecraft:overworld") && playerEntity.blockPosition().closerThan(laputaCoreTE.getBlockPos(), ConfigCommon.LAPUTA_CORE_EFFECT_RANGE.get())){
                                playerEntity.addEffect(new MobEffectInstance(EffectRegister.SACRED_CASTLE_EFFECT.get(), 100));
                            }
                        }
                        Channel.INSTANCE.send(PacketDistributor.NEAR.with(PacketDistributor.TargetPoint.p(laputaCoreTE.getBlockPos().getX(), laputaCoreTE.getBlockPos().getY(), laputaCoreTE.getBlockPos().getZ(), 20, Level.OVERWORLD)),
                                new LaputaTESynPkt(laputaCoreTE.isDestroying, laputaCoreTE.isActive(), laputaCoreTE.getBlockPos()));
                    }
                }
            }
        }

    }

    public void setDestroying(boolean destroying) {
        isDestroying = destroying;
    }

    public boolean isDestroying() {
        return isDestroying;
    }

    public Vec3 getActivatedInitPos() {
        return activatedInitPos;
    }

    public void setActivatedInitPos(Vec3 activatedInitPos) {
        this.activatedInitPos = activatedInitPos;
    }

    public int getDestroyProgress() {
        return destroyProgress;
    }

    public void setDestroyProgress(int destroyProgress) {
        this.destroyProgress = destroyProgress;
    }

    @Override
    public void onChunkUnloaded() {
        super.onChunkUnloaded();
        if(level!=null && !level.isClientSide()){
            Channel.INSTANCE.send(PacketDistributor.ALL.noArg(), new LaputaTESynPkt(this.isDestroying, this.isActive, this.getBlockPos()));
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if(level!=null && !level.isClientSide()){
            Channel.INSTANCE.send(PacketDistributor.ALL.noArg(), new LaputaTESynPkt(this.isDestroying, this.isActive, this.getBlockPos()));
        }
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        CompoundTag nbt = new CompoundTag();
        nbt.putBoolean("is_active", isActive());
        nbt.putBoolean("isDestroying", isDestroying);
        nbt.putInt("destroyProgress", destroyProgress);
        return nbt;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);
        this.setActive(tag.getBoolean("is_active"));
        this.setDestroying(tag.getBoolean("isDestroying"));
        this.destroyProgress = tag.getInt("destroyProgress");
    }

    @Override
    public @NotNull void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putBoolean("is_active", isActive());
        tag.putBoolean("isDestroying", isDestroying);
        tag.putInt("destroyProgress", destroyProgress);
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        this.setActive(tag.getBoolean("is_active"));
        this.setDestroying(tag.getBoolean("isDestroying"));
        this.destroyProgress = tag.getInt("destroyProgress");
    }
}
