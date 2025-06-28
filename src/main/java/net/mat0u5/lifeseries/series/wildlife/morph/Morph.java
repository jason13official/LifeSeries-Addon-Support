package net.mat0u5.lifeseries.series.wildlife.morph;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public class Morph {
    public static void getBaseDimensions(PlayerEntity player, EntityPose pose, CallbackInfoReturnable<EntityDimensions> cir) {
        MorphComponent morphComponent = MorphManager.getOrCreateComponent(player);
        if (morphComponent.isMorphed()) {
            float scaleRatio = 1 / player.getScale();
            LivingEntity dummy = morphComponent.getDummy();
            if (morphComponent.isMorphed() && dummy != null){
                cir.setReturnValue(dummy.getDimensions(pose).scaled(scaleRatio, scaleRatio));
            }
        }
    }

    public static float modifyEntityScale(Entity focusedEntity, float originalDistance) {
        if (!(focusedEntity instanceof PlayerEntity player)) return originalDistance;
        MorphComponent morphComponent = MorphManager.getOrCreateComponent(player);
        if (morphComponent.isMorphed()) {
            LivingEntity dummy = morphComponent.getDummy();
            if (dummy != null) {
                float playerHeight = PlayerEntity.STANDING_DIMENSIONS.height();
                float morphedHeight = dummy.getDimensions(EntityPose.STANDING).height();
                float heightScale = morphedHeight / playerHeight;
                return heightScale * 4.0F;
            }
        }
        return originalDistance;
    }

    //? if <= 1.21 {
    public static void replaceRendering(AbstractClientPlayerEntity abstractClientPlayerEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci){
        if (abstractClientPlayerEntity.isSpectator() || abstractClientPlayerEntity.isInvisible()) return;
        MorphComponent morphComponent = MorphManager.getOrCreateComponent(abstractClientPlayerEntity);
        if (morphComponent.isMorphed()) {
            LivingEntity dummy = morphComponent.getDummy();
            if(morphComponent.isMorphed() && dummy != null){
                MinecraftClient.getInstance().getEntityRenderDispatcher().render(
                        dummy, 0, 0, 0, f, g, matrixStack, vertexConsumerProvider, i);
                ci.cancel();
            }
        }
    }
    //?} else {
    /*public static <E extends Entity> void render(Entity entity, double x, double y, double z, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if (!(entity instanceof PlayerEntity player)) return;
        if (player.isSpectator() || player.isInvisible()) return;
        MorphComponent morphComponent = MorphManager.getOrCreateComponent(player);
        if(morphComponent.isMorphed()) {
            LivingEntity dummy = morphComponent.getDummy();
            if(morphComponent.isMorphed() && dummy != null){
                MinecraftClient.getInstance().getEntityRenderDispatcher().render(
                        dummy, x, y, z, tickDelta, matrices, vertexConsumers, light);
                ci.cancel();
            }
        }
    }
    *///?}
}