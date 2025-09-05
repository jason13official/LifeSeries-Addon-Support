package com.cursee.ls_addon_support.events;

import com.cursee.ls_addon_support.network.NetworkHandlerClient;
import com.cursee.ls_addon_support.utils.versions.VersionControl;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class ClientKeybinds {

  public static KeyBinding superpower;
  public static KeyBinding runCommand;

  public static void tick() {
    while (superpower != null && superpower.wasPressed()) {
      NetworkHandlerClient.pressSuperpowerKey();
    }
    while (runCommand != null && runCommand.wasPressed() && VersionControl.isDevVersion()) {
      NetworkHandlerClient.pressRunCommandKey();
    }
  }

  public static void registerKeybinds() {
    superpower = KeyBindingHelper.registerKeyBinding(new KeyBinding(
        "key.lifeseries.superpower",
        InputUtil.Type.KEYSYM,
        //? if <= 1.21.5 {
        GLFW.GLFW_KEY_G,
        //?} else {
        /*GLFW.GLFW_KEY_R,
         *///?}
        "key.categories.lifeseries"));
    if (VersionControl.isDevVersion()) {
      runCommand = KeyBindingHelper.registerKeyBinding(new KeyBinding(
          "key.lifeseries.runcommand",
          InputUtil.Type.KEYSYM,
          GLFW.GLFW_KEY_LEFT_ALT,
          "key.categories.lifeseries"));
    }
  }
}
