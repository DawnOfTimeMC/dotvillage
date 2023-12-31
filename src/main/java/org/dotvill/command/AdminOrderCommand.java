package org.dotvill.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import org.dotvill.construction.project.ConstructionProjectManager;
import org.dotvill.entity.AdminOrder;
import org.dotvill.entity.Dillager;

import java.util.List;

/**
 * Admin command used to order a villager to do something
 */
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
        ConstructionProjectManager manager = ConstructionProjectManager.get(stack.getLevel());
        return manager.getExistentProjectsNames();
    }

    private static int stopOrder(CommandSourceStack source, Entity entity) {
        if (entity instanceof Dillager villager) {
                villager.setAdminOrder(new AdminOrder.StopOrder());
                source.sendSuccess(() -> Component.literal("Successfully ordered villager to stop"), true);
        } else {
            source.sendFailure(Component.literal("Entity has to be a dawn of time villager"));
        }
        return 1;
    }

    private static int giveBuildStructureOrder(CommandSourceStack source, Entity builder, String name) {
        /*
        if (builder instanceof DotVillager villager) {
            ServerLevel level = source.getLevel();
            ConstructionProjectManager manager = ConstructionProjectManager.get(level);
            FreshBuildingProject site = manager.getProjectByName(name);
            if (site != null) {
                villager.setAdminOrder(new AdminOrder.BuildOrder(site));

                source.sendSuccess(Component.literal("Successfully given build structure order to villager"), true);
            } else {
                source.sendFailure(Component.literal("Building site not found"));
            }
        } else {
            source.sendFailure(Component.literal("Builder entity has to be a dawn of time villager"));
        }

         */
        return 1;
    }
}
