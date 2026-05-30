package com.song.castle_in_the_sky.blocks.block_entities;

import com.song.castle_in_the_sky.blocks.LaputaCore;
import com.song.castle_in_the_sky.config.ConfigCommon;
import com.song.castle_in_the_sky.effects.EffectRegister;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class LaputaCoreBE extends BlockEntity {
    public LaputaCoreBE(BlockPos pos, BlockState state) {
        super(TERegister.LAPUTA_CORE_TE_TYPE.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, LaputaCoreBE core) {
        if (level.isClientSide() || !state.getValue(LaputaCore.POWERED) || !ConfigCommon.NO_GRIEF_IN_CASTLE.get() || level.getGameTime() % 40 != 0) {
            return;
        }

        for (Player player : level.players()) {
            if (player.blockPosition().closerThan(pos, ConfigCommon.LAPUTA_CORE_EFFECT_RANGE.get())) {
                player.addEffect(new MobEffectInstance(EffectRegister.SACRED_CASTLE_EFFECT, 100));
            }
        }
    }
}
