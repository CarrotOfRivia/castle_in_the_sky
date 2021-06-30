package com.song.castle_in_the_sky.blocks;

import com.song.castle_in_the_sky.CastleInTheSky;
import net.minecraft.block.Block;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class BlockRegister {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, CastleInTheSky.MOD_ID);

    public static final RegistryObject<LockedDoor> RED_DOOR = BLOCKS.register("red_door", RedDoor::new);
    public static final RegistryObject<LockedDoor> BLUE_DOOR = BLOCKS.register("blue_door", BlueDoor::new);
    public static final RegistryObject<LockedDoor> YELLOW_DOOR = BLOCKS.register("yellow_door", YellowDoor::new);
}