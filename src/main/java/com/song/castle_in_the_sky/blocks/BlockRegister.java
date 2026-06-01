package com.song.castle_in_the_sky.blocks;

import com.song.castle_in_the_sky.CastleInTheSky;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class BlockRegister {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(CastleInTheSky.MOD_ID);

    public static final DeferredBlock<LockedDoor> RED_DOOR = BLOCKS.registerBlock("red_door", RedDoor::new);
    public static final DeferredBlock<LockedDoor> BLUE_DOOR = BLOCKS.registerBlock("blue_door", BlueDoor::new);
    public static final DeferredBlock<LockedDoor> YELLOW_DOOR = BLOCKS.registerBlock("yellow_door", YellowDoor::new);

    public static final DeferredBlock<LaputaCore> LAPUTA_CORE = BLOCKS.registerBlock("laputa_core", LaputaCore::new);
    public static final DeferredBlock<FakeBeacon> FAKE_BEACON = BLOCKS.registerBlock("fake_beacon", FakeBeacon::new);
    public static final DeferredBlock<LaputaMiniature> LAPUTA_MINIATURE = BLOCKS.registerBlock("laputa_miniature", LaputaMiniature::new);

}
