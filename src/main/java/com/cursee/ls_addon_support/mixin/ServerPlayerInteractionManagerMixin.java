package com.cursee.ls_addon_support.mixin;

import static com.cursee.ls_addon_support.LSAddonSupport.currentSeason;

import com.cursee.ls_addon_support.LSAddonSupport;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ServerPlayerInteractionManager.class, priority = 1)
public class ServerPlayerInteractionManagerMixin {

  @Inject(at = @At("RETURN"), method = "interactBlock")
  private void onInteractBlock(ServerPlayerEntity player, World world, ItemStack stack, Hand hand,
      BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> cir) {
      if (!LSAddonSupport.isLogicalSide()) {
          return;
      }
    currentSeason.onUpdatedInventory(player);
  }

  @Inject(at = @At("RETURN"), method = "interactItem")
  private void onInteractItem(ServerPlayerEntity player, World world, ItemStack stack, Hand hand,
      CallbackInfoReturnable<ActionResult> cir) {
      if (!LSAddonSupport.isLogicalSide()) {
          return;
      }
    currentSeason.onUpdatedInventory(player);
  }
}
