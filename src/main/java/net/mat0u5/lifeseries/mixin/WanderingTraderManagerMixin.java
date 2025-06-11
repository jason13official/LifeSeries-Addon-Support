package net.mat0u5.lifeseries.mixin;

import net.mat0u5.lifeseries.series.SeriesList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.WanderingTraderManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.mat0u5.lifeseries.Main.currentSeries;

@Mixin(value = WanderingTraderManager.class, priority = 1)
public class WanderingTraderManagerMixin {
    @Inject(method = "spawn", at = @At("HEAD"), cancellable = true)
    //? if <= 1.21.4 {
    public void spawn(ServerWorld world, boolean spawnMonsters, boolean spawnAnimals, CallbackInfoReturnable<Integer> cir) {
        if (currentSeries.getSeries() == SeriesList.SIMPLE_LIFE) {
            cir.setReturnValue(0);
        }
    }
    //?} else {
    /*public void spawn(ServerWorld world, boolean spawnMonsters, boolean spawnAnimals, CallbackInfo ci) {
        if (currentSeries.getSeries() == SeriesList.SIMPLE_LIFE) {
            ci.cancel();
        }
    }
    *///?}
}
