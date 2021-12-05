package com.song.castle_in_the_sky.features;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import net.minecraft.core.*;
import net.minecraft.data.worldgen.Pools;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.JigsawBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.JigsawConfiguration;
import net.minecraft.world.level.levelgen.feature.structures.*;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.apache.commons.lang3.mutable.MutableObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.function.Predicate;

public class CastleFixedRotationPlacement {
    static final Logger LOGGER = LogManager.getLogger();

    public static Optional<PieceGenerator<JigsawConfiguration>> addPieces(PieceGeneratorSupplier.Context<JigsawConfiguration> context, CastleFixedRotationPlacement.PieceFactory pieceFactory, BlockPos blockPos, boolean p_197214_, boolean p_197215_, Rotation rotation) {
        WorldgenRandom worldgenrandom = new WorldgenRandom(new LegacyRandomSource(0L));
        worldgenrandom.setLargeFeatureSeed(context.seed(), context.chunkPos().x, context.chunkPos().z);
        RegistryAccess registryaccess = context.registryAccess();
        JigsawConfiguration jigsawconfiguration = context.config();
        ChunkGenerator chunkgenerator = context.chunkGenerator();
        StructureManager structuremanager = context.structureManager();
        LevelHeightAccessor levelheightaccessor = context.heightAccessor();
        Predicate<Biome> predicate = context.validBiome();
        StructureFeature.bootstrap();
        Registry<StructureTemplatePool> registry = registryaccess.registryOrThrow(Registry.TEMPLATE_POOL_REGISTRY);
        StructureTemplatePool structuretemplatepool = jigsawconfiguration.startPool().get();
        StructurePoolElement structurepoolelement = structuretemplatepool.getRandomTemplate(worldgenrandom);
        if (structurepoolelement == EmptyPoolElement.INSTANCE) {
            return Optional.empty();
        } else {
            PoolElementStructurePiece poolelementstructurepiece = pieceFactory.create(structuremanager, structurepoolelement, blockPos, structurepoolelement.getGroundLevelDelta(), rotation, structurepoolelement.getBoundingBox(structuremanager, blockPos, rotation));
            BoundingBox boundingbox = poolelementstructurepiece.getBoundingBox();
            int i = (boundingbox.maxX() + boundingbox.minX()) / 2;
            int j = (boundingbox.maxZ() + boundingbox.minZ()) / 2;
            int k;
            if (p_197215_) {
                k = blockPos.getY() + chunkgenerator.getFirstFreeHeight(i, j, Heightmap.Types.WORLD_SURFACE_WG, levelheightaccessor);
            } else {
                k = blockPos.getY();
            }

            if (!predicate.test(chunkgenerator.getNoiseBiome(QuartPos.fromBlock(i), QuartPos.fromBlock(k), QuartPos.fromBlock(j)))) {
                return Optional.empty();
            } else {
                int l = boundingbox.minY() + poolelementstructurepiece.getGroundLevelDelta();
                poolelementstructurepiece.move(0, k - l, 0);
                return Optional.of((p_197208_, p_197209_) -> {
                    List<PoolElementStructurePiece> list = Lists.newArrayList();
                    list.add(poolelementstructurepiece);
                    if (jigsawconfiguration.maxDepth() > 0) {
                        int i1 = 80;
                        AABB aabb = new AABB((double)(i - 80), (double)(k - 80), (double)(j - 80), (double)(i + 80 + 1), (double)(k + 80 + 1), (double)(j + 80 + 1));
                        CastleFixedRotationPlacement.Placer CastleFixedRotationPlacement$placer = new CastleFixedRotationPlacement.Placer(registry, jigsawconfiguration.maxDepth(), pieceFactory, chunkgenerator, structuremanager, list, worldgenrandom);
                        CastleFixedRotationPlacement$placer.placing.addLast(new CastleFixedRotationPlacement.PieceState(poolelementstructurepiece, new MutableObject<>(Shapes.join(Shapes.create(aabb), Shapes.create(AABB.of(boundingbox)), BooleanOp.ONLY_FIRST)), 0));

                        while(!CastleFixedRotationPlacement$placer.placing.isEmpty()) {
                            CastleFixedRotationPlacement.PieceState CastleFixedRotationPlacement$piecestate = CastleFixedRotationPlacement$placer.placing.removeFirst();
                            CastleFixedRotationPlacement$placer.tryPlacingChildren(CastleFixedRotationPlacement$piecestate.piece, CastleFixedRotationPlacement$piecestate.free, CastleFixedRotationPlacement$piecestate.depth, p_197214_, levelheightaccessor);
                        }

                        list.forEach(p_197208_::addPiece);
                    }
                });
            }
        }
    }


    public interface PieceFactory {
        PoolElementStructurePiece create(StructureManager p_68965_, StructurePoolElement p_68966_, BlockPos p_68967_, int p_68968_, Rotation p_68969_, BoundingBox p_68970_);
    }

    static final class PieceState {
        final PoolElementStructurePiece piece;
        final MutableObject<VoxelShape> free;
        final int depth;

        PieceState(PoolElementStructurePiece p_191509_, MutableObject<VoxelShape> p_191510_, int p_191511_) {
            this.piece = p_191509_;
            this.free = p_191510_;
            this.depth = p_191511_;
        }
    }

    static final class Placer {
        private final Registry<StructureTemplatePool> pools;
        private final int maxDepth;
        private final CastleFixedRotationPlacement.PieceFactory factory;
        private final ChunkGenerator chunkGenerator;
        private final StructureManager structureManager;
        private final List<? super PoolElementStructurePiece> pieces;
        private final Random random;
        final Deque<CastleFixedRotationPlacement.PieceState> placing = Queues.newArrayDeque();

        Placer(Registry<StructureTemplatePool> p_69003_, int p_69004_, CastleFixedRotationPlacement.PieceFactory p_69005_, ChunkGenerator p_69006_, StructureManager p_69007_, List<? super PoolElementStructurePiece> p_69008_, Random p_69009_) {
            this.pools = p_69003_;
            this.maxDepth = p_69004_;
            this.factory = p_69005_;
            this.chunkGenerator = p_69006_;
            this.structureManager = p_69007_;
            this.pieces = p_69008_;
            this.random = p_69009_;
        }

        void tryPlacingChildren(PoolElementStructurePiece p_191513_, MutableObject<VoxelShape> p_191514_, int p_191515_, boolean p_191516_, LevelHeightAccessor p_191517_) {
            StructurePoolElement structurepoolelement = p_191513_.getElement();
            BlockPos blockpos = p_191513_.getPosition();
            Rotation rotation = p_191513_.getRotation();
            StructureTemplatePool.Projection structuretemplatepool$projection = structurepoolelement.getProjection();
            boolean flag = structuretemplatepool$projection == StructureTemplatePool.Projection.RIGID;
            MutableObject<VoxelShape> mutableobject = new MutableObject<>();
            BoundingBox boundingbox = p_191513_.getBoundingBox();
            int i = boundingbox.minY();

            label139:
            for(StructureTemplate.StructureBlockInfo structuretemplate$structureblockinfo : structurepoolelement.getShuffledJigsawBlocks(this.structureManager, blockpos, rotation, this.random)) {
                Direction direction = JigsawBlock.getFrontFacing(structuretemplate$structureblockinfo.state);
                BlockPos blockpos1 = structuretemplate$structureblockinfo.pos;
                BlockPos blockpos2 = blockpos1.relative(direction);
                int j = blockpos1.getY() - i;
                int k = -1;
                ResourceLocation resourcelocation = new ResourceLocation(structuretemplate$structureblockinfo.nbt.getString("pool"));
                Optional<StructureTemplatePool> optional = this.pools.getOptional(resourcelocation);
                if (optional.isPresent() && (optional.get().size() != 0 || Objects.equals(resourcelocation, Pools.EMPTY.location()))) {
                    ResourceLocation resourcelocation1 = optional.get().getFallback();
                    Optional<StructureTemplatePool> optional1 = this.pools.getOptional(resourcelocation1);
                    if (optional1.isPresent() && (optional1.get().size() != 0 || Objects.equals(resourcelocation1, Pools.EMPTY.location()))) {
                        boolean flag1 = boundingbox.isInside(blockpos2);
                        MutableObject<VoxelShape> mutableobject1;
                        if (flag1) {
                            mutableobject1 = mutableobject;
                            if (mutableobject.getValue() == null) {
                                mutableobject.setValue(Shapes.create(AABB.of(boundingbox)));
                            }
                        } else {
                            mutableobject1 = p_191514_;
                        }

                        List<StructurePoolElement> list = Lists.newArrayList();
                        if (p_191515_ != this.maxDepth) {
                            list.addAll(optional.get().getShuffledTemplates(this.random));
                        }

                        list.addAll(optional1.get().getShuffledTemplates(this.random));

                        for(StructurePoolElement structurepoolelement1 : list) {
                            if (structurepoolelement1 == EmptyPoolElement.INSTANCE) {
                                break;
                            }

                            for(Rotation rotation1 : Rotation.getShuffled(this.random)) {
                                List<StructureTemplate.StructureBlockInfo> list1 = structurepoolelement1.getShuffledJigsawBlocks(this.structureManager, BlockPos.ZERO, rotation1, this.random);
                                BoundingBox boundingbox1 = structurepoolelement1.getBoundingBox(this.structureManager, BlockPos.ZERO, rotation1);
                                int l;
                                if (p_191516_ && boundingbox1.getYSpan() <= 16) {
                                    l = list1.stream().mapToInt((p_69032_) -> {
                                        if (!boundingbox1.isInside(p_69032_.pos.relative(JigsawBlock.getFrontFacing(p_69032_.state)))) {
                                            return 0;
                                        } else {
                                            ResourceLocation resourcelocation2 = new ResourceLocation(p_69032_.nbt.getString("pool"));
                                            Optional<StructureTemplatePool> optional2 = this.pools.getOptional(resourcelocation2);
                                            Optional<StructureTemplatePool> optional3 = optional2.flatMap((p_161646_) -> {
                                                return this.pools.getOptional(p_161646_.getFallback());
                                            });
                                            int j3 = optional2.map((p_161644_) -> {
                                                return p_161644_.getMaxSize(this.structureManager);
                                            }).orElse(0);
                                            int k3 = optional3.map((p_161635_) -> {
                                                return p_161635_.getMaxSize(this.structureManager);
                                            }).orElse(0);
                                            return Math.max(j3, k3);
                                        }
                                    }).max().orElse(0);
                                } else {
                                    l = 0;
                                }

                                for(StructureTemplate.StructureBlockInfo structuretemplate$structureblockinfo1 : list1) {
                                    if (JigsawBlock.canAttach(structuretemplate$structureblockinfo, structuretemplate$structureblockinfo1)) {
                                        BlockPos blockpos3 = structuretemplate$structureblockinfo1.pos;
                                        BlockPos blockpos4 = blockpos2.subtract(blockpos3);
                                        BoundingBox boundingbox2 = structurepoolelement1.getBoundingBox(this.structureManager, blockpos4, rotation1);
                                        int i1 = boundingbox2.minY();
                                        StructureTemplatePool.Projection structuretemplatepool$projection1 = structurepoolelement1.getProjection();
                                        boolean flag2 = structuretemplatepool$projection1 == StructureTemplatePool.Projection.RIGID;
                                        int j1 = blockpos3.getY();
                                        int k1 = j - j1 + JigsawBlock.getFrontFacing(structuretemplate$structureblockinfo.state).getStepY();
                                        int l1;
                                        if (flag && flag2) {
                                            l1 = i + k1;
                                        } else {
                                            if (k == -1) {
                                                k = this.chunkGenerator.getFirstFreeHeight(blockpos1.getX(), blockpos1.getZ(), Heightmap.Types.WORLD_SURFACE_WG, p_191517_);
                                            }

                                            l1 = k - j1;
                                        }

                                        int i2 = l1 - i1;
                                        BoundingBox boundingbox3 = boundingbox2.moved(0, i2, 0);
                                        BlockPos blockpos5 = blockpos4.offset(0, i2, 0);
                                        if (l > 0) {
                                            int j2 = Math.max(l + 1, boundingbox3.maxY() - boundingbox3.minY());
                                            boundingbox3.encapsulate(new BlockPos(boundingbox3.minX(), boundingbox3.minY() + j2, boundingbox3.minZ()));
                                        }

                                        if (!Shapes.joinIsNotEmpty(mutableobject1.getValue(), Shapes.create(AABB.of(boundingbox3).deflate(0.25D)), BooleanOp.ONLY_SECOND)) {
                                            mutableobject1.setValue(Shapes.joinUnoptimized(mutableobject1.getValue(), Shapes.create(AABB.of(boundingbox3)), BooleanOp.ONLY_FIRST));
                                            int i3 = p_191513_.getGroundLevelDelta();
                                            int k2;
                                            if (flag2) {
                                                k2 = i3 - k1;
                                            } else {
                                                k2 = structurepoolelement1.getGroundLevelDelta();
                                            }

                                            PoolElementStructurePiece poolelementstructurepiece = this.factory.create(this.structureManager, structurepoolelement1, blockpos5, k2, rotation1, boundingbox3);
                                            int l2;
                                            if (flag) {
                                                l2 = i + j;
                                            } else if (flag2) {
                                                l2 = l1 + j1;
                                            } else {
                                                if (k == -1) {
                                                    k = this.chunkGenerator.getFirstFreeHeight(blockpos1.getX(), blockpos1.getZ(), Heightmap.Types.WORLD_SURFACE_WG, p_191517_);
                                                }

                                                l2 = k + k1 / 2;
                                            }

                                            p_191513_.addJunction(new JigsawJunction(blockpos2.getX(), l2 - j + i3, blockpos2.getZ(), k1, structuretemplatepool$projection1));
                                            poolelementstructurepiece.addJunction(new JigsawJunction(blockpos1.getX(), l2 - j1 + k2, blockpos1.getZ(), -k1, structuretemplatepool$projection));
                                            this.pieces.add(poolelementstructurepiece);
                                            if (p_191515_ + 1 <= this.maxDepth) {
                                                this.placing.addLast(new CastleFixedRotationPlacement.PieceState(poolelementstructurepiece, mutableobject1, p_191515_ + 1));
                                            }
                                            continue label139;
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        CastleFixedRotationPlacement.LOGGER.warn("Empty or non-existent fallback pool: {}", (Object)resourcelocation1);
                    }
                } else {
                    CastleFixedRotationPlacement.LOGGER.warn("Empty or non-existent pool: {}", (Object)resourcelocation);
                }
            }

        }
    }
}
