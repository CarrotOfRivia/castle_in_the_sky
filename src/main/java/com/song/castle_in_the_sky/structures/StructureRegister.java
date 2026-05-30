package com.song.castle_in_the_sky.structures;

import com.song.castle_in_the_sky.CastleInTheSky;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class StructureRegister {
    public static final DeferredRegister<StructureType<?>> DEFERRED_REGISTRY_STRUCTURE =
            DeferredRegister.create(Registries.STRUCTURE_TYPE, CastleInTheSky.MOD_ID);

    public static final DeferredHolder<StructureType<?>, StructureType<CastleStructure>> CASTLE_IN_THE_SKY =
            DEFERRED_REGISTRY_STRUCTURE.register("castle_in_the_sky", () -> () -> CastleStructure.CODEC);

    public static final TagKey<Structure> CASTLE_IN_THE_SKY_LOCATED =
            TagKey.create(Registries.STRUCTURE, Identifier.fromNamespaceAndPath(CastleInTheSky.MOD_ID, "castle_in_the_sky_located"));
}
