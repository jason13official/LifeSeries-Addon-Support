package com.cursee.ls_addon_support.mixin.client;

import com.cursee.ls_addon_support.LSAddonSupportClient;
import com.cursee.ls_addon_support.events.ClientEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LivingEntity.class, priority = 2)
public class LivingEntityMixin {

  @Inject(method = "jump", at = @At("TAIL"))
  private void onJump(CallbackInfo ci) {
    LivingEntity entity = (LivingEntity) (Object) this;
    ClientEvents.onClientJump(entity);
  }

  @ModifyArg(
      //? if <= 1.21 {
      method = "travel",
      //?} else {
      /*method = "travelMidAir",
      *///?}
      at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;applyMovementInput(Lnet/minecraft/util/math/Vec3d;F)Lnet/minecraft/util/math/Vec3d;"),
      index = 1
  )
  private float applyMovementInput(float slipperiness) {
      if ((System.currentTimeMillis() - LSAddonSupportClient.CURSE_SLIDING) > 5000) {
          return slipperiness;
      }
    LivingEntity entity = (LivingEntity) (Object) this;
    if (entity instanceof PlayerEntity playerr && LSAddonSupportClient.isClientPlayer(
        playerr.getUuid()) && playerr.isOnGround() && ClientEvents.onGroundFor >= 5) {
      return 1.198f;
    }
    return slipperiness;
  }

  @ModifyArg(
      method = "applyMovementInput",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;setVelocity(Lnet/minecraft/util/math/Vec3d;)V"),
      index = 0
  )
  private Vec3d applyMovementInput(Vec3d velocity) {
      if ((System.currentTimeMillis() - LSAddonSupportClient.CURSE_SLIDING) > 5000) {
          return velocity;
      }
    LivingEntity entity = (LivingEntity) (Object) this;
    if (entity instanceof PlayerEntity playerr && LSAddonSupportClient.isClientPlayer(
        playerr.getUuid()) && playerr.isOnGround() && ClientEvents.onGroundFor >= 5) {
      BlockPos blockPos = playerr.getVelocityAffectingPos();
      float originalSlipperiness = playerr.getWorld().getBlockState(blockPos).getBlock()
          .getSlipperiness();
      return new Vec3d((velocity.x / originalSlipperiness) * 0.995f, velocity.y,
          (velocity.z / originalSlipperiness) * 0.995f);
    }
    return velocity;
  }
}
