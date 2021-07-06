package com.song.castle_in_the_sky.blocks.tile_entities;

import com.song.castle_in_the_sky.config.ConfigCommon;
import com.song.castle_in_the_sky.effects.EffectRegister;
import com.song.castle_in_the_sky.network.Channel;
import com.song.castle_in_the_sky.network.LaputaTESynPkt;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.Objects;

public class LaputaCoreTE extends TileEntity implements net.minecraft.tileentity.ITickableTileEntity{
    private boolean isActive=false;
    public LaputaCoreTE() {
        super(TERegister.LAPUTA_CORE_TE_TYPE.get());
    }

    @Override
    public void tick() {
        if(isActive() && !Objects.requireNonNull(getLevel()).isClientSide() && getLevel() instanceof ServerWorld){
            ServerWorld serverWorld = (ServerWorld) getLevel();
            if(serverWorld.getGameTime() % 40 == 0){
                for (PlayerEntity playerEntity: serverWorld.players()){
                    if(playerEntity.level.dimension().location().toString().equals("minecraft:overworld") && playerEntity.blockPosition().closerThan(this.getBlockPos(), ConfigCommon.LAPUTA_CORE_EFFECT_RANGE.get())){
                        playerEntity.addEffect(new EffectInstance(EffectRegister.SACRED_CASTLE_EFFECT.get(), 100));
                    }
                }
                Channel.INSTANCE.send(PacketDistributor.NEAR.with(PacketDistributor.TargetPoint.p(this.getBlockPos().getX(), this.getBlockPos().getY(), this.getBlockPos().getZ(), 20, World.OVERWORLD)), new LaputaTESynPkt(isActive(), this.getBlockPos()));
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
    public CompoundNBT getUpdateTag() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putBoolean("is_active", isActive());
        return nbt;
    }

    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT tag) {
        setActive(tag.getBoolean("is_active"));
    }

    @Override
    public CompoundNBT save(CompoundNBT tag) {
        super.save(tag);
        tag.putBoolean("is_active", isActive());
        return tag;
    }

    @Override
    public void load(BlockState blockState, CompoundNBT tag) {
        super.load(blockState, tag);
        setActive(tag.getBoolean("is_active"));
    }
}
