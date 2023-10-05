package org.dawnoftimevillage.registry;

import com.mojang.serialization.Codec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.dawnoftimevillage.DawnOfTimeVillage;
import org.dawnoftimevillage.world.worldgen.VillagePieces;
import org.dawnoftimevillage.world.worldgen.VillageStructure;

public class DoTVStructuresRegistry {
    public static final DeferredRegister<StructureType<?>> STRUCTURE_TYPES = DeferredRegister.create(Registries.STRUCTURE_TYPE, DawnOfTimeVillage.MOD_ID);
    public static final DeferredRegister<StructurePieceType> STRUCTURE_PIECES = DeferredRegister.create(Registries.STRUCTURE_PIECE, DawnOfTimeVillage.MOD_ID);

    public static final RegistryObject<StructureType<VillageStructure>> VILLAGE_STRUCTURE = STRUCTURE_TYPES.register("dot_village", () -> get(VillageStructure.CODEC));
    public static final RegistryObject<StructurePieceType> VILLAGE_PIECE = STRUCTURE_PIECES.register("villagepiece", () -> (StructurePieceType.StructureTemplateType) VillagePieces.VillagePiece::new);

    private static <T extends Structure> StructureType<T> get(Codec<T> codec) {
        return () -> codec;
    }
}

