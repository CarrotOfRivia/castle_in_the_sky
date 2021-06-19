package com.song.castle_in_the_sky.events;

import com.song.castle_in_the_sky.features.StructureFeatureRegister;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ServerEvents {

    @SubscribeEvent
    public void onBiomeLoading(BiomeLoadingEvent event){
        event.getGeneration().addStructureStart(StructureFeatureRegister.CONFIGURED_CASTLE_IN_THE_SKY);
    }
}
