package org.dawnoftimevillage.command;

import com.google.common.collect.Lists;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import org.dawnoftimevillage.util.DoTVUtils;
import org.dawnoftimevillage.world.buildingsite.BuildingSite;
import org.dawnoftimevillage.world.buildingsite.BuildingSiteSettings;
import org.dawnoftimevillage.world.buildingsite.BuildingSitesManager;
import org.dawnoftimevillage.world.entity.workorder.BuildStructureWorkOrder;

import java.util.List;

public class BuildingSiteCommand {
    public static boolean showDebugInfo;
    public static boolean superBuilder;
    // CREATION : /dot buildingsite create mysite1 ~ ~ ~ "tests/maison_1" <mirror> <rotation>
    // LIST :     /dot buildingsite list
    public static LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("buildingsite")
        .then(Commands.literal("list").executes(context -> listAll(context.getSource())))
        .then(Commands.literal("toggledebug").executes(context -> toggleDebug()))
        .then(Commands.literal("togglesuperbuilder").executes(context -> toggleSuperBuilder()))
        .then(Commands.literal("remove")
                .then(Commands.argument("name", StringArgumentType.string())
                        .suggests((context, builder) -> SharedSuggestionProvider.suggest(getBuildingSitesNames(context.getSource()), builder))
                        .executes(context -> removeSite(context.getSource(), StringArgumentType.getString(context, "name")))))
        .then(Commands.literal("create")
                .then(Commands.argument("name", StringArgumentType.string())
                        .then(Commands.argument("position", BlockPosArgument.blockPos())
                                .then(Commands.argument("structure", StringArgumentType.string())
                                        .executes(context -> createSite(context.getSource(),
                                                StringArgumentType.getString(context, "name"),
                                                BlockPosArgument.getBlockPos(context, "position"),
                                                StringArgumentType.getString(context, "structure")))))));
    }

    private static int createSite(CommandSourceStack source, String name, BlockPos buildPos, String structure) {
        BuildingSitesManager manager = BuildingSitesManager.get(source.getLevel());
        manager.addSite(source.getLevel(), name, DoTVUtils.resource(structure), new BuildingSiteSettings(buildPos));
        source.sendSuccess(Component.literal("Sucessfully added building site"), false);
        return 1;
    }

    private static int removeSite(CommandSourceStack source, String name) {
        BuildingSitesManager manager = BuildingSitesManager.get(source.getLevel());
        BuildingSite foundSite = null;
        for (BuildingSite site : manager.getSites()) {
            if (site.getName().equals(name)) {
                foundSite = site;
                break;
            }
        }
        if (foundSite == null) {
            source.sendFailure(Component.literal("Building site not found"));
        } else {
            manager.removeSite(foundSite);
            source.sendSuccess(Component.literal("Successfully removed building site"), true);
        }
        return 1;
    }

    private static List<String> getBuildingSitesNames(CommandSourceStack stack) {
        BuildingSitesManager manager = BuildingSitesManager.get(stack.getLevel());
        List<String> names = Lists.newArrayList();
        for (BuildingSite site : manager.getSites()) {
            names.add(site.getName());
        }
        return names;
    }

    private static int info(CommandSourceStack source) {
        return 1;
    }

    private static int toggleDebug() {
        BuildingSiteCommand.showDebugInfo = !BuildingSiteCommand.showDebugInfo;
        return 1;
    }

    private static int toggleSuperBuilder() {
        BuildingSiteCommand.superBuilder = !BuildingSiteCommand.superBuilder;
        return 1;
    }

    private static int listAll(CommandSourceStack source) {
        List<BuildingSite> sites = BuildingSitesManager.get(source.getLevel()).getSites();
        if (sites.isEmpty()) {
            source.sendSuccess(Component.literal("No building sites found"), false);
        } else {
            source.sendSuccess(Component.literal("Building sites found :"), false);
            for (BuildingSite site : sites) {
                String siteInfo =
                        site.getName() + ", progression : " +
                        site.getProgression() + ", position : [" +
                        site.getLocation().getX() + " " +
                        site.getLocation().getY() + " " +
                        site.getLocation().getZ() + "], structure : " +
                        site.getBuildingPlan().getStructure().getPath();
                source.sendSuccess(Component.literal(siteInfo), false);
            }
        }
        return 1;
    }
}
