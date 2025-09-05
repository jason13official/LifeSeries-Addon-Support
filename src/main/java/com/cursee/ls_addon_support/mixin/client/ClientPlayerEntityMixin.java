package com.cursee.ls_addon_support.mixin.client;

import com.cursee.ls_addon_support.events.ClientEvents;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ClientPlayerEntity.class, priority = 1)
public class ClientPlayerEntityMixin {

  @Inject(method = "tick", at = @At("TAIL"))
  private void tickTail(CallbackInfo ci) {
    ClientEvents.onClientTickEnd();
  }
}
