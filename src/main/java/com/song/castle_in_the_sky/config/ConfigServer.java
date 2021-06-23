package com.song.castle_in_the_sky.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ConfigServer {
    public static ForgeConfigSpec SERVER;

    public static ForgeConfigSpec.IntValue CASTLE_SPAWN_PROOF;

    static {
        ForgeConfigSpec.Builder CONFIG_BUILDER = new ForgeConfigSpec.Builder();
        CASTLE_SPAWN_PROOF = CONFIG_BUILDER.comment("the minimum distance between castle and 0,0").defineInRange("castle_spawn_proof", 10000, 0, 99999999);
        SERVER = CONFIG_BUILDER.build();
    }
}
