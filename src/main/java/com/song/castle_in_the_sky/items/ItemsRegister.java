package com.song.castle_in_the_sky.items;

import com.song.castle_in_the_sky.CastleInTheSky;
import com.song.castle_in_the_sky.blocks.BlockRegister;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ItemsRegister {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, CastleInTheSky.MOD_ID);

    public static final RegistryObject<Item> LEVITATION_STONE = ITEMS.register("levitation_stone", LevitationStone::new);

    public static final RegistryObject<Item> RED_DOOR = ITEMS.register("red_door", ()->new BlockItem(BlockRegister.RED_DOOR.get(), new Item.Properties().tab(CastleInTheSky.ITEM_GROUP)));
    public static final RegistryObject<Item> BLUE_DOOR = ITEMS.register("blue_door", ()->new BlockItem(BlockRegister.BLUE_DOOR.get(), new Item.Properties().tab(CastleInTheSky.ITEM_GROUP)));
    public static final RegistryObject<Item> YELLOW_DOOR = ITEMS.register("yellow_door", ()->new BlockItem(BlockRegister.YELLOW_DOOR.get(), new Item.Properties().tab(CastleInTheSky.ITEM_GROUP)));
    public static final RegistryObject<Item> FAKE_BEACON = ITEMS.register("fake_beacon", ()->new BlockItem(BlockRegister.FAKE_BEACON.get(), new Item.Properties().tab(CastleInTheSky.ITEM_GROUP).rarity(Rarity.RARE)));

    public static final RegistryObject<Item> RED_KEY = ITEMS.register("red_key", KeyItem::new);
    public static final RegistryObject<Item> BLUE_KEY = ITEMS.register("blue_key", KeyItem::new);
    public static final RegistryObject<Item> YELLOW_KEY = ITEMS.register("yellow_key", KeyItem::new);

    public static final RegistryObject<Item> LAPUTA_CORE = ITEMS.register("laputa_core", ()->new BlockItem(BlockRegister.LAPUTA_CORE.get(), new Item.Properties().tab(CastleInTheSky.ITEM_GROUP)));
    public static final RegistryObject<Item> LAPUTA_CORE_ORB = ITEMS.register("laputa_core_orb", ()->new Item(new Item.Properties()));
}
