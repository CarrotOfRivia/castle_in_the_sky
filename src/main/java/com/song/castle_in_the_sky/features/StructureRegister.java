package com.song.castle_in_the_sky.features;

import com.song.castle_in_the_sky.CastleInTheSky;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

/**
 * Hugely inspired from this tutorial: https://github.com/TelepathicGrunt/StructureTutorialMod
 */

public class StructureRegister {

    /**
     * We are using the Deferred Registry system to register our structure as this is the preferred way on Forge.
     * This will handle registering the base structure for us at the correct time so we don't have to handle it ourselves.
     */
    public static final DeferredRegister<StructureType<?>> DEFERRED_REGISTRY_STRUCTURE = DeferredRegister.create(Registry.STRUCTURE_TYPE_REGISTRY, CastleInTheSky.MOD_ID);

    /**
     * Registers the base structure itself and sets what its path is. In this case,
     * this base structure will have the resourcelocation of castle_in_the_sky:castle_in_the_sky.
     */
    public static final RegistryObject<StructureType<CastleStructure>> CASTLE_IN_THE_SKY = DEFERRED_REGISTRY_STRUCTURE.register("castle_in_the_sky", () -> () -> CastleStructure.CODEC);
    public static final TagKey<Structure> CASTLE_IN_THE_SKY_LOCATED = TagKey.create(Registry.STRUCTURE_REGISTRY, new ResourceLocation("castle_in_the_sky_located"));
}
