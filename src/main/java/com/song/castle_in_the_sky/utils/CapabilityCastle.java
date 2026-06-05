package com.song.castle_in_the_sky.utils;

import com.song.castle_in_the_sky.CastleInTheSky;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class CapabilityCastle {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, CastleInTheSky.MOD_ID);
    public static final Supplier<AttachmentType<Data>> CASTLE_CAPS = ATTACHMENT_TYPES.register(
            "castle_caps", () -> AttachmentType.serializable(Data::new).copyOnDeath().build()
    );

    public static class Data implements INBTSerializable<CompoundTag> {
        private static final int INCANTATION_WARNING_CD = 200;

        private boolean incantationWarned;
        private int incantationWarningCD = 0;

        public void tick() {
            if (incantationWarningCD > 0) {
                incantationWarningCD--;
                if (incantationWarningCD <= 0) {
                    setIncantationWarned(false);
                    incantationWarningCD = 0;
                }
            }
        }

        public void setWarningCD() {
            this.incantationWarningCD = INCANTATION_WARNING_CD;
        }

        public void setIncantationWarned(boolean incantationWarned) {
            this.incantationWarned = incantationWarned;
        }

        public boolean isIncantationWarned() {
            return incantationWarned;
        }

        @Override
        public CompoundTag serializeNBT(HolderLookup.Provider provider) {
            CompoundTag tag = new CompoundTag();
            tag.putBoolean("incantationWarned", incantationWarned);
            tag.putInt("incantationWarningCD", incantationWarningCD);
            return tag;
        }

        @Override
        public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag) {
            this.incantationWarned = tag.getBoolean("incantationWarned");
            this.incantationWarningCD = tag.getInt("incantationWarningCD");
        }
    }
}
