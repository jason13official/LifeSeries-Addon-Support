package com.cursee.ls_addon_support.mixin;

import static com.cursee.ls_addon_support.LSAddonSupport.currentSeason;

import com.cursee.ls_addon_support.LSAddonSupport;
import com.cursee.ls_addon_support.seasons.season.doublelife.DoubleLife;
import com.cursee.ls_addon_support.utils.interfaces.IHungerManager;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = HungerManager.class, priority = 1)
public class HungerManagerMixin implements IHungerManager {

  @Shadow
  private int foodLevel;
  @Shadow
  private float saturationLevel;

  @Unique
  private ServerPlayerEntity ls$player;
  @Unique
  private float ls$prevFoodLevel;
  @Unique
  private float ls$prevSaturationLevel;

  @Unique
  @Override
  public int ls$getFoodLevel() {
    return foodLevel;
  }

  @Unique
  @Override
  public float ls$getSaturationLevel() {
    return saturationLevel;
  }

  @Unique
  @Override
  public void ls$setFoodLevel(int foodLevel) {
    this.foodLevel = foodLevel;
  }

  @Unique
  @Override
  public void ls$setSaturationLevel(float saturationLevel) {
    this.saturationLevel = saturationLevel;
  }

  @Inject(method = "update", at = @At("HEAD"))
  private void updateHead(ServerPlayerEntity player, CallbackInfo ci) {
      if (!LSAddonSupport.isLogicalSide()) {
          return;
      }
    if (player instanceof ServerPlayerEntity serverPlayer) {
      this.ls$player = serverPlayer;
    }
    ls$prevFoodLevel = this.foodLevel;
    ls$prevSaturationLevel = this.saturationLevel;
  }

  @Inject(method = "update", at = @At("TAIL"))
  private void updateTail(ServerPlayerEntity player, CallbackInfo ci) {
      if (!LSAddonSupport.isLogicalSide()) {
          return;
      }
    if (ls$prevFoodLevel != foodLevel || ls$prevSaturationLevel != saturationLevel) {
      ls$emitUpdate();
    }
  }

  @Inject(method = "setFoodLevel", at = @At("TAIL"))
  private void setFoodLevel(CallbackInfo ci) {
    ls$emitUpdate();
  }

  @Inject(method = "setSaturationLevel", at = @At("TAIL"))
  private void setSaturationLevel(CallbackInfo ci) {
    ls$emitUpdate();
  }

  @Inject(method = "addInternal", at = @At("TAIL"))
  private void addInternal(CallbackInfo ci) {
    ls$emitUpdate();
  }

  @Unique
  private void ls$emitUpdate() {
      if (!LSAddonSupport.isLogicalSide() || ls$player == null) {
          return;
      }
    if (currentSeason instanceof DoubleLife doubleLife) {
      doubleLife.updateFoodFrom(ls$player);
    }
  }
}
