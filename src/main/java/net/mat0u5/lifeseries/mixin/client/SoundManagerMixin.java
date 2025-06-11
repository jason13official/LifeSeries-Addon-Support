package net.mat0u5.lifeseries.mixin.client;

import net.mat0u5.lifeseries.client.ClientSounds;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.sound.SoundSystem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = SoundManager.class, priority = 1)
public class SoundManagerMixin {
    //? if <= 1.21.5 {
    /*@Inject(method = "play(Lnet/minecraft/client/sound/SoundInstance;)V", at = @At("HEAD"))
    private void getAdjustedPitch(SoundInstance sound, CallbackInfo ci) {
        ClientSounds.onSoundPlay(sound);
    }
    *///?} else {
    @Inject(method = "play(Lnet/minecraft/client/sound/SoundInstance;)Lnet/minecraft/client/sound/SoundSystem$PlayResult;", at = @At("HEAD"))
    private void getAdjustedPitch(SoundInstance sound, CallbackInfoReturnable<SoundSystem.PlayResult> cir) {
        ClientSounds.onSoundPlay(sound);
    }
    //?}
}
