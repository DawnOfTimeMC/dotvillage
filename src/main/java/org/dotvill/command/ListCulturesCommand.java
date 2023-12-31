package org.dotvill.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import org.dotvill.village.Village;
import org.dotvill.village.VillageManager;

import java.util.List;

public class ListCulturesCommand {
    public static LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("listcultures").executes(context -> listCultures(context.getSource()));
    }

    private static int listCultures(CommandSourceStack source) {
        List<Village> villages = VillageManager.getVillageList(source.getLevel());
        if (villages != null) {
            if (villages.size() > 0) {
                source.sendSuccess(() -> Component.literal("Villages founded "), false);
                for (Village village : villages) {
                    String villageInfo = village.getName() + ", position : [" + village.getCenterPosition().getX() + " " + village.getCenterPosition().getY() + " " + village.getCenterPosition().getZ();
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
