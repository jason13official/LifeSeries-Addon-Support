package com.cursee.ls_addon_support.resources.datapack;

import static com.cursee.ls_addon_support.LSAddonSupport.server;

import com.cursee.ls_addon_support.utils.other.OtherUtils;
import com.cursee.ls_addon_support.utils.other.TaskScheduler;
import net.minecraft.server.MinecraftServer;

public class DatapackManager {

  public static void onServerStarted(MinecraftServer server) {
    DynamicDatapackManager.onServerStarted(server);
    TaskScheduler.scheduleTask(50, OtherUtils::reloadServerNoUpdate);
  }

  public static void onReloadStart() {
    DynamicDatapackManager.copyLootTables(server);
  }

  public static void onReloadEnd() {
    DynamicDatapackManager.enableDatapack();
  }

  public static String getMinecraftVersion() {
    //? if = 1.21
    return "1.21-1.21.1";
    //? if = 1.21.2
    /*return "1.21.2-1.21.3";*/
    //? if = 1.21.4
    /*return "1.21.4";*/
    //? if = 1.21.5
    /*return "1.21.5";*/
    //? if = 1.21.6
    /*return "1.21.6";*/
  }

  public static String getResourceTriviaPackVersion() {
    //? if <= 1.21.4 {
    return "";
    //?} else {
    /*return "_1.21.5";
     *///?}
  }

  public static String getResourceTaskPackVersion() {
    //? if <= 1.21.4 {
    return "";
    //?} else {
    /*return "_1.21.5";
     *///?}
  }
}
