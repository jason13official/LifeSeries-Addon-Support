package com.cursee.ls_addon_support.mixin;

import static com.cursee.ls_addon_support.LSAddonSupport.currentSeason;

import com.cursee.ls_addon_support.LSAddonSupport;
import com.cursee.ls_addon_support.seasons.other.WatcherManager;
import com.cursee.ls_addon_support.seasons.season.doublelife.DoubleLife;
import com.cursee.ls_addon_support.seasons.season.wildlife.morph.MorphComponent;
import com.cursee.ls_addon_support.seasons.season.wildlife.morph.MorphManager;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.wildcard.superpowers.Superpowers;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.wildcard.superpowers.SuperpowersWildcard;
import com.cursee.ls_addon_support.utils.player.PlayerUtils;
import java.util.Optional;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentLevelBasedValue;
import net.minecraft.enchantment.effect.entity.ReplaceDiskEnchantmentEffect;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.gen.blockpredicate.BlockPredicate;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//? if >= 1.21.2
/*import net.minecraft.server.world.ServerWorld;*/

@Mixin(value = PlayerEntity.class, priority = 1)
public abstract class PlayerEntityMixin {

  @Unique
  private static final ReplaceDiskEnchantmentEffect ls$frostWalker = new ReplaceDiskEnchantmentEffect(
      EnchantmentLevelBasedValue.constant(5.0F), EnchantmentLevelBasedValue.constant(1.0F),
      new Vec3i(0, -1, 0), Optional.of(
      BlockPredicate.allOf(BlockPredicate.matchingBlockTag(new Vec3i(0, 1, 0), BlockTags.AIR),
          BlockPredicate.matchingBlocks(Blocks.WATER), BlockPredicate.matchingFluids(Fluids.WATER),
          BlockPredicate.unobstructed())), BlockStateProvider.of(Blocks.FROSTED_ICE),
      Optional.of(GameEvent.BLOCK_PLACE));

  @Inject(method = "applyDamage", at = @At("HEAD"), cancellable = true)
  private void onApplyDamage(ServerWorld world, DamageSource source, float amount, CallbackInfo ci) {
      if (!LSAddonSupport.isLogicalSide()) {
          return;
      }
    PlayerEntity player = (PlayerEntity) (Object) this;
      if (WatcherManager.isWatcher(player)) {
          return;
      }

    if (player instanceof ServerPlayerEntity serverPlayer) {
      currentSeason.onPlayerDamage(serverPlayer, source, amount, ci);
    }
  }

  @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
  private void onPreDamage(ServerWorld world, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
      if (!LSAddonSupport.isLogicalSide()) {
          return;
      }
    PlayerEntity player = (PlayerEntity) (Object) this;
      if (WatcherManager.isWatcher(player)) {
          return;
      }

    if (player instanceof ServerPlayerEntity serverPlayer) {
      currentSeason.onPrePlayerDamage(serverPlayer, source, amount, cir);
    }
  }

  @Inject(method = "canFoodHeal", at = @At("HEAD"), cancellable = true)
  private void canFoodHeal(CallbackInfoReturnable<Boolean> cir) {
      if (!LSAddonSupport.isLogicalSide()) {
          return;
      }
    if (currentSeason instanceof DoubleLife doubleLife) {
      PlayerEntity player = (PlayerEntity) (Object) this;
        if (WatcherManager.isWatcher(player)) {
            return;
        }

      if (player instanceof ServerPlayerEntity serverPlayer) {
        doubleLife.canFoodHeal(serverPlayer, cir);
      }
    }
  }

  @Inject(method = "getBaseDimensions", at = @At("HEAD"), cancellable = true)
  public void getBaseDimensions(EntityPose pose, CallbackInfoReturnable<EntityDimensions> cir) {
    PlayerEntity player = (PlayerEntity) (Object) this;
    MorphComponent morphComponent = MorphManager.getOrCreateComponent(player);
      if (!morphComponent.isMorphed()) {
          return;
      }

    float scaleRatio = 1 / player.getScale();
    LivingEntity dummy = morphComponent.getDummy();
    if (morphComponent.isMorphed() && dummy != null) {
      cir.setReturnValue(dummy.getDimensions(pose).scaled(scaleRatio, scaleRatio));
    }
  }

  @Inject(method = "tick", at = @At("HEAD"))
  private void updateHitbox(CallbackInfo ci) {
    ((PlayerEntity) (Object) this).calculateDimensions();
  }

  @Inject(method = "travel", at = @At("HEAD"))
  private void travel(Vec3d movementInput, CallbackInfo ci) {
    LivingEntity entity = (LivingEntity) (Object) this;
      if (!(entity instanceof ServerPlayerEntity player)) {
          return;
      }
      if (!player.isOnGround()) {
          return;
      }
      if (!SuperpowersWildcard.hasActivatedPower(player, Superpowers.SUPERSPEED)) {
          return;
      }

    ls$frostWalker.apply(PlayerUtils.getServerWorld(player), 5, null, player, player.getPos());
  }
}
