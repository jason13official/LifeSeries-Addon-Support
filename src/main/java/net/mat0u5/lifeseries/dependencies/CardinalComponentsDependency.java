package net.mat0u5.lifeseries.dependencies;

import net.mat0u5.lifeseries.series.wildlife.morph.MorphComponent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistryV3;
import org.ladysnake.cca.api.v3.entity.EntityComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentInitializer;
import org.ladysnake.cca.api.v3.entity.RespawnCopyStrategy;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public class CardinalComponentsDependency implements EntityComponentInitializer {
    public static final ComponentKey<MorphComponent> MORPH_COMPONENT =
            ComponentRegistryV3.INSTANCE.getOrCreate(Identifier.of("lifeseries","morph"), MorphComponent.class);

    @Override
    public  void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerForPlayers(MORPH_COMPONENT, MorphComponent::new, RespawnCopyStrategy.ALWAYS_COPY);
    }

    public static void getBaseDimensions(PlayerEntity player, EntityPose pose, CallbackInfoReturnable<EntityDimensions> cir) {
        if(MORPH_COMPONENT.isProvidedBy(player)) {
            float scaleRatio = 1 / player.getScale();
            MorphComponent morphComponent = MORPH_COMPONENT.get(player);
            LivingEntity dummy = morphComponent.getDummy();
            if (morphComponent.isMorphed() && dummy != null){
                cir.setReturnValue(dummy.getDimensions(pose).scaled(scaleRatio, scaleRatio));
            }
        }
    }

    public static float modifyEntityScale(Entity focusedEntity, float originalDistance) {
        if (focusedEntity instanceof PlayerEntity player && MORPH_COMPONENT.isProvidedBy(player)) {
            MorphComponent morphComponent = MORPH_COMPONENT.get(player);
            if (morphComponent.isMorphed()) {
                LivingEntity dummy = morphComponent.getDummy();
                if (dummy != null) {
                    float playerHeight = PlayerEntity.STANDING_DIMENSIONS.height();
                    float morphedHeight = dummy.getDimensions(EntityPose.STANDING).height();
                    float heightScale = morphedHeight / playerHeight;
                    return heightScale * 4.0F;
                }
            }
        }
        return originalDistance;
    }

    //? if <= 1.21 {
    public static void replaceRendering(AbstractClientPlayerEntity abstractClientPlayerEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci){
        if (MORPH_COMPONENT.isProvidedBy(abstractClientPlayerEntity)) {
            MorphComponent morphComponent = MORPH_COMPONENT.get(abstractClientPlayerEntity);
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
        if(MORPH_COMPONENT.isProvidedBy(entity)){
            MorphComponent morphComponent = MORPH_COMPONENT.get(entity);
            LivingEntity dummy = morphComponent.getDummy();
            if(morphComponent.isMorphed() && dummy != null){
                MinecraftClient.getInstance().getEntityRenderDispatcher().render(
                        dummy, x, y, z, tickDelta, matrices, vertexConsumers, light);
                ci.cancel();
            }
        }
    }
    *///?}

    public static void resetWildcardsOnPlayerJoin(ServerPlayerEntity player) {
        MORPH_COMPONENT.maybeGet(player).ifPresent(morphComponent -> morphComponent.setMorph(null));
    }

    public static boolean allowPlayerSizeChange(ServerPlayerEntity player) {
        if (MORPH_COMPONENT.maybeGet(player).isPresent()) {
            if (MORPH_COMPONENT.maybeGet(player).get().isMorphed()) return false;
        }
        return true;
    }

    public static void setMorph(ServerPlayerEntity player, EntityType<?> finalMorph) {
        MORPH_COMPONENT.maybeGet(player).ifPresent(morphComponent -> morphComponent.setMorph(finalMorph));
        MORPH_COMPONENT.sync(player);
    }

    public static void resetMorph(ServerPlayerEntity player) {
        MORPH_COMPONENT.maybeGet(player).ifPresent(morphComponent -> morphComponent.setMorph(null));
        MORPH_COMPONENT.sync(player);
    }
}
