package com.song.castle_in_the_sky.items;

import com.song.castle_in_the_sky.CastleInTheSky;
import com.song.castle_in_the_sky.blocks.BlockRegister;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.song.castle_in_the_sky.CastleInTheSky.MOD_ID;
import static net.minecraft.core.registries.Registries.CREATIVE_MODE_TAB;

public class ItemsRegister {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(CastleInTheSky.MOD_ID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(CREATIVE_MODE_TAB, CastleInTheSky.MOD_ID);

    public static final DeferredItem<LevitationStone> LEVITATION_STONE = ITEMS.registerItem("levitation_stone", LevitationStone::new);

    public static final DeferredItem<BlockItem> RED_DOOR = ITEMS.registerSimpleBlockItem(BlockRegister.RED_DOOR);
    public static final DeferredItem<BlockItem> BLUE_DOOR = ITEMS.registerSimpleBlockItem(BlockRegister.BLUE_DOOR);
    public static final DeferredItem<BlockItem> YELLOW_DOOR = ITEMS.registerSimpleBlockItem(BlockRegister.YELLOW_DOOR);
    public static final DeferredItem<BlockItem> FAKE_BEACON = ITEMS.registerSimpleBlockItem(BlockRegister.FAKE_BEACON, new Item.Properties().rarity(Rarity.RARE));
    public static final DeferredItem<BlockItem> LAPUTA_MINIATURE = ITEMS.registerSimpleBlockItem(BlockRegister.LAPUTA_MINIATURE, new Item.Properties().rarity(Rarity.EPIC));

    public static final DeferredItem<KeyItem> RED_KEY = ITEMS.registerItem("red_key", KeyItem::new);
    public static final DeferredItem<KeyItem> BLUE_KEY = ITEMS.registerItem("blue_key", KeyItem::new);
    public static final DeferredItem<KeyItem> YELLOW_KEY = ITEMS.registerItem("yellow_key", KeyItem::new);

    public static final DeferredItem<BlockItem> LAPUTA_CORE = ITEMS.registerSimpleBlockItem(BlockRegister.LAPUTA_CORE);
    public static final DeferredItem<Item> LAPUTA_CORE_ORB = ITEMS.registerSimpleItem("laputa_core_orb");

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> CASTLE_IN_THE_SKY_TAB = CREATIVE_TABS.register("example", () -> CreativeModeTab.builder()
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
