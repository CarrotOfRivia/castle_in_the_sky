package com.song.castle_in_the_sky.config;

import net.neoforged.neoforge.common.ModConfigSpec;

@Deprecated
public class ConfigServer {
    // It works great, but people seem to have a hard time finding server config files, so I removed all of them into common config
    public static ModConfigSpec SERVER;

    public static ModConfigSpec.IntValue CASTLE_SPAWN_PROOF;

    static {
        ModConfigSpec.Builder CONFIG_BUILDER = new ModConfigSpec.Builder();
        CASTLE_SPAWN_PROOF = CONFIG_BUILDER.comment("the minimum distance between castle and 0,0").defineInRange("castle_spawn_proof", 10000, 0, 99999999);

        SERVER = CONFIG_BUILDER.build();
    }
}
