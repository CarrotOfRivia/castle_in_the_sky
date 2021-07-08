package com.song.castle_in_the_sky.utils;

import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MyTradingRecipe {
    private final ForgeConfigSpec.ConfigValue<String>  resItem1;
    private final ForgeConfigSpec.ConfigValue<String>  resItem2;
    private final ForgeConfigSpec.ConfigValue<String>  resOutput;
    private final ForgeConfigSpec.ConfigValue<String>  resProfession;

    public final ForgeConfigSpec.IntValue price1Min;
    public final ForgeConfigSpec.IntValue price1Max;
    public final ForgeConfigSpec.IntValue price2Min;
    public final ForgeConfigSpec.IntValue price2Max;
    public final ForgeConfigSpec.IntValue outputMin;
    public final ForgeConfigSpec.IntValue outputMax;
    public final ForgeConfigSpec.IntValue level;

    public MyTradingRecipe(ForgeConfigSpec.ConfigValue<String> resItem1, ForgeConfigSpec.ConfigValue<String>  resItem2,
                           ForgeConfigSpec.ConfigValue<String>  resOutput, ForgeConfigSpec.ConfigValue<String>  resProfession,
                           ForgeConfigSpec.IntValue price1Min, ForgeConfigSpec.IntValue price1Max, ForgeConfigSpec.IntValue price2Min,
                           ForgeConfigSpec.IntValue price2Max, ForgeConfigSpec.IntValue outputMin, ForgeConfigSpec.IntValue outputMax, ForgeConfigSpec.IntValue level){
        this.resItem1 = resItem1;
        this.resItem2 = resItem2;
        this.resOutput = resOutput;
        this.resProfession = resProfession;
        this.price1Min = price1Min;
        this.price1Max = price1Max;
        this.price2Min = price2Min;
        this.price2Max = price2Max;
        this.outputMin = outputMin;
        this.outputMax = outputMax;
        this.level = level;
    }

    public String getStringProfession() {
        return resProfession.get();
    }

    // Only called after registering everything
    public Item getItem1() {
        if("null".equals(resItem1.get())){
            return null;
        }
        return ForgeRegistries.ITEMS.getValue(new ResourceLocation(resItem1.get()));
    }

    public Item getItem2() {
        if("null".equals(resItem2.get())){
            return null;
        }
        return ForgeRegistries.ITEMS.getValue(new ResourceLocation(resItem2.get()));
    }

    public Item getOutput() {
        if("null".equals(resOutput.get())){
            return null;
        }
        return ForgeRegistries.ITEMS.getValue(new ResourceLocation(resOutput.get()));
    }

    public VillagerProfession getProfession() {
        return ForgeRegistries.PROFESSIONS.getValue(new ResourceLocation(resProfession.get()));
    }

    public List<ItemStack> getInputStacks(){
        List<ItemStack> output = new ArrayList<>();
        if(getItem1() != null){
            output.add(new ItemStack(getItem1()));
        }
        if(getItem2() != null){
            output.add(new ItemStack(getItem2()));
        }
        if(! output.isEmpty()){
            return output;
        }
        else {
            return Arrays.asList(new ItemStack(Items.BARRIER), new ItemStack(Items.BARRIER));
        }
    }

    public ItemStack getOutputStack() {
        return new ItemStack(getOutput());
    }
}
