package com.song.castle_in_the_sky.config;

import net.minecraft.world.level.block.ChestBlock;
import net.minecraftforge.common.ForgeConfigSpec;

@Deprecated
public class ConfigServer {
    // It works great, but people seem to have a hard time finding server config files, so I removed all of them into common config
    public static ForgeConfigSpec SERVER;

    public static ForgeConfigSpec.IntValue CASTLE_SPAWN_PROOF;

    static {
        ForgeConfigSpec.Builder CONFIG_BUILDER = new ForgeConfigSpec.Builder();
        CASTLE_SPAWN_PROOF = CONFIG_BUILDER.comment("the minimum distance between castle and 0,0").defineInRange("castle_spawn_proof", 10000, 0, 99999999);

        SERVER = CONFIG_BUILDER.build();
    }
}
