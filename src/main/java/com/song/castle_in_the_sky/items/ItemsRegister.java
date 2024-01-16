package com.song.castle_in_the_sky.items;

import com.song.castle_in_the_sky.CastleInTheSky;
import com.song.castle_in_the_sky.blocks.BlockRegister;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.song.castle_in_the_sky.CastleInTheSky.MOD_ID;
import static net.minecraft.core.registries.Registries.CREATIVE_MODE_TAB;

public class ItemsRegister {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, CastleInTheSky.MOD_ID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(CREATIVE_MODE_TAB, CastleInTheSky.MOD_ID);

    public static final RegistryObject<Item> LEVITATION_STONE = ITEMS.register("levitation_stone", LevitationStone::new);

    public static final RegistryObject<Item> RED_DOOR = ITEMS.register("red_door", ()->new BlockItem(BlockRegister.RED_DOOR.get(), new Item.Properties()));
    public static final RegistryObject<Item> BLUE_DOOR = ITEMS.register("blue_door", ()->new BlockItem(BlockRegister.BLUE_DOOR.get(), new Item.Properties()));
    public static final RegistryObject<Item> YELLOW_DOOR = ITEMS.register("yellow_door", ()->new BlockItem(BlockRegister.YELLOW_DOOR.get(), new Item.Properties()));
    public static final RegistryObject<Item> FAKE_BEACON = ITEMS.register("fake_beacon", ()->new BlockItem(BlockRegister.FAKE_BEACON.get(), new Item.Properties().rarity(Rarity.RARE)));
    public static final RegistryObject<Item> LAPUTA_MINIATURE = ITEMS.register("laputa_miniature", ()->new BlockItem(BlockRegister.LAPUTA_MINIATURE.get(), new Item.Properties().rarity(Rarity.EPIC)));

    public static final RegistryObject<Item> RED_KEY = ITEMS.register("red_key", KeyItem::new);
    public static final RegistryObject<Item> BLUE_KEY = ITEMS.register("blue_key", KeyItem::new);
    public static final RegistryObject<Item> YELLOW_KEY = ITEMS.register("yellow_key", KeyItem::new);

    public static final RegistryObject<Item> LAPUTA_CORE = ITEMS.register("laputa_core", ()->new BlockItem(BlockRegister.LAPUTA_CORE.get(), new Item.Properties()));
    public static final RegistryObject<Item> LAPUTA_CORE_ORB = ITEMS.register("laputa_core_orb", ()->new Item(new Item.Properties()));

    public static final RegistryObject<CreativeModeTab> CASTLE_IN_THE_SKY_TAB = CREATIVE_TABS.register("example", () -> CreativeModeTab.builder()
            // Set name of tab to display
            .title(Component.translatable("item_group." + MOD_ID + ".item_group"))
            // Set icon of creative tab
            .icon(() -> new ItemStack(ItemsRegister.LEVITATION_STONE.get()))
            // Add default items to tab
            .displayItems((params, output) -> {
                output.accept(ItemsRegister.LEVITATION_STONE.get());
                output.accept(ItemsRegister.RED_DOOR.get());
                output.accept(ItemsRegister.BLUE_DOOR.get());
                output.accept(ItemsRegister.YELLOW_DOOR.get());

                output.accept(ItemsRegister.LAPUTA_MINIATURE.get());
                output.accept(ItemsRegister.FAKE_BEACON.get());
                output.accept(ItemsRegister.RED_KEY.get());
                output.accept(ItemsRegister.BLUE_KEY.get());
                output.accept(ItemsRegister.YELLOW_KEY.get());

                output.accept(ItemsRegister.LAPUTA_CORE.get());
                output.accept(ItemsRegister.LAPUTA_CORE_ORB.get());
            })
            .build()
    );
}
