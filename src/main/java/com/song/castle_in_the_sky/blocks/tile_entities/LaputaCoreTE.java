package com.song.castle_in_the_sky.blocks.tile_entities;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.world.server.ServerWorld;

import java.util.Objects;

public class LaputaCoreTE extends TileEntity implements net.minecraft.tileentity.ITickableTileEntity{
    public LaputaCoreTE() {
        super(TERegister.LAPUTA_CORE_TE_TYPE.get());
    }

    @Override
    public void tick() {
        if(!Objects.requireNonNull(getLevel()).isClientSide() && getLevel() instanceof ServerWorld){
            ServerWorld serverWorld = (ServerWorld) getLevel();
            if(serverWorld.getGameTime() % 20 == 0){
                for (PlayerEntity playerEntity: serverWorld.players()){
                    if(playerEntity.blockPosition().closerThan(this.getBlockPos(), 100)){
                        playerEntity.addEffect(new EffectInstance(Effects.GLOWING, 40));
                    }
                }
            }
        }
    }
}
