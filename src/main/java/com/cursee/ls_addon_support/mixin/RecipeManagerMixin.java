package com.cursee.ls_addon_support.mixin;

import static com.cursee.ls_addon_support.LSAddonSupport.blacklist;

import com.cursee.ls_addon_support.LSAddonSupport;
import java.util.List;
import net.minecraft.recipe.PreparedRecipes;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.ServerRecipeManager;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerRecipeManager.class)
public abstract class RecipeManagerMixin {

  @Shadow
  private PreparedRecipes preparedRecipes;

  @Inject(method = "apply", at = @At("HEAD"), cancellable = true)
  private void applyMixin(PreparedRecipes preparedRecipes, ResourceManager resourceManager,
      Profiler profiler, CallbackInfo ci) {
    if (!LSAddonSupport.isLogicalSide()) {
      return;
    }
    if (blacklist == null) {
      return;
    }
    if (blacklist.loadedListItemIdentifier == null) {
      blacklist.getItemBlacklist();
    }
    if (blacklist.loadedListItemIdentifier.isEmpty()) {
      return;
    }

    List<RecipeEntry<?>> filteredRecipes = preparedRecipes.recipes().stream()
        .filter(recipe -> !blacklist.loadedListItemIdentifier.contains(recipe.id().getValue()))
        .toList();

    this.preparedRecipes = PreparedRecipes.of(filteredRecipes);

    // Log the updated recipe count
    LSAddonSupport.LOGGER.info("Loaded {} recipes after filtering", filteredRecipes.size());

    // Cancel further processing of the original method
    ci.cancel();
  }

}