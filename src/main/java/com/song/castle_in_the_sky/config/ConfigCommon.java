package com.song.castle_in_the_sky.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ConfigCommon {
    public static ForgeConfigSpec COMMON;
    public static ForgeConfigSpec.IntValue CASTLE_HEIGHT;

    static {
        ForgeConfigSpec.Builder CONFIG_BUILDER = new ForgeConfigSpec.Builder();
        CASTLE_HEIGHT = CONFIG_BUILDER.comment("The height of generated castle, recommended value is below $WORLD_HEIGHT - 144").defineInRange("castle_height", 110, -99999999, 99999999);

        COMMON = CONFIG_BUILDER.build();
    }
}
