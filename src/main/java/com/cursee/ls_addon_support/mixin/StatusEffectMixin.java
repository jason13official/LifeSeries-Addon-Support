package com.cursee.ls_addon_support.mixin;

import static com.cursee.ls_addon_support.LSAddonSupport.blacklist;

import com.cursee.ls_addon_support.LSAddonSupport;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.server.world.ServerWorld;

@Mixin(value = StatusEffect.class, priority = 1)
public class StatusEffectMixin {

  @Inject(method = "applyInstantEffect", at = @At("HEAD"), cancellable = true)
  public void applyInstantEffect(ServerWorld world, Entity effectEntity, Entity attacker, LivingEntity target, int amplifier, double proximity, CallbackInfo ci) {
      if (!LSAddonSupport.isLogicalSide()) {
          return;
      }
    StatusEffect effect = (StatusEffect) (Object) this;
    if (target instanceof ServerPlayerEntity) {
      if (blacklist.getBannedEffects().contains(Registries.STATUS_EFFECT.getEntry(effect))) {
        ci.cancel();
      }
    }
  }

  @Inject(method = "applyUpdateEffect", at = @At("HEAD"), cancellable = true)
  public void applyInstantEffect(ServerWorld world, LivingEntity entity, int amplifier, CallbackInfoReturnable<Boolean> cir) {
      if (!LSAddonSupport.isLogicalSide()) {
          return;
      }
    StatusEffect effect = (StatusEffect) (Object) this;
    if (entity instanceof ServerPlayerEntity) {
      if (blacklist.getBannedEffects().contains(Registries.STATUS_EFFECT.getEntry(effect))) {
        cir.setReturnValue(false);
      }
    }
  }
}
