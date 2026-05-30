package com.song.castle_in_the_sky.blocks.block_entities;

import com.song.castle_in_the_sky.CastleInTheSky;
import com.song.castle_in_the_sky.blocks.BlockRegister;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public class TERegister {
    public static final DeferredRegister<BlockEntityType<?>> TILE_ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, CastleInTheSky.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LaputaCoreBE>> LAPUTA_CORE_TE_TYPE =
            TILE_ENTITIES.register("laputa_core", () -> new BlockEntityType<>(LaputaCoreBE::new, BlockRegister.LAPUTA_CORE.get()));
}
