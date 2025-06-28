package net.mat0u5.lifeseries.mixin.superpowers.client;

import net.mat0u5.lifeseries.series.wildlife.morph.Morph;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
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
        return Morph.modifyEntityScale(focusedEntity, originalDistance);
    }
}