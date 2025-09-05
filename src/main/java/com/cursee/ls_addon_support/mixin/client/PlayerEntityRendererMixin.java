package com.cursee.ls_addon_support.mixin.client;

import com.cursee.ls_addon_support.LSAddonSupportClient;
import com.cursee.ls_addon_support.seasons.season.wildlife.morph.MorphComponent;
import com.cursee.ls_addon_support.seasons.season.wildlife.morph.MorphManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = EntityRenderDispatcher.class, priority = 1)
public class PlayerEntityRendererMixin {

  @Inject(method = "render(Lnet/minecraft/entity/Entity;DDDFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At("HEAD"), cancellable = true)
  public <E extends Entity> void render(Entity entity, double x, double y, double z,
      float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light,
      CallbackInfo ci) {
    if (entity instanceof PlayerEntity playerEntity) {
      if (LSAddonSupportClient.invisiblePlayers.containsKey(playerEntity.getUuid())) {
        long time = LSAddonSupportClient.invisiblePlayers.get(playerEntity.getUuid());
        if (time > System.currentTimeMillis() || time == -1) {
          ci.cancel();
          return;
        }
      }
      if (!(entity instanceof PlayerEntity player)) {
        return;
      }
      if (player.isSpectator() || player.isInvisible()) {
        return;
      }
      MorphComponent morphComponent = MorphManager.getOrCreateComponent(player);
      if (morphComponent.isMorphed()) {
        LivingEntity dummy = morphComponent.getDummy();
        if (morphComponent.isMorphed() && dummy != null) {
          MinecraftClient.getInstance().getEntityRenderDispatcher()
              .render(dummy, x, y, z, tickDelta, matrices, vertexConsumers, light);
          ci.cancel();
        }
      }
    }
  }
}