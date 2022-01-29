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
        private static final int INCANTATION_WARNING_CD = 200;

        private boolean incantationWarned;
        private int incantationWarningCD =0;

        public void tick(){
            if (incantationWarningCD > 0){
                incantationWarningCD--;
                if (incantationWarningCD <= 0){
                    setIncantationWarned(false);
                    incantationWarningCD = 0;
                }
            }
        }

        public void setWarningCD(){
            this.incantationWarningCD = INCANTATION_WARNING_CD;
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
            tag.putInt("incantationWarningCD", incantationWarningCD);
            return tag;
        }

        public void load(CompoundTag tag){
            this.incantationWarned = tag.getBoolean("incantationWarned");
            this.incantationWarningCD = tag.getInt("incantationWarningCD");
        }
    }
}
