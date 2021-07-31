package com.song.castle_in_the_sky.blocks.tile_entities;

import com.song.castle_in_the_sky.config.ConfigCommon;
import com.song.castle_in_the_sky.effects.EffectRegister;
import com.song.castle_in_the_sky.network.Channel;
import com.song.castle_in_the_sky.network.LaputaTESynPkt;
import net.minecraft.block.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.world.World;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.TickingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fmllegacy.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.Objects;

public class LaputaCoreTE extends BlockEntity implements TickingBlockEntity {
    private boolean isActive=false;

    public LaputaCoreTE(BlockPos pos, BlockState state){
        super(TERegister.LAPUTA_CORE_TE_TYPE.get(), pos, state);
    }

    @Override
    public void tick() {
        if(isActive() && !Objects.requireNonNull(getLevel()).isClientSide() && getLevel() instanceof ServerLevel){
            ServerLevel serverWorld = (ServerLevel) getLevel();
            if(ConfigCommon.NO_GRIEF_IN_CASTLE.get() && serverWorld.getGameTime() % 40 == 0){
                for (Player playerEntity: serverWorld.players()){
                    if(playerEntity.level.dimension().location().toString().equals("minecraft:overworld") && playerEntity.blockPosition().closerThan(this.getBlockPos(), ConfigCommon.LAPUTA_CORE_EFFECT_RANGE.get())){
                        playerEntity.addEffect(new MobEffectInstance(EffectRegister.SACRED_CASTLE_EFFECT.get(), 100));
                    }
                }
                Channel.INSTANCE.send(PacketDistributor.NEAR.with(PacketDistributor.TargetPoint.p(this.getBlockPos().getX(), this.getBlockPos().getY(), this.getBlockPos().getZ(), 20, Level.OVERWORLD)), new LaputaTESynPkt(isActive(), this.getBlockPos()));
            }
        }
    }

    @Override
    public BlockPos getPos() {
        return worldPosition;
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
    public CompoundTag save(CompoundTag tag) {
        super.save(tag);
        tag.putBoolean("is_active", isActive());
        return tag;
    }

}
