package org.dawnoftimevillage.registry;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import org.dawnoftimevillage.command.BuildingSiteCommand;
import org.dawnoftimevillage.command.WorkOrderCommand;

public class DoTVCommandsRegistry {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal("dawnoftime")
                .then(BuildingSiteCommand.register())
                .then(WorkOrderCommand.register());
        LiteralCommandNode<CommandSourceStack> node = dispatcher.register(builder);
        dispatcher.register(Commands.literal("dot").redirect(node));
    }
}
