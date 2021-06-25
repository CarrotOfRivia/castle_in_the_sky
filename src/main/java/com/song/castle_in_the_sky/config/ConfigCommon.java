package com.song.castle_in_the_sky.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ConfigCommon {
    public static ForgeConfigSpec COMMON;
    public static ForgeConfigSpec.IntValue CASTLE_HEIGHT;
    public static ForgeConfigSpec.IntValue CASTLE_SPAWN_PROOF;
    public static ForgeConfigSpec.IntValue CASTLE_AVG_DIST_CHUNK;
    public static ForgeConfigSpec.IntValue CASTLE_MIN_DIST_CHUNK;

    static {
        ForgeConfigSpec.Builder CONFIG_BUILDER = new ForgeConfigSpec.Builder();
        CASTLE_HEIGHT = CONFIG_BUILDER.comment("The height of generated castle, recommended value is below $WORLD_HEIGHT - 144").defineInRange("castle_height", 110, -99999999, 99999999);
        CASTLE_SPAWN_PROOF = CONFIG_BUILDER.comment("the minimum distance between castle and 0,0").defineInRange("castle_spawn_proof", 10000, 0, 99999999);

        CASTLE_AVG_DIST_CHUNK = CONFIG_BUILDER.comment("average distance apart in chunks between spawn attempts").defineInRange("castle_avg_dist_chunk", 500, 0, 99999999);
        CASTLE_MIN_DIST_CHUNK = CONFIG_BUILDER.comment("minimum distance apart in chunks between spawn attempts").defineInRange("castle_min_dist_chunk", 300, 0, 99999999);
        COMMON = CONFIG_BUILDER.build();
    }
}
