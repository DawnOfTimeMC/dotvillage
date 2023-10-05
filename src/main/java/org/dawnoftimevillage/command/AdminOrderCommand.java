package org.dawnoftimevillage.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import org.dawnoftimevillage.world.buildingsite.BuildingSite;
import org.dawnoftimevillage.world.buildingsite.BuildingSitesManager;
import org.dawnoftimevillage.world.entity.AdminOrder;
import org.dawnoftimevillage.world.entity.DoTVillager;

import java.util.List;

public class AdminOrderCommand {
    // ORDER A VILLAGER TO DO SOMETHING : /dot order give <villager> <order> <parameters>
    //                          EXAMPLE : /dot order give @e[type=dawnoftimevillage:villager,limit=1] buildstructure mybuildingsite
    public static LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("order")
                .then(Commands.argument("villager", EntityArgument.entity())
                        .then(Commands.literal("stop").executes(context -> stopOrder(context.getSource(), EntityArgument.getEntity(context, "villager"))))
                        .then(Commands.literal("buildstructure")
                                .then(Commands.argument("buildingsite", StringArgumentType.string())
                                .suggests((context, builder) -> SharedSuggestionProvider.suggest(getExistentSitesNames(context.getSource()), builder))
                                .executes(context -> giveBuildStructureOrder(context.getSource(),
                                        EntityArgument.getEntity(context, "villager"),
                                        StringArgumentType.getString(context,"buildingsite"))))));
    }

    private static List<String> getExistentSitesNames(CommandSourceStack stack) {
        BuildingSitesManager manager = BuildingSitesManager.get(stack.getLevel());
        return manager.getExistentSitesNames();
    }

    private static int stopOrder(CommandSourceStack source, Entity entity) {
        if (entity instanceof DoTVillager villager) {
                villager.setAdminOrder(new AdminOrder.StopOrder());
                source.sendSuccess(Component.literal("Successfully ordered villager to stop"), true);
        } else {
            source.sendFailure(Component.literal("Entity has to be a dawn of time villager"));
        }
        return 1;
    }

    private static int giveBuildStructureOrder(CommandSourceStack source, Entity builder, String name) {
        if (builder instanceof DoTVillager villager) {
            ServerLevel level = source.getLevel();
            BuildingSitesManager manager = BuildingSitesManager.get(level);
            BuildingSite site = manager.getSiteByName(name);
            if (site != null) {
                villager.setAdminOrder(new AdminOrder.BuildOrder(site));

                source.sendSuccess(Component.literal("Successfully given build structure order to villager"), true);
            } else {
                source.sendFailure(Component.literal("Building site not found"));
            }
        } else {
            source.sendFailure(Component.literal("Builder entity has to be a dawn of time villager"));
        }
        return 1;
    }
}
