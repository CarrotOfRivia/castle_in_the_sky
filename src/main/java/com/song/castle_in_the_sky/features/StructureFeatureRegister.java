package com.song.castle_in_the_sky.features;

import com.song.castle_in_the_sky.CastleInTheSky;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;

public class StructureFeatureRegister {

    public static final ConfiguredStructureFeature<NoneFeatureConfiguration, ? extends StructureFeature<NoneFeatureConfiguration>> CONFIGURED_CASTLE_IN_THE_SKY = StructureRegister.CASTLE_IN_THE_SKY.get().configured(NoneFeatureConfiguration.INSTANCE);

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

        /* Ok so, this part may be hard to grasp but basically, just add your structure to this to
         * prevent any sort of crash or issue with other mod's custom ChunkGenerators. If they use
         * FlatGenerationSettings.STRUCTURE_FEATURES in it and you don't add your structure to it, the game
         * could crash later when you attempt to add the StructureSeparationSettings to the dimension.
         *
         * (It would also crash with superflat worldtype if you omit the below line
         * and attempt to add the structure's StructureSeparationSettings to the world)
         *
         * Note: If you want your structure to spawn in superflat, remove the FlatChunkGenerator check
         * in StructureTutorialMain.addDimensionalSpacing and then create a superflat world, exit it,
         * and re-enter it and your structures will be spawning. I could not figure out why it needs
         * the restart but honestly, superflat is really buggy and shouldn't be your main focus in my opinion.
         *
         * Requires AccessTransformer ( see resources/META-INF/accesstransformer.cfg )
         */
        FlatLevelGeneratorSettings.STRUCTURE_FEATURES.put(StructureRegister.CASTLE_IN_THE_SKY.get(), CONFIGURED_CASTLE_IN_THE_SKY);
    }

}
