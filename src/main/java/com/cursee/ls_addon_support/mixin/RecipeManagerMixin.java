package com.cursee.ls_addon_support.mixin;

import static com.cursee.ls_addon_support.LSAddonSupport.blacklist;

import com.cursee.ls_addon_support.LSAddonSupport;
import com.google.gson.JsonElement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = RecipeManager.class, priority = 1)
public class RecipeManagerMixin {

  @Inject(method = "apply(Ljava/util/Map;Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/util/profiler/Profiler;)V", at = @At("HEAD"))
  private void applyMixin(Map<Identifier, JsonElement> map, ResourceManager resourceManager,
      Profiler profiler, CallbackInfo info) {
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

    List<Identifier> toRemove = new ArrayList<>();

    for (Identifier identifier : map.keySet()) {
      if (blacklist.loadedListItemIdentifier.contains(identifier)) {
        toRemove.add(identifier);
      }
    }
    toRemove.forEach(map::remove);
  }

}
//?} else {
/*import net.minecraft.recipe.ServerRecipeManager;
import net.minecraft.recipe.PreparedRecipes;
import net.mat0u5.lifeseries.Main;
import net.minecraft.recipe.RecipeEntry;
import org.spongepowered.asm.mixin.Shadow;
@Mixin(ServerRecipeManager.class)
public abstract class RecipeManagerMixin {

    @Shadow
    private PreparedRecipes preparedRecipes;

    @Inject(method = "apply", at = @At("HEAD"), cancellable = true)
    private void applyMixin(PreparedRecipes preparedRecipes, ResourceManager resourceManager, Profiler profiler, CallbackInfo ci) {
        if (!Main.isLogicalSide()) return;
        if (blacklist == null) return;
        if (blacklist.loadedListItemIdentifier == null)  {
            blacklist.getItemBlacklist();
        }
        if (blacklist.loadedListItemIdentifier.isEmpty()) return;

        List<RecipeEntry<?>> filteredRecipes = preparedRecipes.recipes().stream()
            .filter(recipe -> !blacklist.loadedListItemIdentifier.contains(recipe.id().getValue()))
            .toList();

        this.preparedRecipes = PreparedRecipes.of(filteredRecipes);

        // Log the updated recipe count
        Main.LOGGER.info("Loaded {} recipes after filtering", filteredRecipes.size());

        // Cancel further processing of the original method
        ci.cancel();
    }

}
*///?}