package com.song.castle_in_the_sky.structures;

import com.mojang.serialization.MapCodec;
import com.song.castle_in_the_sky.CastleInTheSky;
import com.song.castle_in_the_sky.config.ConfigCommon;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;

import java.util.Optional;

public class CastleStructure extends Structure {
    public static final MapCodec<CastleStructure> CODEC = Structure.simpleCodec(CastleStructure::new);

    public CastleStructure(StructureSettings settings) {
        super(settings);
    }

    @Override
    public Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        int startY = ConfigCommon.CASTLE_HEIGHT.get();
        ChunkPos chunkPos = context.chunkPos();
        BlockPos basePos = new BlockPos(chunkPos.getMinBlockX(), startY, chunkPos.getMinBlockZ());

        if (basePos.closerThan(new Vec3i(0, startY, 0), ConfigCommon.CASTLE_SPAWN_PROOF.get())) {
            CastleInTheSky.LOGGER.info("Castle generation rejected near spawn at chunk {} base {}", chunkPos, basePos);
            return Optional.empty();
        }

        CastleInTheSky.LOGGER.info("Castle generation accepted at chunk {} base {}", chunkPos, basePos);
        return Optional.of(new GenerationStub(basePos, builder -> {
            int pieces = 0;
            for (int shift1 = 0; shift1 < 3; shift1++) {
                for (int shift2 = 0; shift2 < 3; shift2++) {
                    for (int shiftY = 0; shiftY < 3; shiftY++) {
                        String pieceId = "laputa" + shift1 + shiftY + shift2;
                        StructurePoolElement element = StructurePoolElement.single("castle_in_the_sky:" + pieceId)
                                .apply(StructureTemplatePool.Projection.RIGID);
                        BlockPos piecePos = basePos.offset(shift2 * 48, shiftY * 48, shift1 * 48);
                        PoolElementStructurePiece piece = new PoolElementStructurePiece(
                                context.structureTemplateManager(),
                                element,
                                piecePos,
                                element.getGroundLevelDelta(),
                                Rotation.NONE,
                                element.getBoundingBox(context.structureTemplateManager(), piecePos, Rotation.NONE),
                                LiquidSettings.IGNORE_WATERLOGGING
                        );
                        builder.addPiece(piece);
                        pieces++;
                    }
                }
            }
            CastleInTheSky.LOGGER.info("Castle generation added {} legacy NBT pieces from base {}", pieces, basePos);
        }));
    }

    @Override
    public StructureType<?> type() {
        return StructureRegister.CASTLE_IN_THE_SKY.get();
    }
}
