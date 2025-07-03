package net.mat0u5.lifeseries.events;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.mat0u5.lifeseries.network.NetworkHandlerClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class ClientKeybinds {
    public static KeyBinding superpower;
    public static void tick() {
        while (superpower.wasPressed()) {
            NetworkHandlerClient.pressSuperpowerKey();
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
    }
}
