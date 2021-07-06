package com.song.castle_in_the_sky.blocks.tile_entities;

import com.song.castle_in_the_sky.effects.EffectRegister;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.world.server.ServerWorld;

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
                    if(playerEntity.blockPosition().closerThan(this.getBlockPos(), 100)){
                        playerEntity.addEffect(new EffectInstance(EffectRegister.SACRED_CASTLE_EFFECT.get(), 40));
                    }
                }
            }
        }
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public CompoundNBT save(CompoundNBT nbt) {
        nbt.putBoolean("is_active", isActive());
        return super.save(nbt);
    }

    @Override
    public void load(BlockState blockState, CompoundNBT nbt) {
        this.setActive(nbt.getBoolean("is_active"));
        super.load(blockState, nbt);
    }

}
