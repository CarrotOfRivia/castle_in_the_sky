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

public class CapabilityCastle implements ICapabilitySerializable<CompoundTag> {
    private final Data data = new Data();
    private final LazyOptional<Data> dataOptional = LazyOptional.of(() -> this.data);

    public static final Capability<Data> CASTLE_CAPS = CapabilityManager.get(new CapabilityToken<>() {
    });


    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return CASTLE_CAPS.orEmpty(cap, this.dataOptional);
    }

    @Override
    public CompoundTag serializeNBT() {
        return this.data.save();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.data.load(nbt);
    }

    public void invalidate() {
        this.dataOptional.invalidate();
    }

    public static class Data{
        private boolean incantationWarned;
        private int incantationCD=0;

        public void tick(){
            if (incantationCD >= 1){
                incantationCD --;
                if (incantationCD == 0){
                    setIncantationWarned(false);
                }
            }
        }

        public void setIncantationWarned(boolean incantationWarned){
            this.incantationWarned = incantationWarned;
        }

        public boolean isIncantationWarned() {
            return incantationWarned;
        }

        public CompoundTag save(){
            CompoundTag tag = new CompoundTag();
            tag.putBoolean("incantationWarned", incantationWarned);
            tag.putInt("incantationCD", incantationCD);
            return tag;
        }

        public void load(CompoundTag tag){
            this.incantationWarned = tag.getBoolean("incantationWarned");
            this.incantationCD = tag.getInt("incantationCD");
        }
    }
}
