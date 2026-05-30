package com.song.castle_in_the_sky.utils;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.ModConfigSpec;

public record MyTradingRecipe(ModConfigSpec.ConfigValue<String> resItem1,
                              ModConfigSpec.ConfigValue<String> resItem2,
                              ModConfigSpec.ConfigValue<String> resOutput,
                              ModConfigSpec.ConfigValue<String> resProfession,
                              ModConfigSpec.IntValue price1Min,
                              ModConfigSpec.IntValue price1Max,
                              ModConfigSpec.IntValue price2Min,
                              ModConfigSpec.IntValue price2Max,
                              ModConfigSpec.IntValue outputMin,
                              ModConfigSpec.IntValue outputMax,
                              ModConfigSpec.IntValue level) {
    public String getStringProfession() {
        return resProfession.get();
    }

    public Item getItem1() {
        if ("null".equals(resItem1.get())) {
            return null;
        }
        return BuiltInRegistries.ITEM.getValue(Identifier.parse(resItem1.get()));
    }

    public Item getItem2() {
        if ("null".equals(resItem2.get())) {
            return null;
        }
        return BuiltInRegistries.ITEM.getValue(Identifier.parse(resItem2.get()));
    }

    public Item getOutput() {
        if ("null".equals(resOutput.get())) {
            return null;
        }
        return BuiltInRegistries.ITEM.getValue(Identifier.parse(resOutput.get()));
    }

    public int getLevel() {
        return level.get();
    }
}
