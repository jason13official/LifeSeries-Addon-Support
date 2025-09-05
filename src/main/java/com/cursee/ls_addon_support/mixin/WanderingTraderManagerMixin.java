package com.cursee.ls_addon_support.mixin;

import static com.cursee.ls_addon_support.LSAddonSupport.currentSeason;

import com.cursee.ls_addon_support.LSAddonSupport;
import com.cursee.ls_addon_support.seasons.season.Seasons;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.WanderingTraderManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//? if >= 1.21.5
/*import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;*/

@Mixin(value = WanderingTraderManager.class, priority = 1)
public class WanderingTraderManagerMixin {

  @Inject(method = "spawn", at = @At("HEAD"), cancellable = true)
  //? if <= 1.21.4 {
  public void spawn(ServerWorld world, boolean spawnMonsters, boolean spawnAnimals,
      CallbackInfoReturnable<Integer> cir) {
      if (!LSAddonSupport.isLogicalSide()) {
          return;
      }
    if (currentSeason.getSeason() == Seasons.SIMPLE_LIFE) {
      cir.setReturnValue(0);
    }
  }
  //?} else {
    /*public void spawn(ServerWorld world, boolean spawnMonsters, boolean spawnAnimals, CallbackInfo ci) {
        if (!Main.isLogicalSide()) return;
        if (currentSeason.getSeason() == Seasons.SIMPLE_LIFE) {
            ci.cancel();
        }
    }
    *///?}
}
