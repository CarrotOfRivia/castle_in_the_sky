package com.song.castle_in_the_sky.loot;

import com.song.castle_in_the_sky.CastleInTheSky;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class LootModifierRegister {
    public static final DeferredRegister<GlobalLootModifierSerializer<?>> GLM = DeferredRegister.create(ForgeRegistries.LOOT_MODIFIER_SERIALIZERS, CastleInTheSky.MOD_ID);

//    public static final RegistryObject<StructureModdedLootModifier.Serializer> STRUCTURE_MODDED_LOOT_MODIFIER = GLM.register("import_structure_modded_loot", StructureModdedLootModifier.Serializer::new);
}
