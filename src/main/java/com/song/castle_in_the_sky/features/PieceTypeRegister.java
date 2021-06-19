package com.song.castle_in_the_sky.features;

import com.song.castle_in_the_sky.CastleInTheSky;
import com.song.castle_in_the_sky.decrepcated.CastlePieces;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;

import static net.minecraft.world.gen.feature.structure.IStructurePieceType.setPieceId;

public class PieceTypeRegister {
    public static IStructurePieceType CASTLE_IN_THE_SKY = setPieceId(CastlePieces.Piece::new, CastleInTheSky.MOD_ID+":castle_in_the_sky");
}
