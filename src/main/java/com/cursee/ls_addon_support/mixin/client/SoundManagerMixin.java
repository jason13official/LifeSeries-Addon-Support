package com.cursee.ls_addon_support.mixin.client;

import com.cursee.ls_addon_support.utils.ClientSounds;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.sound.SoundSystem;
import net.minecraft.client.sound.SoundSystem.PlayResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = SoundManager.class, priority = 1)
public class SoundManagerMixin {

  @Inject(method = "play(Lnet/minecraft/client/sound/SoundInstance;)Lnet/minecraft/client/sound/SoundSystem$PlayResult;", at = @At("HEAD"))
  private void play(SoundInstance sound, CallbackInfoReturnable<PlayResult> cir) {
    ClientSounds.onSoundPlay(sound);
  }
}
