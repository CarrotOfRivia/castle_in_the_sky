package com.song.castle_in_the_sky.features;

import net.minecraft.world.level.levelgen.feature.configurations.JigsawConfiguration;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * A wrapper class to generate multiple nbt at once
 */
public class PieceGeneratorList implements PieceGenerator<JigsawConfiguration> {
    private final List<PieceGenerator<JigsawConfiguration>> list;
    public PieceGeneratorList(List<PieceGenerator<JigsawConfiguration>> list){
        this.list = list;
    }

    @Override
    public void generatePieces(@NotNull StructurePiecesBuilder piecesBuilder, @NotNull Context<JigsawConfiguration> context) {
        for (PieceGenerator<JigsawConfiguration> generator:list){
            generator.generatePieces(piecesBuilder, context);
        }
    }
}
