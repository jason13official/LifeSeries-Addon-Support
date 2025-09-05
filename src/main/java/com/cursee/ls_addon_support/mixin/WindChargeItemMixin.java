package com.cursee.ls_addon_support.mixin;

import static com.cursee.ls_addon_support.LSAddonSupport.currentSeason;

import com.cursee.ls_addon_support.LSAddonSupport;
import com.cursee.ls_addon_support.seasons.season.Seasons;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.wildcard.superpowers.Superpowers;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.wildcard.superpowers.SuperpowersWildcard;
import com.cursee.ls_addon_support.utils.other.TaskScheduler;
import com.cursee.ls_addon_support.utils.player.PlayerUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.WindChargeItem;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//?}
//? if >= 1.21.2
/*import net.minecraft.util.ActionResult;*/

@Mixin(value = WindChargeItem.class, priority = 1)
public class WindChargeItemMixin {

  @Inject(method = "use", at = @At("RETURN"))
  public void use(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
      if (!LSAddonSupport.isLogicalSide()) {
          return;
      }
    if (user instanceof ServerPlayerEntity player) {
        if (currentSeason.getSeason() != Seasons.WILD_LIFE) {
            return;
        }
        if (!SuperpowersWildcard.hasActivatedPower(player, Superpowers.WIND_CHARGE)) {
            return;
        }

      TaskScheduler.scheduleTask(1, () -> {
        player.getInventory().insertStack(Items.WIND_CHARGE.getDefaultStack());
        player.getInventory().markDirty();
        PlayerUtils.updatePlayerInventory(player);
      });
    }
  }
}
