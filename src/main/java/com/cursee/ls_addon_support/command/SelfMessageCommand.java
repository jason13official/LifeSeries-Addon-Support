package com.cursee.ls_addon_support.command;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class SelfMessageCommand {

  public static void register(CommandDispatcher<ServerCommandSource> dispatcher,
      CommandRegistryAccess commandRegistryAccess,
      CommandManager.RegistrationEnvironment registrationEnvironment) {
    dispatcher.register(
        literal("selfmsg")
            .then(argument("message", StringArgumentType.greedyString())
                .executes(context -> execute(
                    context.getSource(),
                    StringArgumentType.getString(context, "message")
                ))
            )
    );

  }

  public static int execute(ServerCommandSource source, String string) {
    source.sendMessage(Text.of(string));
    return 1;
  }
}
