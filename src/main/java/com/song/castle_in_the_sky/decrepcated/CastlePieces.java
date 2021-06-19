package com.song.castle_in_the_sky.decrepcated;

import com.song.castle_in_the_sky.features.PieceTypeRegister;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import net.minecraft.world.gen.feature.structure.TemplateStructurePiece;
import net.minecraft.world.gen.feature.template.BlockIgnoreStructureProcessor;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;

import java.util.List;
import java.util.Random;

public class CastlePieces {
    private static final ResourceLocation STRUCTURE_LOCATION_IGLOO = new ResourceLocation("igloo/top");
    public static void addPieces(TemplateManager templateManager, BlockPos blockPos, Rotation rotation, List<StructurePiece> structurePieces, Random random) {
        structurePieces.add(new CastlePieces.Piece(templateManager, STRUCTURE_LOCATION_IGLOO, blockPos, rotation, 0));
    }

    public static class Piece extends TemplateStructurePiece {
        private final ResourceLocation templateLocation;
        private final Rotation rotation;

        public Piece(TemplateManager templateManager, ResourceLocation resourceLocation, BlockPos blockPos, Rotation rotation, int p_i49313_5_) {
            super(PieceTypeRegister.CASTLE_IN_THE_SKY, 0);
            this.templateLocation = resourceLocation;
            this.rotation = rotation;
            this.loadTemplate(templateManager);
        }

        public Piece(TemplateManager templateManager, CompoundNBT nbt) {
            super(PieceTypeRegister.CASTLE_IN_THE_SKY, nbt);
            this.templateLocation = new ResourceLocation(nbt.getString("Template"));
            this.rotation = Rotation.valueOf(nbt.getString("Rot"));
            this.loadTemplate(templateManager);
        }

        private void loadTemplate(TemplateManager p_207614_1_) {
            Template template = p_207614_1_.getOrCreate(this.templateLocation);
            PlacementSettings placementsettings = (new PlacementSettings()).setRotation(this.rotation).setMirror(Mirror.NONE).addProcessor(BlockIgnoreStructureProcessor.STRUCTURE_BLOCK);
            this.setup(template, this.templatePosition, placementsettings);
        }

        @Override
        protected void handleDataMarker(String data, BlockPos blockPos, IServerWorld serverWorld, Random random, MutableBoundingBox mutableBoundingBox) {

        }
    }
}
