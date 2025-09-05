package com.cursee.ls_addon_support.mixin;

import static com.cursee.ls_addon_support.LSAddonSupport.currentSeason;

import com.cursee.ls_addon_support.LSAddonSupport;
import com.cursee.ls_addon_support.entity.snail.Snail;
import com.cursee.ls_addon_support.seasons.season.wildlife.WildLife;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.WildcardManager;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.Wildcards;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.wildcard.snails.Snails;
import com.cursee.ls_addon_support.utils.interfaces.IEntityDataSaver;
import com.cursee.ls_addon_support.utils.interfaces.IMorph;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.EvokerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//? if >= 1.21.2 {
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.EntityType;
import net.minecraft.server.world.ServerWorld;

@Mixin(value = Entity.class, priority = 1)
public abstract class EntityMixin implements IEntityDataSaver, IMorph {
    /*
    private NbtCompound persistentData;
    @Override
    public NbtCompound getPersistentData() {
        if (persistentData == null) {
            persistentData = new NbtCompound();
        }
        return persistentData;
    }

    @Inject(method = "writeNbt", at = @At("HEAD"))
    protected void writeNbt(NbtCompound nbt, CallbackInfoReturnable<NbtCompound> cir) {
        if (persistentData != null) {
            nbt.put("lifeseries", persistentData);
        }
    }

    @Inject(method = "readNbt", at = @At("HEAD"))
    protected void readNbt(NbtCompound nbt, CallbackInfo ci) {
        if (nbt.contains("lifeseries")) {
            persistentData = nbt.getCompound("lifeseries");
        }
    }
    */

  @Unique
  private boolean ls$fromMorph = false;

  @Unique
  @Override
  public boolean isFromMorph() {
    return ls$fromMorph;
  }

  @Unique
  @Override
  public void setFromMorph(boolean fromMorph) {
    this.ls$fromMorph = fromMorph;
  }

  @Inject(method = "getAir", at = @At("RETURN"), cancellable = true)
  public void getAir(CallbackInfoReturnable<Integer> cir) {
      if (!LSAddonSupport.isLogicalSide()) {
          return;
      }
    if (currentSeason instanceof WildLife) {
        if (!Snail.SHOULD_DROWN_PLAYER) {
            return;
        }
        if (!WildcardManager.isActiveWildcard(Wildcards.SNAILS)) {
            return;
        }
      Entity entity = (Entity) (Object) this;
      if (entity instanceof PlayerEntity player && !player.hasStatusEffect(
          StatusEffects.WATER_BREATHING)) {
          if (!Snails.snails.containsKey(player.getUuid())) {
              return;
          }
        Snail snail = Snails.snails.get(player.getUuid());
          if (snail == null) {
              return;
          }
        int snailAir = snail.getAir();
        int initialAir = cir.getReturnValue();
        if (snailAir < initialAir) {
          cir.setReturnValue(snailAir);
        }
      }
    }
  }

  @Inject(method = "dropStack(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/item/ItemStack;F)Lnet/minecraft/entity/ItemEntity;",
      at = @At("HEAD"), cancellable = true)
  public void dropStack(ServerWorld world, ItemStack stack, float yOffset, CallbackInfoReturnable<ItemEntity> cir) {
      if (!LSAddonSupport.isLogicalSide()) {
          return;
      }
    if (currentSeason instanceof WildLife) {
      Entity entity = (Entity) (Object) this;
      if (entity instanceof EvokerEntity && stack.isOf(Items.TOTEM_OF_UNDYING)) {
        cir.setReturnValue(null);
      }
    }
  }

  @WrapOperation(
            method = "startRiding(Lnet/minecraft/entity/Entity;Z)Z",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityType;isSaveable()Z")
    )
    private boolean allowRidingPlayers(EntityType instance, Operation<Boolean> original) {
        if( instance == EntityType.PLAYER) {
            return true;
        } else {
            return original.call(instance);
        }
    }
}