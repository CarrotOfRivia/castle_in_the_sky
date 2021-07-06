package com.song.castle_in_the_sky.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ConfigCommon {
    public static ForgeConfigSpec COMMON;
    public static ForgeConfigSpec.IntValue CASTLE_HEIGHT;
    public static ForgeConfigSpec.IntValue CASTLE_SPAWN_PROOF;
    public static ForgeConfigSpec.IntValue CASTLE_AVG_DIST_CHUNK;
    public static ForgeConfigSpec.IntValue CASTLE_MIN_DIST_CHUNK;
    public static ForgeConfigSpec.IntValue LAPUTA_CORE_EFFECT_RANGE;

    public static ForgeConfigSpec.DoubleValue YELLOW_KEY_DROP_RATE;
    public static ForgeConfigSpec.DoubleValue BLUE_KEY_DROP_RATE;
    public static ForgeConfigSpec.DoubleValue RED_KEY_DROP_RATE;

    static {
        ForgeConfigSpec.Builder CONFIG_BUILDER = new ForgeConfigSpec.Builder();
        CASTLE_HEIGHT = CONFIG_BUILDER.comment("The height of generated castle, recommended value is below $WORLD_HEIGHT - 144").defineInRange("castle_height", 110, -99999999, 99999999);
        CASTLE_SPAWN_PROOF = CONFIG_BUILDER.comment("the minimum distance between castle and 0,0").defineInRange("castle_spawn_proof", 10000, 0, 99999999);

        CASTLE_AVG_DIST_CHUNK = CONFIG_BUILDER.comment("average distance apart in chunks between spawn attempts").defineInRange("castle_avg_dist_chunk", 500, 0, 99999999);
        CASTLE_MIN_DIST_CHUNK = CONFIG_BUILDER.comment("minimum distance apart in chunks between spawn attempts").defineInRange("castle_min_dist_chunk", 300, 0, 99999999);

        LAPUTA_CORE_EFFECT_RANGE = CONFIG_BUILDER.comment("Effect Range of Laputa Core").defineInRange("laputa_core_effect_range", 100, 0, 99999999);

        YELLOW_KEY_DROP_RATE = CONFIG_BUILDER.defineInRange("yellow_key_dro_rate", 0.3, 0, 1.);
        BLUE_KEY_DROP_RATE = CONFIG_BUILDER.defineInRange("blue_key_dro_rate", 0.1, 0, 1.);
        RED_KEY_DROP_RATE = CONFIG_BUILDER.defineInRange("red_key_dro_rate", 0.02, 0, 1.);
        COMMON = CONFIG_BUILDER.build();
    }
}
