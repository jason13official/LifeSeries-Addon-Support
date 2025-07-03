package net.mat0u5.lifeseries.mixin.client;

import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = PlayerEntity.class, priority = 2)
public class PlayerEntityMixin {
    //? if >= 1.21.2 {
    /*@Inject(method = "canGlide", at = @At("HEAD"), cancellable = true)
    protected void canGlide(CallbackInfoReturnable<Boolean> cir) {
        if (ClientUtils.shouldPreventGliding()) {
            cir.setReturnValue(false);
        }
    }
    *///?}
}
