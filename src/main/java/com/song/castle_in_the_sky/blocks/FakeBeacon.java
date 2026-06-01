package com.song.castle_in_the_sky.blocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;

public class FakeBeacon extends Block {
    public FakeBeacon(BlockBehaviour.Properties properties) {
        super(properties.instrument(NoteBlockInstrument.HAT)
                .mapColor(MapColor.DIAMOND)
                .strength(3.0F)
                .lightLevel((p)->15)
                .noOcclusion()
                .isViewBlocking((state, level, pos) -> false)
                .isSuffocating((state, level, pos) -> false));
    }
}
