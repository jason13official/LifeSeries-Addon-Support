package com.cursee.ls_addon_support.mixin.client;

import com.cursee.ls_addon_support.LSAddonSupportClient;
import com.cursee.ls_addon_support.utils.other.TextUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(value = EntityRenderer.class, priority = 1)
public class EntityRendererMixin<T extends Entity> {

  @ModifyArg(
      method = "render",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/EntityRenderer;renderLabelIfPresent(Lnet/minecraft/client/render/entity/state/EntityRenderState;Lnet/minecraft/text/Text;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"),
      index = 1
  )
  public Text render(Text text) {
      if (text == null) {
          return text;
      }
      if (MinecraftClient.getInstance().getNetworkHandler() == null) {
          return text;
      }

    if (LSAddonSupportClient.playerDisguiseNames.containsKey(text.getString())) {
      String name = LSAddonSupportClient.playerDisguiseNames.get(text.getString());
      for (PlayerListEntry entry : MinecraftClient.getInstance().getNetworkHandler()
          .getPlayerList()) {
        if (entry.getProfile().getName().equalsIgnoreCase(TextUtils.removeFormattingCodes(name))) {
          if (entry.getDisplayName() != null) {
            return ls$applyColorblind(entry.getDisplayName(), entry.getScoreboardTeam());
          }
          return ls$applyColorblind(Text.literal(name), entry.getScoreboardTeam());
        }
      }
    } else {
      for (PlayerListEntry entry : MinecraftClient.getInstance().getNetworkHandler()
          .getPlayerList()) {
        if (entry.getProfile().getName()
            .equalsIgnoreCase(TextUtils.removeFormattingCodes(text.getString()))) {
          return ls$applyColorblind(text, entry.getScoreboardTeam());
        }
      }
    }
    return text;
  }

  @Unique
  public Text ls$applyColorblind(Text original, Team team) {
      if (!LSAddonSupportClient.COLORBLIND_SUPPORT) {
          return original;
      }
      if (original == null) {
          return original;
      }
      if (team == null) {
          return original;
      }
    return TextUtils.format("[{}] ", team.getDisplayName().getString()).formatted(team.getColor())
        .append(original);
  }
}
