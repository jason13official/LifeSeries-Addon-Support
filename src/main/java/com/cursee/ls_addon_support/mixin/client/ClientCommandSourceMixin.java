package com.cursee.ls_addon_support.mixin.client;

import com.cursee.ls_addon_support.LSAddonSupportClient;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import java.util.Collection;
import net.minecraft.client.network.ClientCommandSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = ClientCommandSource.class, priority = 1)
public class ClientCommandSourceMixin {

  @ModifyReturnValue(method = "getPlayerNames", at = @At("RETURN"))
  private Collection<String> addHiddenPlayers(Collection<String> original) {
    if (LSAddonSupportClient.hiddenTabPlayers != null) {
      for (String hiddenName : LSAddonSupportClient.hiddenTabPlayers) {
          if (original.contains(hiddenName)) {
              continue;
          }
        original.add(hiddenName);
      }
    }
    return original;
  }
}
