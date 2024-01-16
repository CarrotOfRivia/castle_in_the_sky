package com.song.castle_in_the_sky.blocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;

public class FakeBeacon extends Block {
    public FakeBeacon() {
        super(Properties.of().instrument(NoteBlockInstrument.HAT).mapColor(MapColor.DIAMOND).strength(3.0F).lightLevel((p)->15).noOcclusion());
    }
}
