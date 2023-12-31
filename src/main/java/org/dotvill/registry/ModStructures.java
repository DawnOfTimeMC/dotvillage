package org.dotvill.registry;

import com.mojang.serialization.Codec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.dotvill.DotVill;
import org.dotvill.world.VillagePieces;
import org.dotvill.world.VillageStructure;

public class ModStructures {
    public static final DeferredRegister<StructureType<?>> STRUCTURE_TYPES = DeferredRegister.create(Registries.STRUCTURE_TYPE, DotVill.MOD_ID);
    public static final DeferredRegister<StructurePieceType> STRUCTURE_PIECES = DeferredRegister.create(Registries.STRUCTURE_PIECE, DotVill.MOD_ID);

    public static final RegistryObject<StructureType<VillageStructure>> VILLAGE_STRUCTURE = STRUCTURE_TYPES.register("village_generator", () -> get(VillageStructure.CODEC));
    public static final RegistryObject<StructurePieceType> VILLAGE_PIECE = STRUCTURE_PIECES.register("villagepiece", () -> (StructurePieceType.StructureTemplateType) VillagePieces.VillagePiece::new);

    private static <T extends Structure> StructureType<T> get(Codec<T> codec) {
        return () -> codec;
    }
}

