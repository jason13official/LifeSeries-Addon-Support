package com.cursee.ls_addon_support.mixin;

import static com.cursee.ls_addon_support.LSAddonSupport.currentSeason;

import com.cursee.ls_addon_support.LSAddonSupport;
import com.cursee.ls_addon_support.seasons.season.wildlife.WildLife;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.WildcardManager;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.Wildcards;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.wildcard.MobSwap;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = SpawnHelper.class, priority = 1)
public class SpawnHelperMixin {

  @Inject(method = "isAcceptableSpawnPosition", at = @At("HEAD"), cancellable = true)
  private static void isAcceptableSpawnPosition(ServerWorld world, Chunk chunk,
      BlockPos.Mutable pos, double squaredDistance, CallbackInfoReturnable<Boolean> cir) {
      if (!LSAddonSupport.isLogicalSide()) {
          return;
      }
    if (currentSeason instanceof WildLife) {
      if (WildcardManager.isActiveWildcard(Wildcards.MOB_SWAP)) {
        MobSwap.isAcceptableSpawnPosition(world, chunk, pos, squaredDistance, cir);
      }
    }
  }
}
