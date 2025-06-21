package net.mat0u5.lifeseries.client.render;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

public class ClientRenderUtils {
    public static boolean isGameFullyFrozen = false;
    public static void onInitialize() {
        HudRenderCallback.EVENT.register(ClientRenderUtils::renderText);
    }

    private static void renderText(DrawContext context, RenderTickCounter renderTickCounter) {
        TextInfo.renderText(context);
        VignetteRenderer.renderVignette(context);
    }
}
