package com.song.castle_in_the_sky.blocks;

import com.song.castle_in_the_sky.CastleInTheSky;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;

public class BlockRegister {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(CastleInTheSky.MOD_ID);

    public static final DeferredBlock<LockedDoor> RED_DOOR = BLOCKS.registerBlock("red_door", RedDoor::new, properties -> properties
            .mapColor(MapColor.METAL)
            .requiresCorrectToolForDrops()
            .strength(5.0F)
            .noOcclusion()
            .pushReaction(PushReaction.DESTROY));
    public static final DeferredBlock<LockedDoor> BLUE_DOOR = BLOCKS.registerBlock("blue_door", BlueDoor::new, properties -> properties
            .mapColor(MapColor.METAL)
            .requiresCorrectToolForDrops()
            .strength(5.0F)
            .noOcclusion()
            .pushReaction(PushReaction.DESTROY));
    public static final DeferredBlock<LockedDoor> YELLOW_DOOR = BLOCKS.registerBlock("yellow_door", YellowDoor::new, properties -> properties
            .mapColor(MapColor.METAL)
            .requiresCorrectToolForDrops()
            .strength(5.0F)
            .noOcclusion()
            .pushReaction(PushReaction.DESTROY));

    public static final DeferredBlock<LaputaCore> LAPUTA_CORE = BLOCKS.registerBlock("laputa_core", LaputaCore::new, properties -> properties
            .mapColor(MapColor.METAL)
            .strength(-1.0F, 3600000.0F)
            .lightLevel(state -> state.getValue(LaputaCore.POWERED) ? 15 : 0)
            .noLootTable()
            .noOcclusion());
    public static final DeferredBlock<FakeBeacon> FAKE_BEACON = BLOCKS.registerBlock("fake_beacon", FakeBeacon::new, properties -> properties
            .instrument(NoteBlockInstrument.HAT)
            .mapColor(MapColor.DIAMOND)
            .strength(3.0F)
            .lightLevel(state -> 15)
            .noOcclusion());
    public static final DeferredBlock<LaputaMiniature> LAPUTA_MINIATURE = BLOCKS.registerBlock("laputa_miniature", LaputaMiniature::new, properties -> properties
            .mapColor(MapColor.DIRT)
            .strength(3.0F)
            .lightLevel(state -> 15)
            .noOcclusion());

}
