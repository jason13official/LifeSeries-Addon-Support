package com.cursee.ls_addon_support.mixin;

import static com.cursee.ls_addon_support.LSAddonSupport.currentSeason;

import com.cursee.ls_addon_support.LSAddonSupport;
import com.cursee.ls_addon_support.seasons.season.Seasons;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.WanderingTraderManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = WanderingTraderManager.class, priority = 1)
public class WanderingTraderManagerMixin {

  @Inject(method = "spawn", at = @At("HEAD"), cancellable = true)
  public void spawn(ServerWorld world, boolean spawnMonsters, boolean spawnAnimals, CallbackInfo ci) {
    if (!LSAddonSupport.isLogicalSide()) return;
    if (currentSeason.getSeason() == Seasons.SIMPLE_LIFE) {
      ci.cancel();
    }
  }
}
