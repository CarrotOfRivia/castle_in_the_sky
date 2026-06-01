package com.song.castle_in_the_sky.effects;

import com.song.castle_in_the_sky.CastleInTheSky;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public class EffectRegister {
    public static final DeferredRegister<MobEffect> EFFECT = DeferredRegister.create(BuiltInRegistries.MOB_EFFECT, CastleInTheSky.MOD_ID);

    public static final DeferredHolder<MobEffect, SacredCastle> SACRED_CASTLE_EFFECT = EFFECT.register("sacred_castle_effect", SacredCastle::new);
}
