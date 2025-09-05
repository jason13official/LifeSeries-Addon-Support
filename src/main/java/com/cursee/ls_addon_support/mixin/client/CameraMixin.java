package com.cursee.ls_addon_support.mixin.client;

import com.cursee.ls_addon_support.seasons.season.wildlife.morph.MorphComponent;
import com.cursee.ls_addon_support.seasons.season.wildlife.morph.MorphManager;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Camera.class)
public class CameraMixin {

  @Shadow
  private Entity focusedEntity;

  @ModifyArg(
      method = "update",
      at = @At(
          value = "INVOKE",
          target = "Lnet/minecraft/client/render/Camera;clipToSpace(F)F"
      ),
      index = 0
  )
  private float modifyEntityScale(float originalDistance) {
      if (!(focusedEntity instanceof PlayerEntity player)) {
          return originalDistance;
      }
    MorphComponent morphComponent = MorphManager.getOrCreateComponent(player);
    if (morphComponent.isMorphed()) {
      LivingEntity dummy = morphComponent.getDummy();
      if (dummy != null) {
        float playerHeight = PlayerEntity.STANDING_DIMENSIONS.height();
        float morphedHeight = dummy.getDimensions(EntityPose.STANDING).height();
        float heightScale = morphedHeight / playerHeight;
        float cameraDistance = 4.0F;
        cameraDistance = (float)player.getAttributeValue(EntityAttributes.CAMERA_DISTANCE);
        return heightScale * cameraDistance;
      }
    }
    return originalDistance;
  }
}