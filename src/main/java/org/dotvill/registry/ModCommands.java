package org.dotvill.registry;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import org.dotvill.command.AdminOrderCommand;
import org.dotvill.command.BuildingProjectCommand;
import org.dotvill.command.ListCulturesCommand;
import org.dotvill.command.ListVillagesCommand;

public class ModCommands {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal("dawnoftime")
                .then(ListCulturesCommand.register())
                .then(ListVillagesCommand.register())
                .then(BuildingProjectCommand.register())
                .then(AdminOrderCommand.register());
        LiteralCommandNode<CommandSourceStack> node = dispatcher.register(builder);
        dispatcher.register(Commands.literal("dot").redirect(node));
    }
}
