package com.cursee.ls_addon_support.mixin;

import static com.cursee.ls_addon_support.LSAddonSupport.currentSeason;

import com.cursee.ls_addon_support.LSAddonSupport;
import com.cursee.ls_addon_support.seasons.season.Seasons;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.wildcard.superpowers.Superpowers;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.wildcard.superpowers.SuperpowersWildcard;
import com.cursee.ls_addon_support.utils.player.PlayerUtils;
import java.util.List;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.SleepManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = SleepManager.class, priority = 1)
public abstract class SleepManagerMixin {

  @Inject(method = "canResetTime", at = @At("RETURN"), cancellable = true)
  public void canResetTime(int percentage, List<ServerPlayerEntity> players,
      CallbackInfoReturnable<Boolean> cir) {
      if (!LSAddonSupport.isLogicalSide()) {
          return;
      }
      if (currentSeason.getSeason() != Seasons.WILD_LIFE) {
          return;
      }
    for (ServerPlayerEntity player : players) {
        if (!player.canResetTimeBySleeping()) {
            return;
        }
      if (SuperpowersWildcard.hasActivePower(player, Superpowers.TIME_CONTROL)) {
        cir.setReturnValue(true);
      }
    }
  }

  @Inject(method = "canSkipNight", at = @At("RETURN"), cancellable = true)
  public void canSkipNight(int percentage, CallbackInfoReturnable<Boolean> cir) {
      if (!LSAddonSupport.isLogicalSide()) {
          return;
      }
      if (currentSeason.getSeason() != Seasons.WILD_LIFE) {
          return;
      }
    for (ServerPlayerEntity player : PlayerUtils.getAllPlayers()) {
        if (!player.isSleeping()) {
            return;
        }
      if (SuperpowersWildcard.hasActivePower(player, Superpowers.TIME_CONTROL)) {
        cir.setReturnValue(true);
      }
    }
  }
}
