package com.cursee.ls_addon_support.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.client.gl.RenderPipelines;

public class VignetteRenderer {

  private static final Identifier VIGNETTE_TEXTURE = Identifier.ofVanilla(
      "textures/misc/vignette.png");
  private static float vignetteDarkness = 0.0F;
  private static long vignetteEnd = 0;

  public static void renderVignette(DrawContext context) {
      if (System.currentTimeMillis() >= vignetteEnd && vignetteEnd != -1) {
          return;
      }

    MinecraftClient client = MinecraftClient.getInstance();
      if (client.player == null) {
          return;
      }

    float darkness = MathHelper.clamp(vignetteDarkness, 0.0F, 1.0F);
      if (darkness == 0) {
          return;
      }

    int color = ColorHelper.fromFloats(1.0F, darkness, darkness, darkness);
    context.drawTexture(RenderPipelines.VIGNETTE, VIGNETTE_TEXTURE, 0, 0, 0.0F, 0.0F,
        context.getScaledWindowWidth(), context.getScaledWindowHeight(), context.getScaledWindowWidth(), context.getScaledWindowHeight(), color);
  }

  // Call this method to show the vignette for a certain duration
  public static void showVignetteFor(float darkness, long durationMillis) {
    vignetteDarkness = MathHelper.clamp(darkness, 0.0F, 1.0F);
    if (durationMillis == -1) {
      vignetteEnd = -1;
    } else {
      vignetteEnd = System.currentTimeMillis() + durationMillis;
    }
  }
}
