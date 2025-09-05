package com.cursee.ls_addon_support.mixin.client;

import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundSystem;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.tick.TickManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = SoundSystem.class, priority = 1)
public class SoundSystemMixin {

  @Unique
  private static final List<String> ls$nonAdjustedSounds = List.of(
      "block.beacon.deactivate",
      "wildlife_time_slow_down",
      "wildlife_time_speed_up",
      "wildlife_trivia_suspense",
      "wildlife_trivia_suspense_end"
  );

  @Inject(method = "getAdjustedPitch", at = @At("HEAD"), cancellable = true)
  private void getAdjustedPitch(SoundInstance sound, CallbackInfoReturnable<Float> cir) {
    String name = sound.getId().getPath();
      if (ls$nonAdjustedSounds.contains(name)) {
          return;
      }
    MinecraftClient client = MinecraftClient.getInstance();
    if (client.world != null) {
      TickManager tickManager = client.world.getTickManager();
      if (tickManager.getTickRate() != 20) {
        cir.setReturnValue(
            MathHelper.clamp(sound.getPitch(), 0.5F, 2.0F) * (tickManager.getTickRate() / 20.0f));
      }
    }
  }
}
