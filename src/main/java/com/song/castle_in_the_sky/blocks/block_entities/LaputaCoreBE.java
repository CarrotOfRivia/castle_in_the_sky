package com.song.castle_in_the_sky.blocks.block_entities;

import com.song.castle_in_the_sky.config.ConfigCommon;
import com.song.castle_in_the_sky.effects.EffectRegister;
import com.song.castle_in_the_sky.network.Channel;
import com.song.castle_in_the_sky.network.LaputaTESynPkt;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fmllegacy.network.PacketDistributor;

import java.util.Objects;

public class LaputaCoreBE extends BlockEntity {
    private boolean isActive=false;

    public LaputaCoreBE(BlockPos pos, BlockState state){
        super(TERegister.LAPUTA_CORE_TE_TYPE.get(), pos, state);
    }

    public static void tick(Level level, BlockPos blockPos, BlockState blockState, LaputaCoreBE laputaCoreTE) {
        if(laputaCoreTE.isActive() && !Objects.requireNonNull(laputaCoreTE.getLevel()).isClientSide() && laputaCoreTE.getLevel() instanceof ServerLevel){
            ServerLevel serverWorld = (ServerLevel) laputaCoreTE.getLevel();
            if(ConfigCommon.NO_GRIEF_IN_CASTLE.get() && serverWorld.getGameTime() % 40 == 0){
                for (Player playerEntity: serverWorld.players()){
                    if(playerEntity.level.dimension().location().toString().equals("minecraft:overworld") && playerEntity.blockPosition().closerThan(laputaCoreTE.getBlockPos(), ConfigCommon.LAPUTA_CORE_EFFECT_RANGE.get())){
                        playerEntity.addEffect(new MobEffectInstance(EffectRegister.SACRED_CASTLE_EFFECT.get(), 100));
                    }
                }
                Channel.INSTANCE.send(PacketDistributor.NEAR.with(PacketDistributor.TargetPoint.p(laputaCoreTE.getBlockPos().getX(), laputaCoreTE.getBlockPos().getY(), laputaCoreTE.getBlockPos().getZ(), 20, Level.OVERWORLD)), new LaputaTESynPkt(laputaCoreTE.isActive(), laputaCoreTE.getBlockPos()));
            }
        }
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
