package com.song.castle_in_the_sky.config;

import com.song.castle_in_the_sky.utils.MyTradingRecipe;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.ArrayList;

public class ConfigCommon {
    public static ForgeConfigSpec COMMON;
    public static ForgeConfigSpec.IntValue CASTLE_HEIGHT;
    public static ForgeConfigSpec.IntValue CASTLE_SPAWN_PROOF;
    public static ForgeConfigSpec.DoubleValue LEVITATION_STONE_USE_PERCENT;
    public static ForgeConfigSpec.IntValue CASTLE_AVG_DIST_CHUNK;
    public static ForgeConfigSpec.IntValue CASTLE_MIN_DIST_CHUNK;
    public static ForgeConfigSpec.IntValue LAPUTA_CORE_EFFECT_RANGE;

    public static ForgeConfigSpec.DoubleValue YELLOW_KEY_DROP_RATE;
    public static ForgeConfigSpec.DoubleValue BLUE_KEY_DROP_RATE;
    public static ForgeConfigSpec.DoubleValue RED_KEY_DROP_RATE;
    public static ForgeConfigSpec.BooleanValue NO_GRIEF_IN_CASTLE;

    public static ForgeConfigSpec.BooleanValue SILENT_INCANTATION;
    public static ForgeConfigSpec.BooleanValue DESTRUCTION_DROPS;
    public static ForgeConfigSpec.BooleanValue DISABLE_INCANTATION;

    public static final ArrayList<MyTradingRecipe> MY_TRADING_RECIPES = new ArrayList<>();

    static {
        ForgeConfigSpec.Builder CONFIG_BUILDER = new ForgeConfigSpec.Builder();
        CONFIG_BUILDER.comment("NOTE: as of 1.18.2, castle_avg_dist_chunk and castle_min_dist_chunk are no longer in the config file. You need to use datapacks (https://minecraft.fandom.com/wiki/Custom_world_generation) to config them");

        CONFIG_BUILDER.push("behaviours");
        CASTLE_HEIGHT = CONFIG_BUILDER.comment("The height of generated castle, recommended value is below $WORLD_HEIGHT - 144").defineInRange("castle_height", 174, Integer.MIN_VALUE, Integer.MAX_VALUE);
        CASTLE_SPAWN_PROOF = CONFIG_BUILDER.comment("the minimum distance between castle and 0,0").defineInRange("castle_spawn_proof", 10000, 0, Integer.MAX_VALUE);
        LEVITATION_STONE_USE_PERCENT = CONFIG_BUILDER.comment("Levitation can only be used when the user is bigger than this percent of spawn_proof. This is to prevent lag.").defineInRange("levitation_stone_use_percent", 0.8, 0, Double.MAX_VALUE);

        LAPUTA_CORE_EFFECT_RANGE = CONFIG_BUILDER.comment("Effect Range of Laputa Core").defineInRange("laputa_core_effect_range", 100, 0, Integer.MAX_VALUE);

        YELLOW_KEY_DROP_RATE = CONFIG_BUILDER.defineInRange("yellow_key_dro_rate", 0.3, 0, 1.);
        BLUE_KEY_DROP_RATE = CONFIG_BUILDER.defineInRange("blue_key_dro_rate", 0.1, 0, 1.);
        RED_KEY_DROP_RATE = CONFIG_BUILDER.defineInRange("red_key_dro_rate", 0.02, 0, 1.);

        NO_GRIEF_IN_CASTLE = CONFIG_BUILDER.comment("Player cannot place or destroy blocks in the castle").define("no_grief_in_castle", true);

        DISABLE_INCANTATION = CONFIG_BUILDER.comment("Disable destruction incantation").define("disable_incantation", false);
        SILENT_INCANTATION = CONFIG_BUILDER.comment("Do not say the incantation (eg. BALSE) to public chat.").define("silent_incantation", true);
        DESTRUCTION_DROPS = CONFIG_BUILDER.comment("Destruction drop items").define("destruction_drops", false);
        CONFIG_BUILDER.pop();

        CONFIG_BUILDER.comment("Trading configuration: use the correct item id by pressing F3+H in game. Set 'null' to disable this slot, set both price1 and price2 to 'null' to disable the trading").push("tradings");
        addTrader("cartographer", "castle_in_the_sky:levitation_stone", 5, "minecraft:elytra", 1, 1, "minecraft:recovery_compass", 1, 1, 1, 1, CONFIG_BUILDER);
        CONFIG_BUILDER.pop();

        COMMON = CONFIG_BUILDER.build();
    }

    private static void addTrader(String profession, String output, int level, String price1, int price1Min, int price1Max, String price2, int price2Min, int price2Max, int outputMin, int outputMax, ForgeConfigSpec.Builder builder){
        builder.push(output);
        MY_TRADING_RECIPES.add(new MyTradingRecipe(
                builder.define(output+"_price1", price1),
                builder.define(output+"_price2", price2),
                builder.define(output+"_output", output),
                builder.define(output+"_profession", profession),
                builder.defineInRange(output+"_price1_min", price1Min, 1, 64),
                builder.defineInRange(output+"_price1_max", price1Max, 1, 64),
                builder.defineInRange(output+"_price2_min", price2Min, 1, 64),
                builder.defineInRange(output+"_price2_max", price2Max, 1, 64),
                builder.defineInRange(output+"_output_min", outputMin, 1, 64),
                builder.defineInRange(output+"_output_max", outputMax, 1, 64),
                builder.defineInRange(output+"_level", level, 1, 64)));
        builder.pop();
    }
}
