package com.song.castle_in_the_sky.blocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

public class FakeBeacon extends Block {
    public FakeBeacon() {
        super(Properties.of(Material.GLASS, MaterialColor.DIAMOND).strength(3.0F).lightLevel((p)->15).noOcclusion());
    }
}
