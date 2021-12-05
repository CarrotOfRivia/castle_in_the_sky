package com.song.castle_in_the_sky.features;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.song.castle_in_the_sky.CastleInTheSky;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.StructureSettings;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.JigsawConfiguration;

import java.util.Objects;

/**
 * Hugely inspired from this tutorial: https://github.com/TelepathicGrunt/StructureTutorialMod
 */

public class StructureFeatureRegister {

    public static final ConfiguredStructureFeature<JigsawConfiguration, ? extends StructureFeature<JigsawConfiguration>> CONFIGURED_CASTLE_IN_THE_SKY = StructureRegister.CASTLE_IN_THE_SKY.get().configured(new JigsawConfiguration(() -> {
        return CastleStructure.START;
    }, 7));

    /**
     * Registers the configured structure which is what gets added to the biomes.
     * Noticed we are not using a forge registry because there is none for configured structures.
     *
     * We can register configured structures at any time before a world is clicked on and made.
     * But the best time to register configured features by code is honestly to do it in FMLCommonSetupEvent.
     */
    public static void registerConfiguredStructures() {
        Registry<ConfiguredStructureFeature<?, ?>> registry = BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE;
        Registry.register(registry, new ResourceLocation(CastleInTheSky.MOD_ID, "castle_in_the_sky"), CONFIGURED_CASTLE_IN_THE_SKY);
    }

}
