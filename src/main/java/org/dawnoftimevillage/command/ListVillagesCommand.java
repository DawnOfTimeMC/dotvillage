package org.dawnoftimevillage.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import org.dawnoftimevillage.construction.project.ConstructionProjectManager;
import org.dawnoftimevillage.construction.project.FreshBuildingProject;
import org.dawnoftimevillage.village.Village;
import org.dawnoftimevillage.village.VillageManager;

import java.util.List;

public class ListVillagesCommand {
    // LIST ALL VILLAGES IN THIS LEVEL : /dot listvillages
    public static LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("listvillages").executes(context -> listVillages(context.getSource()));
    }

    private static int listVillages(CommandSourceStack source) {
        List<Village> villages = VillageManager.getVillageList(source.getLevel());
        if (villages != null) {
            if (villages.size() > 0) {
                source.sendSuccess(() -> Component.literal("Villages founded "), false);
                for (Village village : villages) {
                    String villageInfo = village.getName() + ", position : [" + village.getPosition().getX() + " " + village.getPosition().getY() + " " + village.getPosition().getZ();
                    source.sendSuccess(() -> Component.literal(villageInfo), false);
                }
            } else {
                source.sendSuccess(() -> Component.literal("No villages founded"), false);
            }
        } else {
            source.sendSuccess(() -> Component.literal("No villages founded (null)"), false);
        }
        return 1;
    }
}
