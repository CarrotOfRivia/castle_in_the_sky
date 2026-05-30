package com.song.castle_in_the_sky.blocks;

import com.song.castle_in_the_sky.items.ItemsRegister;
import net.minecraft.world.item.Item;

public class RedDoor extends LockedDoor {
    protected RedDoor(Properties properties) {
        super(properties);
    }

    @Override
    protected boolean isKeyItem(Item item) {
        return item==ItemsRegister.RED_KEY.get();
    }
}
