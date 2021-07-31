package com.song.castle_in_the_sky.utils;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public record MyTradingRecipe(ForgeConfigSpec.ConfigValue<String> resItem1,
                              ForgeConfigSpec.ConfigValue<String> resItem2,
                              ForgeConfigSpec.ConfigValue<String> resOutput,
                              ForgeConfigSpec.ConfigValue<String> resProfession,
                              ForgeConfigSpec.IntValue price1Min,
                              ForgeConfigSpec.IntValue price1Max,
                              ForgeConfigSpec.IntValue price2Min,
                              ForgeConfigSpec.IntValue price2Max,
                              ForgeConfigSpec.IntValue outputMin,
                              ForgeConfigSpec.IntValue outputMax,
                              ForgeConfigSpec.IntValue level) {

    public String getStringProfession() {
        return resProfession.get();
    }

    // Only called after registering everything
    public Item getItem1() {
        if ("null".equals(resItem1.get())) {
            return null;
        }
        return ForgeRegistries.ITEMS.getValue(new ResourceLocation(resItem1.get()));
    }

    public Item getItem2() {
        if ("null".equals(resItem2.get())) {
            return null;
        }
        return ForgeRegistries.ITEMS.getValue(new ResourceLocation(resItem2.get()));
    }

    public Item getOutput() {
        if ("null".equals(resOutput.get())) {
            return null;
        }
        return ForgeRegistries.ITEMS.getValue(new ResourceLocation(resOutput.get()));
    }

    public VillagerProfession getProfession() {
        return ForgeRegistries.PROFESSIONS.getValue(new ResourceLocation(resProfession.get()));
    }

    public List<ItemStack> getInputStacks() {
        List<ItemStack> output = new ArrayList<>();
        if (getItem1() != null) {
            output.add(new ItemStack(getItem1()));
        }
        if (getItem2() != null) {
            output.add(new ItemStack(getItem2()));
        }
        if (!output.isEmpty()) {
            return output;
        } else {
            return Arrays.asList(new ItemStack(Items.BARRIER), new ItemStack(Items.BARRIER));
        }
    }

    public ItemStack getOutputStack() {
        return new ItemStack(getOutput());
    }

    public int getLevel() {
        return level.get();
    }

    @Override
    public ForgeConfigSpec.IntValue price1Min() {
        return price1Min;
    }

    @Override
    public ForgeConfigSpec.IntValue price1Max() {
        return price1Max;
    }

    @Override
    public ForgeConfigSpec.IntValue price2Max() {
        return price2Max;
    }

    @Override
    public ForgeConfigSpec.IntValue price2Min() {
        return price2Min;
    }
}
