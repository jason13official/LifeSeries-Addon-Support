package com.cursee.ls_addon_support.mixin;

import static com.cursee.ls_addon_support.LSAddonSupport.currentSeason;

import com.cursee.ls_addon_support.LSAddonSupport;
import com.cursee.ls_addon_support.seasons.season.Seasons;
import com.cursee.ls_addon_support.seasons.season.wildlife.WildLife;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.WildcardManager;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.Wildcards;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.wildcard.Hunger;
import net.minecraft.component.ComponentMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.component.MergedComponentMap;

@Mixin(value = Item.class, priority = 1)
public abstract class ItemMixin {

  @Accessor("components")
  public abstract ComponentMap normalComponents();

  @Inject(method = "getComponents", at = @At("HEAD"), cancellable = true)
  public void getComponents(CallbackInfoReturnable<ComponentMap> cir) {
    boolean isLogicalSide = LSAddonSupport.isLogicalSide();
    boolean hungerActive = false;
    if (isLogicalSide) {
      if (currentSeason instanceof WildLife && WildcardManager.isActiveWildcard(Wildcards.HUNGER)) {
        hungerActive = true;
      }
    } else {
      if (LSAddonSupport.clientHelper != null &&
          LSAddonSupport.clientHelper.getCurrentSeason() == Seasons.WILD_LIFE &&
          LSAddonSupport.clientHelper.getActiveWildcards().contains(Wildcards.HUNGER)) {
        hungerActive = true;
      }
    }
    if (hungerActive) {
      Item item = (Item) (Object) this;
        MergedComponentMap components = new MergedComponentMap(normalComponents());
      Hunger.defaultFoodComponents(item, components);
      cir.setReturnValue(components);
    }
  }

  @Inject(method = "finishUsing", at = @At("HEAD"))
  public void finishUsing(ItemStack stack, World world, LivingEntity user,
      CallbackInfoReturnable<ItemStack> cir) {
      if (!LSAddonSupport.isLogicalSide()) {
          return;
      }
    if (currentSeason instanceof WildLife && WildcardManager.isActiveWildcard(Wildcards.HUNGER)) {
      Item item = (Item) (Object) this;
      Hunger.finishUsing(item, normalComponents(), user);
    }
  }
}