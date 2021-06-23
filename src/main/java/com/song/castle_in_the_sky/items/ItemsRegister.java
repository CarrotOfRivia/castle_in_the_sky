package com.song.castle_in_the_sky.items;

import com.song.castle_in_the_sky.CastleInTheSky;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemsRegister {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, CastleInTheSky.MOD_ID);

    public static final RegistryObject<Item> LEVITATION_STONE = ITEMS.register("levitation_stone", LevitationStone::new);

}
