package com.song.castle_in_the_sky.blocks;

import com.song.castle_in_the_sky.items.ItemsRegister;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

public class YellowDoor extends LockedDoor {
    protected YellowDoor() {
        super(Properties.of(Material.METAL, MaterialColor.METAL).requiresCorrectToolForDrops().strength(5.0F).sound(SoundType.METAL).noOcclusion());
    }

    @Override
    protected boolean isKeyItem(Item item) {
        return item==ItemsRegister.YELLOW_KEY.get();
    }
}
