package com.song.castle_in_the_sky.blocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

public class LaputaMiniature extends Block {
    public LaputaMiniature(BlockBehaviour.Properties properties) {
        super(properties.mapColor(MapColor.DIRT).strength(3.0F).lightLevel((p)->15).noOcclusion());
    }
}
