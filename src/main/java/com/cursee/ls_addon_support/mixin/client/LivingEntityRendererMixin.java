package com.cursee.ls_addon_support.mixin.client;

import com.cursee.ls_addon_support.seasons.season.wildlife.morph.MorphComponent;
import com.cursee.ls_addon_support.seasons.season.wildlife.morph.MorphManager;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = LivingEntityRenderer.class, priority = 1)
public abstract class LivingEntityRendererMixin {

//  //? if <= 1.21 {
//  @Inject(method = "getShadowRadius(Lnet/minecraft/entity/LivingEntity;)F", at = @At("HEAD"), cancellable = true)
//  public <T extends LivingEntity> void stopShadow(T livingEntity,
//      CallbackInfoReturnable<Float> cir) {
//    if (livingEntity instanceof PlayerEntity player) {
//      MorphComponent morphComponent = MorphManager.getComponent(player);
//      if (morphComponent != null && morphComponent.isMorphed()) {
//        cir.setReturnValue(0.0F);
//      }
//    }
//  }
//  //?}
}
