package com.cursee.ls_addon_support.mixin.client;

import com.cursee.ls_addon_support.LSAddonSupportClient;
import com.cursee.ls_addon_support.seasons.season.Seasons;
import com.cursee.ls_addon_support.utils.ClientUtils;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = InGameHud.class, priority = 1)
public class InGameHudMixin {
  @Unique
  private static final List<String> ls$allowedColors = List.of(
      "aqua","black","blue","dark_aqua","dark_blue","dark_gray","dark_green",
      "dark_purple","dark_red","gold","gray","green","light_purple","white","yellow", "red"
  );
  @Unique
  private static final List<String> ls$allowedHearts = List.of(
      "hud/heart/full", "hud/heart/full_blinking", "hud/heart/half", "hud/heart/half_blinking",
      "hud/heart/hardcore_full", "hud/heart/hardcore_full_blinking", "hud/heart/hardcore_half", "hud/heart/hardcore_half_blinking"
  );

  @Redirect(method = "drawHeart", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/util/Identifier;IIII)V"))
  private void customHearts(DrawContext instance, RenderPipeline renderPipeline, Identifier identifier, int x, int y, int u, int v) {

    String texturePath = identifier.getPath();
    Team playerTeam = ClientUtils.getPlayerTeam();
    if (!LSAddonSupportClient.COLORED_HEARTS || playerTeam == null || playerTeam.getColor() == null ||
        !ls$allowedColors.contains(playerTeam.getColor().getName().toLowerCase()) ||
        !ls$allowedHearts.contains(texturePath)) {
      if (LSAddonSupportClient.clientCurrentSeason == Seasons.SECRET_LIFE && texturePath.startsWith("hud/heart/container")) {
        return;
      }
      instance.drawGuiTexture(renderPipeline, identifier, x, y, u, v);
      ls$afterHeartDraw(instance, renderPipeline, identifier, x, y, u, v);
      return;
    }

    String color = playerTeam.getColor().getName().toLowerCase();

    String heartType = texturePath.replaceFirst("hud/heart/", "");

    if (!heartType.startsWith("hardcore_")) {
      if (LSAddonSupportClient.COLORED_HEARTS_HARDCORE_ALL_LIVES || (playerTeam.getName().equals("lives_1") & LSAddonSupportClient.COLORED_HEARTS_HARDCORE_LAST_LIFE)) {
        heartType = "hardcore_"+heartType;
      }
    }
    Identifier customHeart = Identifier.of("lifeseries", "textures/gui/hearts/"+color+"_"+heartType+".png");
    instance.drawTexture(renderPipeline, customHeart, x, y, u, v, u, v, u, v);
    ls$afterHeartDraw(instance, renderPipeline, identifier, x, y, u, v);
  }

  @Unique
  private void ls$afterHeartDraw(DrawContext instance, RenderPipeline renderPipeline, Identifier identifier, int x, int y, int u, int v) {
    if (LSAddonSupportClient.clientCurrentSeason != Seasons.SECRET_LIFE) {
      return;
    }
    String name = identifier.getPath();
    boolean blinking = name.contains("blinking");
    boolean half = name.contains("half");
    String heartName = "container";
    if (blinking) heartName += "_blinking";
    if (half) heartName += "_half";

    Identifier customHeart = Identifier.of("lifeseries", "textures/gui/hearts/secretlife/"+heartName+".png");
    instance.drawTexture(renderPipeline, customHeart, x, y, u, v, u, v, u, v);
  }
}
