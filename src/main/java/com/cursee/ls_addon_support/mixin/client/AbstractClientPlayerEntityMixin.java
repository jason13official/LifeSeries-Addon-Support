package com.cursee.ls_addon_support.mixin.client;

import com.cursee.ls_addon_support.LSAddonSupportClient;
import java.util.UUID;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.SkinTextures;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = AbstractClientPlayerEntity.class, priority = 1)
public class AbstractClientPlayerEntityMixin {

  @Inject(method = "getSkinTextures", at = @At("HEAD"), cancellable = true)
  public void getSkinTextures(CallbackInfoReturnable<SkinTextures> cir) {
    AbstractClientPlayerEntity abstrPlayer = (AbstractClientPlayerEntity) (Object) this;
    UUID uuid = abstrPlayer.getUuid();
      if (uuid == null) {
          return;
      }
      if (!LSAddonSupportClient.playerDisguiseUUIDs.containsKey(uuid)) {
          return;
      }

    UUID disguisedUUID = LSAddonSupportClient.playerDisguiseUUIDs.get(uuid);
    if (MinecraftClient.getInstance().getNetworkHandler() == null) {
      return;
    }
    for (PlayerListEntry entry : MinecraftClient.getInstance().getNetworkHandler()
        .getPlayerList()) {
      if (entry.getProfile().getId().equals(disguisedUUID)) {
        cir.setReturnValue(entry.getSkinTextures());
        return;
      }
    }
  }
}
