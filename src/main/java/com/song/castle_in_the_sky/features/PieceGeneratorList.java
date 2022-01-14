package com.song.castle_in_the_sky.features;

import net.minecraft.world.level.levelgen.feature.configurations.JigsawConfiguration;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * A wrapper class to generate multiple nbt at once
 */
public record PieceGeneratorList(List<PieceGenerator<JigsawConfiguration>> list) implements PieceGenerator<JigsawConfiguration> {

    @Override
    public void generatePieces(@NotNull StructurePiecesBuilder piecesBuilder, @NotNull Context<JigsawConfiguration> context) {
        for (PieceGenerator<JigsawConfiguration> generator : list) {
            generator.generatePieces(piecesBuilder, context);
        }
    }
}
