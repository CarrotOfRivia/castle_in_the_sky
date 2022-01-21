package com.song.castle_in_the_sky.utils;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.text.html.parser.Entity;

public class CapabilityCastle implements ICapabilitySerializable<CompoundTag> {
    private final Data data = new Data();
    private final LazyOptional<Data> dataOptional = LazyOptional.of(() -> this.data);

    public static final Capability<Entity> CASTLE_CAPS = CapabilityManager.get(new CapabilityToken<Entity>() {
    });


    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return CASTLE_CAPS.orEmpty(cap, this.dataOptional.cast());
    }

    @Override
    public CompoundTag serializeNBT() {
        return null;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {

    }

    public static class Data{

    }
}
