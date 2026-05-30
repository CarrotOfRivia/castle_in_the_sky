package com.song.castle_in_the_sky.blocks;

import com.song.castle_in_the_sky.items.ItemsRegister;
import net.minecraft.world.item.Item;

public class BlueDoor extends LockedDoor {
    protected BlueDoor(Properties properties) {
        super(properties);
    }

    @Override
    protected boolean isKeyItem(Item item) {
        return item==ItemsRegister.BLUE_KEY.get();
    }
}
