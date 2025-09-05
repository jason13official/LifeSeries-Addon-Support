package com.cursee.ls_addon_support.registries;

import com.cursee.ls_addon_support.command.ClaimKillCommand;
import com.cursee.ls_addon_support.command.GivelifeCommand;
import com.cursee.ls_addon_support.command.LifeSeriesCommand;
import com.cursee.ls_addon_support.command.LivesCommand;
import com.cursee.ls_addon_support.command.SelfMessageCommand;
import com.cursee.ls_addon_support.command.SessionCommand;
import com.cursee.ls_addon_support.command.WatcherCommand;
import com.cursee.ls_addon_support.dependencies.DependencyManager;
import com.cursee.ls_addon_support.events.Events;
import com.cursee.ls_addon_support.seasons.boogeyman.BoogeymanCommand;
import com.cursee.ls_addon_support.seasons.season.doublelife.DoubleLifeCommands;
import com.cursee.ls_addon_support.seasons.season.secretlife.SecretLifeCommands;
import com.cursee.ls_addon_support.seasons.season.wildlife.WildLifeCommands;
import com.cursee.ls_addon_support.seasons.secretsociety.SocietyCommands;
import com.cursee.ls_addon_support.utils.other.TaskScheduler;
import com.cursee.ls_addon_support.utils.other.TextUtils;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class ModRegistries {

  public static void registerModStuff() {
    registerCommands();
    registerEvents();
    TextUtils.setEmotes();

    if (DependencyManager.polymerLoaded()) {
      MobRegistry.registerMobs();
    }
  }

  private static void registerCommands() {
    CommandRegistrationCallback.EVENT.register(DoubleLifeCommands::register);
    CommandRegistrationCallback.EVENT.register(SecretLifeCommands::register);
    CommandRegistrationCallback.EVENT.register(WildLifeCommands::register);

    CommandRegistrationCallback.EVENT.register(LivesCommand::register);
    CommandRegistrationCallback.EVENT.register(SessionCommand::register);
    CommandRegistrationCallback.EVENT.register(BoogeymanCommand::register);
    CommandRegistrationCallback.EVENT.register(ClaimKillCommand::register);
    CommandRegistrationCallback.EVENT.register(LifeSeriesCommand::register);
    CommandRegistrationCallback.EVENT.register(GivelifeCommand::register);
    CommandRegistrationCallback.EVENT.register(SelfMessageCommand::register);
    CommandRegistrationCallback.EVENT.register(WatcherCommand::register);
    CommandRegistrationCallback.EVENT.register(SocietyCommands::register);
  }

  private static void registerEvents() {
    Events.register();
    TaskScheduler.registerTickHandler();
  }
}
