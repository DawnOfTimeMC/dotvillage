package org.dawnoftimevillage.command;

import com.google.common.collect.Lists;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import org.dawnoftimevillage.world.buildingsite.BuildingSite;
import org.dawnoftimevillage.world.buildingsite.BuildingSitesManager;
import org.dawnoftimevillage.world.entity.DoTVillager;
import org.dawnoftimevillage.world.entity.workorder.BuildStructureWorkOrder;

import java.util.List;

public class WorkOrderCommand {
    // ORDER : /dot order give <villager> buildstructure <constructionSite>
    public static LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("order")
                .then(Commands.literal("give")
                        .then(Commands.argument("builder", EntityArgument.entity())
                                .then(Commands.literal("buildstructure")
                                        .then(Commands.argument("buildingsite", StringArgumentType.string())
                                        .suggests((context, builder) -> SharedSuggestionProvider.suggest(getBuildingSitesNames(context.getSource()), builder))
                                        .executes(context -> giveBuildStructureOrder(context.getSource(),
                                                EntityArgument.getEntity(context, "builder"),
                                                StringArgumentType.getString(context,"buildingsite")))))));
    }

    private static List<String> getBuildingSitesNames(CommandSourceStack stack) {
        BuildingSitesManager manager = BuildingSitesManager.get(stack.getLevel());
        List<String> names = Lists.newArrayList();
        for (BuildingSite site : manager.getSites()) {
            names.add(site.getName());
        }
        return names;
    }

    private static int giveBuildStructureOrder(CommandSourceStack source, Entity builder, String site) {
        if (builder instanceof DoTVillager villager) {
            BuildingSitesManager manager = BuildingSitesManager.get(source.getLevel());
            BuildingSite foundSite = null;
            for (BuildingSite buildingSite : manager.getSites()) {
                if (buildingSite.getName().equals(site)) {
                    foundSite = buildingSite;
                    break;
                }
            }
            if (foundSite == null) {
                source.sendFailure(Component.literal("Building site not found"));
            } else {
                villager.setWorkOrder(new BuildStructureWorkOrder(foundSite));
                source.sendSuccess(Component.literal("Successfully given build structure work order to villager"), true);
            }
        } else {
            source.sendFailure(Component.literal("Builder entity has to be a dawn of time villager"));
        }
        return 1;
    }
}
