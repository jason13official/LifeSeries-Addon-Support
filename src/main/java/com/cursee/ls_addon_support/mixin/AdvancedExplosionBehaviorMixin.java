package com.cursee.ls_addon_support.mixin;

import static com.cursee.ls_addon_support.LSAddonSupport.currentSeason;

import com.cursee.ls_addon_support.LSAddonSupport;
import com.cursee.ls_addon_support.seasons.season.Seasons;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.wildcard.superpowers.Superpowers;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.wildcard.superpowers.SuperpowersWildcard;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.explosion.AdvancedExplosionBehavior;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = AdvancedExplosionBehavior.class, priority = 1)
public class AdvancedExplosionBehaviorMixin {

  @Inject(method = "getKnockbackModifier", at = @At("RETURN"), cancellable = true)
  public void getKnockbackModifier(Entity entity, CallbackInfoReturnable<Float> cir) {
      if (!LSAddonSupport.isLogicalSide()) {
          return;
      }
    if (entity instanceof ServerPlayerEntity player) {
        if (currentSeason.getSeason() != Seasons.WILD_LIFE) {
            return;
        }
        if (player.getAbilities().flying) {
            return;
        }
      if (SuperpowersWildcard.hasActivatedPower(player, Superpowers.WIND_CHARGE)) {
        cir.setReturnValue(3f); // Default is 1.22f
      }
    }
  }
}
