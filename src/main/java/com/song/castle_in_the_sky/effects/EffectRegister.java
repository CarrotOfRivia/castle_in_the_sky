package com.song.castle_in_the_sky.effects;

import com.song.castle_in_the_sky.CastleInTheSky;
import net.minecraft.potion.Effect;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class EffectRegister {
    public static final DeferredRegister<Effect> EFFECT = DeferredRegister.create(ForgeRegistries.POTIONS, CastleInTheSky.MOD_ID);

    public static final RegistryObject<SacredCastle> SACRED_CASTLE_EFFECT = EFFECT.register("sacred_castle_effect", SacredCastle::new);
}
