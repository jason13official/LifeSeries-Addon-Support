package com.cursee.ls_addon_support.mixin;

import static com.cursee.ls_addon_support.LSAddonSupport.currentSeason;

import com.cursee.ls_addon_support.seasons.season.Seasons;
import com.cursee.ls_addon_support.utils.player.PlayerUtils;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ServerWorld.class, priority = 1)
public class ServerWorldMixin {

  @Inject(method = "sendEntityStatus", at = @At("HEAD"))
  public void broadcast(Entity entity, byte status, CallbackInfo ci) {
    if (status != (byte) 35 || currentSeason.getSeason() != Seasons.SECRET_LIFE) {
      return;
    }
    // This sound doesnt exist client-side, so it won't double
    PlayerUtils.playSoundWithSourceToPlayers(entity,
        SoundEvent.of(Identifier.of("secretlife_normal_totem")), entity.getSoundCategory(), 1, 1);
  }
}
