package com.cursee.ls_addon_support.mixin;

import static com.cursee.ls_addon_support.LSAddonSupport.blacklist;
import static com.cursee.ls_addon_support.LSAddonSupport.currentSeason;

import com.cursee.ls_addon_support.LSAddonSupport;
import com.cursee.ls_addon_support.events.Events;
import com.cursee.ls_addon_support.seasons.other.WatcherManager;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.wildcard.superpowers.Superpowers;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.wildcard.superpowers.SuperpowersWildcard;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.wildcard.superpowers.superpower.WindCharge;
import com.cursee.ls_addon_support.utils.world.ItemStackUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.CreakingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = LivingEntity.class, priority = 1)
public abstract class LivingEntityMixin {

  @Unique
  private DamageSource ls$lastDamageSource;

  @Inject(method = "heal", at = @At("HEAD"), cancellable = true)
  private void onHealHead(float amount, CallbackInfo info) {
      if (!LSAddonSupport.isLogicalSide()) {
          return;
      }
      if (!currentSeason.NO_HEALING) {
          return;
      }

    LivingEntity entity = (LivingEntity) (Object) this;
    if (entity instanceof ServerPlayerEntity) {
      info.cancel();
    }
  }

  @Inject(method = "heal", at = @At("TAIL"))
  private void onHeal(float amount, CallbackInfo info) {
      if (!LSAddonSupport.isLogicalSide()) {
          return;
      }
    LivingEntity entity = (LivingEntity) (Object) this;
    if (entity instanceof ServerPlayerEntity player) {
        if (WatcherManager.isWatcher(player)) {
            return;
        }
      currentSeason.onPlayerHeal(player, amount);
    }
  }

  //? if = 1.21.2 {
    /*@Inject(method = "isEntityLookingAtMe", at = @At("HEAD"), cancellable = true)
    public void isEntityLookingAtMe(LivingEntity entity, double d, boolean bl, boolean visualShape
            , Predicate<LivingEntity> predicate, DoubleSupplier[] entityYChecks, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity me = (LivingEntity) (Object) this;
        if (me instanceof CreakingEntity creaking) {
            if (creaking.isTeammate(entity)) cir.setReturnValue(false);
        }
    }
    *///?}

  @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
  public void damage(ServerWorld world, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
      if (!LSAddonSupport.isLogicalSide()) {
          return;
      }

    LivingEntity entity = (LivingEntity) (Object) this;
      if (entity instanceof CreakingEntity creaking) {
          creaking.hurtTime = 20;
      }

    ItemStack weapon = source.getWeaponStack();
      if (amount <= WindCharge.MAX_MACE_DAMAGE) {
          return;
      }
      if (weapon == null) {
          return;
      }
      if (weapon.isEmpty()) {
          return;
      }
      if (!weapon.isOf(Items.MACE)) {
          return;
      }
      if (!ItemStackUtils.hasCustomComponentEntry(weapon, "WindChargeSuperpower")) {
          return;
      }
      cir.setReturnValue(entity.damage(world, source, WindCharge.MAX_MACE_DAMAGE));
  }

    /*
        Superpowers
     */

  @Inject(method = "addStatusEffect(Lnet/minecraft/entity/effect/StatusEffectInstance;Lnet/minecraft/entity/Entity;)Z", at = @At("HEAD"), cancellable = true)
  public void addStatusEffect(StatusEffectInstance effect, Entity source,
      CallbackInfoReturnable<Boolean> cir) {
      if (!LSAddonSupport.isLogicalSide()) {
          return;
      }
    LivingEntity entity = (LivingEntity) (Object) this;
    if (entity instanceof ServerPlayerEntity) {
        if (!effect.isAmbient() && !effect.shouldShowIcon() && !effect.shouldShowParticles()) {
            return;
        }
      if (blacklist.getBannedEffects().contains(effect.getEffectType())) {
        cir.setReturnValue(false);
      }
    }
  }

    @Inject(method = "damage", at = @At("HEAD"))
    private void captureDamageSource(ServerWorld world, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        this.ls$lastDamageSource = source;
    }

  @ModifyArg(
      method = "damage",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;takeKnockback(DDD)V"),
      index = 0
  )
  private double modifyKnockback(double strength) {
      if (!LSAddonSupport.isLogicalSide()) {
          return strength;
      }
      if (ls$lastDamageSource == null) {
          return strength;
      }

    DamageSource source = ls$lastDamageSource;
    if (source.getAttacker() instanceof ServerPlayerEntity attacker &&
        source.getType() == attacker.getDamageSources().playerAttack(attacker).getType() &&
        SuperpowersWildcard.hasActivatedPower(attacker, Superpowers.SUPER_PUNCH)) {
      return 3;
    }
    return strength;
  }

  @Inject(method = "drop", at = @At("HEAD"))
  private void onDrop(ServerWorld world, DamageSource damageSource, CallbackInfo ci) {
      if (!LSAddonSupport.isLogicalSide()) {
          return;
      }
    Events.onEntityDropItems((LivingEntity) (Object) this, damageSource);
  }

    @Inject(method = "tryUseDeathProtector", at = @At("HEAD"))
  private void stopFakeTotem(DamageSource source, CallbackInfoReturnable<Boolean> cir) {
    LivingEntity entity = (LivingEntity) (Object) this;
    if (ItemStackUtils.hasCustomComponentEntry(entity.getMainHandStack(), "FakeTotem")) {
      entity.setStackInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
    }
    if (ItemStackUtils.hasCustomComponentEntry(entity.getOffHandStack(), "FakeTotem")) {
      entity.setStackInHand(Hand.OFF_HAND, ItemStack.EMPTY);
    }
  }
}
