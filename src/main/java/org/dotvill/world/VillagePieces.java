package org.dotvill.world;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePieceAccessor;
import net.minecraft.world.level.levelgen.structure.TemplateStructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.dotvill.culture.BuildingType;
import org.dotvill.culture.HardCodedBuildingTypes;
import org.dotvill.registry.ModStructures;
import org.dotvill.util.ModUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class VillagePieces {
    private static final int STARTER_PACK_SIZE = 4;
    private static final int BUILDING_SEPARATION = 4;
    private static final int BUILDING_MAX_DISTANCE_FROM_VILLAGE_CENTER = 25;
    private static final ResourceLocation[] GERMAN_STARTER_PACK = new ResourceLocation[]{
            germanBuilding("defense_tower"),
            germanBuilding("lumberjack"),
            germanBuilding("sheep_farm")};
    private static final ResourceLocation[] JAPANESE_STARTER_PACK = new ResourceLocation[]{
            japaneseBuilding("japanesehouse1"),
            japaneseBuilding("japanesehouse2"),
            japaneseBuilding("japanesetemple1"),
            japaneseBuilding("japanesetemple2")};

    public static void addPieces(StructureTemplateManager manager, BlockPos villagePos, StructurePieceAccessor pieces, RandomSource random, VillageStructure.Culture culture) {
        List<ResourceLocation> availableBuildings = new ArrayList<>(getStarterPack(culture));
        if (availableBuildings.isEmpty()) {
            throw new RuntimeException("No buildings in village starterpack");
        }
        List<BoundingBox> buildingsBoundingBoxes = new ArrayList<>();

        int maxBuildings =  Math.min(STARTER_PACK_SIZE, availableBuildings.size());
        for (int i = 0; i < maxBuildings; ++i) {
            ResourceLocation building = availableBuildings.remove(Mth.nextInt(random, 0, availableBuildings.size() - 1));
            Optional<StructureTemplate> buildingTemplate = manager.get(building);
            if (!buildingTemplate.isPresent()) {
                throw new RuntimeException("Failed to load village building during world generation : " + building);
            }
            BoundingBox buildingBoundingBox = null;
            Rotation rotation = Rotation.getRandom(random);

            BlockPos randomPos = BlockPos.ZERO;
            boolean collision = false;
            for (int j = 0; j < 1000; ++j) {
                collision = false;
                int lowerBoundX = villagePos.getX() - BUILDING_MAX_DISTANCE_FROM_VILLAGE_CENTER;
                int upperBoundX = villagePos.getX() + BUILDING_MAX_DISTANCE_FROM_VILLAGE_CENTER;
                int lowerBoundZ = villagePos.getZ() - BUILDING_MAX_DISTANCE_FROM_VILLAGE_CENTER;
                int upperBoundZ = villagePos.getZ() + BUILDING_MAX_DISTANCE_FROM_VILLAGE_CENTER;

                int offsetX = Mth.nextInt(random, lowerBoundX, upperBoundX);
                int offsetZ = Mth.nextInt(random, lowerBoundZ, upperBoundZ);
                randomPos = villagePos.offset(offsetX - villagePos.getX(), 0, offsetZ - villagePos.getZ());

                buildingBoundingBox = buildingTemplate.get().getBoundingBox(randomPos, rotation, BlockPos.ZERO, Mirror.NONE).inflatedBy(BUILDING_SEPARATION);
                if (!buildingsBoundingBoxes.isEmpty()) {
                    for (BoundingBox box : buildingsBoundingBoxes) {
                        if (buildingBoundingBox.intersects(box)) {
                            collision = true;
                        }
                    }
                }
                if (!collision) {
                    break;
                }
            }
            if (!collision) {
                BuildingType buildingType;
                if (building.getPath().equals("village/german/starterpack/lumberjack")) {
                    buildingType = HardCodedBuildingTypes.LUMBERJACK;
                } else if (building.getPath().equals("village/german/starterpack/sheep_farm.nbt")) {
                    buildingType = HardCodedBuildingTypes.SHEEP_FARM;
                } else {
                    buildingType = HardCodedBuildingTypes.STANDARD_TYPE;
                }

                pieces.addPiece(new VillagePieces.VillagePiece(manager, building, randomPos, rotation, buildingType));
                buildingsBoundingBoxes.add(buildingBoundingBox);
            }
        }
    }

    private static List<ResourceLocation> getStarterPack(VillageStructure.Culture culture) {
        return switch (culture) {
            case GERMAN -> Arrays.asList(GERMAN_STARTER_PACK);
            case JAPANESE -> Arrays.asList(JAPANESE_STARTER_PACK);
        };
    }

    private static ResourceLocation germanBuilding(String name) {
        return ModUtils.resource("village/german/starterpack/" + name);
    }

    private static ResourceLocation japaneseBuilding(String name) {
        return ModUtils.resource("village/japanese/starterpack/" + name);
    }

    public static class VillagePiece extends TemplateStructurePiece {
        private BuildingType buildingType;

        public VillagePiece(StructureTemplateManager manager, ResourceLocation resourceLocation, BlockPos pos, Rotation rotation, BuildingType buildingType) {
            super(ModStructures.VILLAGE_PIECE.get(), 0, manager, resourceLocation, resourceLocation.toString(), makeSettings(rotation), pos);
            this.buildingType = buildingType;
        }

        public VillagePiece(StructureTemplateManager manager, CompoundTag tag) {
            super(ModStructures.VILLAGE_PIECE.get(), tag, manager, (p) -> makeSettings(Rotation.valueOf(tag.getString("Rot"))));
        }

        private static StructurePlaceSettings makeSettings(Rotation rotation) {
            return (new StructurePlaceSettings()).setRotation(rotation).setMirror(Mirror.NONE);//.addProcessor(BlockIgnoreProcessor.STRUCTURE_AND_AIR);
        }

        protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tag) {
            super.addAdditionalSaveData(context, tag);
            tag.putString("Rot", this.placeSettings.getRotation().name());
        }

        protected void handleDataMarker(String pName, BlockPos pPos, ServerLevelAccessor pLevel, RandomSource pRandom, BoundingBox pBox) {
        }

        public BuildingType getBuildingType() {
            return this.buildingType;
        }

        public void postProcess(WorldGenLevel pLevel, StructureManager pStructureManager, ChunkGenerator pGenerator, RandomSource pRandom, BoundingBox pBox, ChunkPos pChunkPos, BlockPos pPos) {
            /*
            int i = pLevel.getHeight(Heightmap.Types.WORLD_SURFACE_WG, this.templatePosition.getX(), this.templatePosition.getZ());
            this.templatePosition = new BlockPos(this.templatePosition.getX(), i, this.templatePosition.getZ());
            */
            super.postProcess(pLevel, pStructureManager, pGenerator, pRandom, pBox, pChunkPos, pPos);
        }
    }
}
