package com.cursee.ls_addon_support.mixin;

import com.cursee.ls_addon_support.LSAddonSupport;
import com.cursee.ls_addon_support.utils.world.ItemStackUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = CraftingScreenHandler.class, priority = 1)
public class CraftingScreenHandlerMixin {

  @Inject(method = "updateResult", at = @At("HEAD"), cancellable = true)
  private static void blockPreviewIfNoCraftingItemPresent(ScreenHandler handler, ServerWorld world, PlayerEntity player,
      RecipeInputInventory craftingInventory, CraftingResultInventory resultInventory, RecipeEntry<CraftingRecipe> recipe, CallbackInfo ci) {
      if (!LSAddonSupport.isLogicalSide()) {
          return;
      }

    for (int i = 0; i < craftingInventory.size(); i++) {
      ItemStack stack = craftingInventory.getStack(i);
      if (ItemStackUtils.hasCustomComponentEntry(stack, "NoCrafting") ||
          ItemStackUtils.hasCustomComponentEntry(stack, "NoModifications")) {
        resultInventory.setStack(0, ItemStack.EMPTY);
        ci.cancel();
        return;
      }
    }
  }
}
