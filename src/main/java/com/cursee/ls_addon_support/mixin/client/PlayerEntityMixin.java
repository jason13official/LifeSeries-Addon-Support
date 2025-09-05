package com.cursee.ls_addon_support.mixin.client;

import com.cursee.ls_addon_support.utils.ClientUtils;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = PlayerEntity.class, priority = 2)
public class PlayerEntityMixin {
  @Inject(method = "canGlide", at = @At("HEAD"), cancellable = true)
  protected void canGlide(CallbackInfoReturnable<Boolean> cir) {
    if (ClientUtils.shouldPreventGliding()) {
      cir.setReturnValue(false);
    }
  }
}
