package org.dawnoftimevillage.command;

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

import java.util.List;

public class BuildingSiteCommand {
    /** Dev purpose variables **/
    // TODO : remove before publishing mod
    public static boolean showDebugInfo;
    public static boolean superBuilder;
    // CREATE A SITE  : /dot buildingsite create <name> <position> <structure path> <mirror> <rotation>
    //        EXAMPLE : /dot buildingsite create mysite ~ ~ ~ "example/myhouse"
    // LIST ALL SITES : /dot buildingsite list
    public static LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("buildingsite")
        .then(Commands.literal("list").executes(context -> listAllSites(context.getSource())))
        .then(Commands.literal("toggledebug").executes(context -> toggleDebug()))
        .then(Commands.literal("togglesuperbuilder").executes(context -> toggleSuperBuilder()))
        .then(Commands.literal("remove")
                .then(Commands.argument("name", StringArgumentType.string())
                        .suggests((context, builder) -> SharedSuggestionProvider.suggest(getExistentSitesNames(context.getSource()), builder))
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
        boolean added = manager.addSite(source.getLevel(), name, DoTVUtils.resource(structure), new BuildingSiteSettings(buildPos));
        if (added) {
            source.sendSuccess(Component.literal("Successfully added building site"), true);
        } else {
            source.sendFailure(Component.literal("Failed to add building site"));
        }
        return 1;
    }

    private static int removeSite(CommandSourceStack source, String name) {
        BuildingSitesManager manager = BuildingSitesManager.get(source.getLevel());
        BuildingSite site = manager.getSiteByName(name);
        if (site != null) {
            manager.removeSite(site);
            source.sendSuccess(Component.literal("Successfully removed building site"), true);

        } else {
            source.sendFailure(Component.literal("Failed to remove building site"));
        }
        return 1;
    }

    private static List<String> getExistentSitesNames(CommandSourceStack stack) {
        BuildingSitesManager manager = BuildingSitesManager.get(stack.getLevel());
        return manager.getExistentSitesNames();
    }

    private static int getBuildingSiteInfo(CommandSourceStack source) {
        return 1;
    }

    private static int listAllSites(CommandSourceStack source) {
        List<BuildingSite> sites = BuildingSitesManager.get(source.getLevel()).getSites();
        if (sites.isEmpty()) {
            source.sendSuccess(Component.literal("No building sites found"), false);
        } else {
            source.sendSuccess(Component.literal("Building sites found :"), false);
            for (BuildingSite site : sites) {
                String siteInfo = site.getName() + ", progression : " + site.getNextBlockToBuildIndex() + ", position : [" + site.getLocation().getX() + " " + site.getLocation().getY() + " " + site.getLocation().getZ() + "], structure : " + site.getBuildingPlan().getStructure().getPath();
                source.sendSuccess(Component.literal(siteInfo), false);
            }
        }
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
}