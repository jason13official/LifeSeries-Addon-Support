package com.cursee.ls_addon_support.mixin;

import com.cursee.ls_addon_support.LSAddonSupport;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.wildcard.superpowers.Superpowers;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.wildcard.superpowers.SuperpowersWildcard;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(value = EnderPearlEntity.class, priority = 1)
public class EnderPearlEntityMixin {

  @ModifyArg(
      method = "onCollision",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;damage(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/damage/DamageSource;F)Z"),
      index = 2
  )
  private float onTargetDamaged(float amount) {
      if (!LSAddonSupport.isLogicalSide()) {
          return amount;
      }
    EnderPearlEntity pearl = (EnderPearlEntity) (Object) this;
      if (!(pearl.getOwner() instanceof ServerPlayerEntity owner)) {
          return amount;
      }
      if (!SuperpowersWildcard.hasActivePower(owner, Superpowers.TELEPORTATION)) {
          return amount;
      }
    return 0;
  }
}
