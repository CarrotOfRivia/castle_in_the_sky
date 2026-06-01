package com.song.castle_in_the_sky.utils;

import com.song.castle_in_the_sky.CastleInTheSky;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.common.util.ValueIOSerializable;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class CapabilityCastle {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, CastleInTheSky.MOD_ID);
    public static final Supplier<AttachmentType<Data>> CASTLE_CAPS = ATTACHMENT_TYPES.register(
            "castle_caps", () -> AttachmentType.serializable(Data::new).copyOnDeath().build()
    );

    public static class Data implements ValueIOSerializable {
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
        public void serialize(ValueOutput output) {
            output.putBoolean("incantationWarned", incantationWarned);
            output.putInt("incantationWarningCD", incantationWarningCD);
        }

        @Override
        public void deserialize(ValueInput input) {
            this.incantationWarned = input.getBooleanOr("incantationWarned", false);
            this.incantationWarningCD = input.getIntOr("incantationWarningCD", 0);
        }
    }
}
