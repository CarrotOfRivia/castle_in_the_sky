package com.song.castle_in_the_sky.features;

import com.song.castle_in_the_sky.CastleInTheSky;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.JigsawConfiguration;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Hugely inspired from this tutorial: https://github.com/TelepathicGrunt/StructureTutorialMod
 */

public class StructureRegister {
    public static final DeferredRegister<StructureFeature<?>> STRUCTURES = DeferredRegister.create(ForgeRegistries.STRUCTURE_FEATURES, CastleInTheSky.MOD_ID);
    public static final CastleStructure CASTLE_IN_THE_SKY_RAW = new CastleStructure(JigsawConfiguration.CODEC);

    public static final RegistryObject<StructureFeature<JigsawConfiguration>> CASTLE_IN_THE_SKY = STRUCTURES.register("castle_in_the_sky", ()-> CASTLE_IN_THE_SKY_RAW);

    public static final TagKey<ConfiguredStructureFeature<?, ?>> TAG_CASTLE_IN_THE_SKY = TagKey.create(Registry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY, new ResourceLocation("castle_in_the_sky", "castle_in_the_sky"));

}
