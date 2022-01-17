package com.song.castle_in_the_sky.blocks;

import com.song.castle_in_the_sky.CastleInTheSky;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BlockRegister {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, CastleInTheSky.MOD_ID);

    public static final RegistryObject<LockedDoor> RED_DOOR = BLOCKS.register("red_door", RedDoor::new);
    public static final RegistryObject<LockedDoor> BLUE_DOOR = BLOCKS.register("blue_door", BlueDoor::new);
    public static final RegistryObject<LockedDoor> YELLOW_DOOR = BLOCKS.register("yellow_door", YellowDoor::new);

    public static final RegistryObject<LaputaCore> LAPUTA_CORE = BLOCKS.register("laputa_core", LaputaCore::new);
    public static final RegistryObject<FakeBeacon> FAKE_BEACON = BLOCKS.register("fake_beacon", FakeBeacon::new);
    public static final RegistryObject<LaputaMiniature> LAPUTA_MINIATURE = BLOCKS.register("laputa_miniature", LaputaMiniature::new);

}
