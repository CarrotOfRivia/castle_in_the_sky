package com.song.castle_in_the_sky.effects;

import com.song.castle_in_the_sky.CastleInTheSky;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class EffectRegister {
    public static final DeferredRegister<MobEffect> EFFECT = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, CastleInTheSky.MOD_ID);

    public static final RegistryObject<SacredCastle> SACRED_CASTLE_EFFECT = EFFECT.register("sacred_castle_effect", SacredCastle::new);
}
