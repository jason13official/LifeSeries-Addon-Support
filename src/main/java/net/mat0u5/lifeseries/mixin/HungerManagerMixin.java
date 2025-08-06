package net.mat0u5.lifeseries.mixin;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.seasons.season.doublelife.DoubleLife;
import net.mat0u5.lifeseries.utils.interfaces.IHungerManager;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.mat0u5.lifeseries.Main.currentSeason;

@Mixin(value = HungerManager.class, priority = 1)
public class HungerManagerMixin implements IHungerManager {
    @Shadow
    private int foodLevel;
    @Shadow
    private float saturationLevel;
    @Shadow
    private float exhaustion;
    @Shadow
    private int foodTickTimer;
    @Shadow
    private int prevFoodLevel;

    @Unique
    ServerPlayerEntity ls$player;

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

    @Unique
    private float prevSaturationLevel;

    @Inject(method = "update", at = @At("HEAD"))
    private void updateHead(PlayerEntity player, CallbackInfo ci) {
        if (!Main.isLogicalSide()) return;
        if (player instanceof ServerPlayerEntity serverPlayer) {
            this.ls$player = serverPlayer;
        }
        prevSaturationLevel = this.saturationLevel;
    }

    @Inject(method = "update", at = @At("TAIL"))
    private void updateTail(PlayerEntity player, CallbackInfo ci) {
        if (!Main.isLogicalSide()) return;
        if (prevFoodLevel != foodLevel || prevSaturationLevel != saturationLevel) {
            ls$emitUpdate();
        }
    }

    @Inject(method = "setFoodLevel", at = @At("TAIL"))
    private void setFoodLevel(CallbackInfo ci) {
        ls$emitUpdate();
    }

    @Inject(method = "setExhaustion", at = @At("TAIL"))
    private void setExhaustion(CallbackInfo ci) {
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
        if (!Main.isLogicalSide() || ls$player == null) return;
        if (currentSeason instanceof DoubleLife doubleLife) {
            doubleLife.updateFoodFrom(ls$player);
        }
    }
}
