package com.cursee.ls_addon_support.registries;

import com.cursee.ls_addon_support.command.ClientCommands;
import com.cursee.ls_addon_support.events.ClientEvents;
import com.cursee.ls_addon_support.events.ClientKeybinds;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;

public class ClientRegistries {

  public static void registerModStuff() {
    registerCommands();
    ClientEvents.registerClientEvents();
    ClientKeybinds.registerKeybinds();
  }

  private static void registerCommands() {
    ClientCommandRegistrationCallback.EVENT.register(ClientCommands::register);
  }
}
