package com.song.castle_in_the_sky.blocks.tile_entities;

import com.song.castle_in_the_sky.CastleInTheSky;
import com.song.castle_in_the_sky.blocks.BlockRegister;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class TERegister {
    public static final DeferredRegister<TileEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, CastleInTheSky.MOD_ID);

    public static final RegistryObject<TileEntityType<LaputaCoreTE>> LAPUTA_CORE_TE_TYPE = TILE_ENTITIES.register("laputa_core_te", ()->TileEntityType.Builder.of(LaputaCoreTE::new, BlockRegister.LAPUTA_CORE.get()).build(null));
}
