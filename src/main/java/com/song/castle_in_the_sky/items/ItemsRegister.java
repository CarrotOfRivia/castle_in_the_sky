package com.song.castle_in_the_sky.items;

import com.song.castle_in_the_sky.CastleInTheSky;
import com.song.castle_in_the_sky.blocks.BlockRegister;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemsRegister {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, CastleInTheSky.MOD_ID);

    public static final RegistryObject<Item> LEVITATION_STONE = ITEMS.register("levitation_stone", LevitationStone::new);

    public static final RegistryObject<Item> RED_DOOR = ITEMS.register("red_door", ()->new BlockItem(BlockRegister.RED_DOOR.get(), new Item.Properties().tab(CastleInTheSky.ITEM_GROUP)));
    public static final RegistryObject<Item> BLUE_DOOR = ITEMS.register("blue_door", ()->new BlockItem(BlockRegister.BLUE_DOOR.get(), new Item.Properties().tab(CastleInTheSky.ITEM_GROUP)));
    public static final RegistryObject<Item> YELLOW_DOOR = ITEMS.register("yellow_door", ()->new BlockItem(BlockRegister.YELLOW_DOOR.get(), new Item.Properties().tab(CastleInTheSky.ITEM_GROUP)));

}
