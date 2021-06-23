package com.song.castle_in_the_sky.events;

import com.song.castle_in_the_sky.features.StructureFeatureRegister;
import com.song.castle_in_the_sky.items.ItemsRegister;
import com.song.castle_in_the_sky.utils.RandomTradeBuilder;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.item.Items;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ServerEvents {

    @SubscribeEvent
    public void onBiomeLoading(BiomeLoadingEvent event){
        if(event.getCategory() == Biome.Category.OCEAN){
            event.getGeneration().addStructureStart(StructureFeatureRegister.CONFIGURED_CASTLE_IN_THE_SKY);
        }
    }

    @SubscribeEvent
    public void onVillageTradeRegister(VillagerTradesEvent event){
        if(event.getType() == VillagerProfession.CARTOGRAPHER){
            event.getTrades().get(5).add(
                    new RandomTradeBuilder(64, 25, 0.05f)
                            .setPrice(Items.EMERALD, 40, 64)
                            .setPrice2(Items.COMPASS, 1, 1)
                            .setForSale(ItemsRegister.LEVITATION_STONE.get(), 1, 1)
                            .build());
        }
    }

    @SubscribeEvent
    public void addLootTable(LootTableLoadEvent event){

    }
}
