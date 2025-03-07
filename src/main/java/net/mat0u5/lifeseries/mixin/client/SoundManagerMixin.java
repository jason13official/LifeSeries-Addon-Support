package net.mat0u5.lifeseries.mixin.client;

import net.mat0u5.lifeseries.client.ClientSounds;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SoundManager.class, priority = 1)
public class SoundManagerMixin {
    @Inject(method = "play(Lnet/minecraft/client/sound/SoundInstance;)V", at = @At("HEAD"))
    private void getAdjustedPitch(SoundInstance sound, CallbackInfo ci) {
        ClientSounds.onSoundPlay(sound);
    }
}
