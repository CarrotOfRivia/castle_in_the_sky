package com.song.castle_in_the_sky.blocks.block_entities;

import com.song.castle_in_the_sky.blocks.BlockRegister;
import com.song.castle_in_the_sky.config.ConfigCommon;
import com.song.castle_in_the_sky.effects.EffectRegister;
import com.song.castle_in_the_sky.items.ItemsRegister;
import com.song.castle_in_the_sky.network.Channel;
import com.song.castle_in_the_sky.network.LaputaTESynPkt;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.PacketDistributor;
import org.checkerframework.checker.units.qual.A;

import java.util.*;

public class LaputaCoreBE extends BlockEntity {
    private boolean isActive=false;

    private boolean isDestroying=false;
    private int destroyProgress = 0;

    private static final int ANIMATION_TIME = 20;
    private static final int DESTRUCTION_TIME = 60;
    private static final int DESTROY_MAX = ANIMATION_TIME + DESTRUCTION_TIME;
    private static final int RADIUS = 20;
    private static final int RADIUS2 = RADIUS * RADIUS;
    private static final int HEIGHT_MIN = -10;
    private static final int HEIGHT_MAX = 20;
    private static final ArrayList<ArrayList<Integer>> DestructionPattern = new ArrayList<>();
    private static final int PROGRESS_EACH_TICK;
    static {
        for(int dx = -RADIUS; dx<=RADIUS; dx++){
            for(int dy = HEIGHT_MIN; dy<=HEIGHT_MAX; dy++){
                for(int dz = -RADIUS; dz<=RADIUS; dz++){
                    if (dx*dx + dz*dz <= RADIUS2){
                        ArrayList<Integer> tmp = new ArrayList<>(Arrays.asList(dx, dy, dz));
                        DestructionPattern.add(tmp);
                    }
                }
            }
        }

        Collections.shuffle(DestructionPattern);

        PROGRESS_EACH_TICK = DestructionPattern.size() / DESTRUCTION_TIME + 1;
    }

    public LaputaCoreBE(BlockPos pos, BlockState state){
        super(TERegister.LAPUTA_CORE_TE_TYPE.get(), pos, state);
    }

    public static void tick(Level level, BlockPos blockPos, BlockState blockState, LaputaCoreBE laputaCoreTE) {
        if(!level.isClientSide()){
            if (laputaCoreTE.isDestroying){
                if (laputaCoreTE.destroyProgress >= ANIMATION_TIME){
                    if(laputaCoreTE.destroyProgress >= DESTROY_MAX){
                        level.destroyBlock(blockPos, false);
                        level.addFreshEntity(new ItemEntity(level, blockPos.getX(), blockPos.getY(), blockPos.getZ(), new ItemStack(ItemsRegister.LAPUTA_MINIATURE.get())));
                    }
                    else {
                        // Destruction in progress
                        int process_tick = laputaCoreTE.destroyProgress - ANIMATION_TIME;
                        for (int i=process_tick*PROGRESS_EACH_TICK; i<(process_tick+1)*PROGRESS_EACH_TICK; i++){
                            if (i >= DestructionPattern.size()){
                                break;
                            }
                            ArrayList<Integer> pos = DestructionPattern.get(i);
                            BlockPos target = blockPos.offset(pos.get(0), pos.get(1), pos.get(2));
                            if(level.getBlockState(target).getBlock() != BlockRegister.LAPUTA_CORE.get()){
                                level.destroyBlock(target, false);
                            }
                        }
                    }
                }

                laputaCoreTE.destroyProgress ++;
            }
            else {
                if(laputaCoreTE.isActive()){
                    if(ConfigCommon.NO_GRIEF_IN_CASTLE.get() && level.getGameTime() % 40 == 0){
                        for (Player playerEntity: level.players()){
                            if(playerEntity.level.dimension().location().toString().equals("minecraft:overworld") && playerEntity.blockPosition().closerThan(laputaCoreTE.getBlockPos(), ConfigCommon.LAPUTA_CORE_EFFECT_RANGE.get())){
                                playerEntity.addEffect(new MobEffectInstance(EffectRegister.SACRED_CASTLE_EFFECT.get(), 100));
                            }
                        }
                        Channel.INSTANCE.send(PacketDistributor.NEAR.with(PacketDistributor.TargetPoint.p(laputaCoreTE.getBlockPos().getX(), laputaCoreTE.getBlockPos().getY(), laputaCoreTE.getBlockPos().getZ(), 20, Level.OVERWORLD)), new LaputaTESynPkt(laputaCoreTE.isActive(), laputaCoreTE.getBlockPos()));
                    }
                }
            }
        }

    }

    public void setDestroying(boolean destroying) {
        isDestroying = destroying;
    }

    @Override
    public void onChunkUnloaded() {
        super.onChunkUnloaded();
        if(level!=null && !level.isClientSide()){
            Channel.INSTANCE.send(PacketDistributor.ALL.noArg(), new LaputaTESynPkt(this.isActive, this.getBlockPos()));
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if(level!=null && !level.isClientSide()){
            Channel.INSTANCE.send(PacketDistributor.ALL.noArg(), new LaputaTESynPkt(this.isActive, this.getBlockPos()));
        }
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag nbt = new CompoundTag();
        nbt.putBoolean("is_active", isActive());
        return nbt;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);
        this.setActive(tag.getBoolean("is_active"));
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        super.save(tag);
        tag.putBoolean("is_active", isActive());
        return tag;
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.setActive(tag.getBoolean("is_active"));
    }
}
