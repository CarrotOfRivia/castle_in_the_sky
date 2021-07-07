package com.song.castle_in_the_sky.blocks;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;

public class FakeBeacon extends Block {
    public FakeBeacon() {
        super(AbstractBlock.Properties.of(Material.GLASS, MaterialColor.DIAMOND).strength(3.0F).lightLevel((p)->15).noOcclusion());
    }
}
