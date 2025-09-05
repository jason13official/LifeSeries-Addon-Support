package com.cursee.ls_addon_support.mixin.client;

import com.cursee.ls_addon_support.LSAddonSupport;
import com.cursee.ls_addon_support.LSAddonSupportClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Entity.class, priority = 2)
public class EntityMixin {

  @Inject(method = "getAir", at = @At("RETURN"), cancellable = true)
  public void getAir(CallbackInfoReturnable<Integer> cir) {
      if (LSAddonSupport.isLogicalSide()) {
          return;
      }
      if (System.currentTimeMillis() - LSAddonSupportClient.snailAirTimestamp > 5000) {
          return;
      }
      if (LSAddonSupportClient.snailAir >= 300) {
          return;
      }

    Entity entity = (Entity) (Object) this;
    if (entity instanceof PlayerEntity player && !player.hasStatusEffect(
        StatusEffects.WATER_BREATHING)) {
      int initialAir = cir.getReturnValue();
      if (LSAddonSupportClient.snailAir < initialAir) {
        cir.setReturnValue(LSAddonSupportClient.snailAir);
      }
    }
  }
}
