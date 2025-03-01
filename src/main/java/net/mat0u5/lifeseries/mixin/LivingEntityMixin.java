package net.mat0u5.lifeseries.mixin;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.client.ClientEvents;
import net.mat0u5.lifeseries.series.wildlife.wildcards.wildcard.superpowers.Superpowers;
import net.mat0u5.lifeseries.series.wildlife.wildcards.wildcard.superpowers.SuperpowersWildcard;
import net.mat0u5.lifeseries.utils.ItemStackUtils;
import net.mat0u5.lifeseries.utils.OtherUtils;
import net.mat0u5.lifeseries.utils.morph.DummyInterface;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.DoubleSupplier;
import java.util.function.Predicate;

import static net.mat0u5.lifeseries.Main.currentSeries;

@Mixin(value = LivingEntity.class, priority = 1)
public abstract class LivingEntityMixin implements DummyInterface {
    @Inject(method = "heal", at = @At("HEAD"), cancellable = true)
    private void onHealHead(float amount, CallbackInfo info) {
        if (!Main.isLogicalSide())return;
        if (!currentSeries.NO_HEALING) return;

        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity instanceof ServerPlayerEntity player) {
            info.cancel();
        }
    }

    @Inject(method = "heal", at = @At("TAIL"))
    private void onHeal(float amount, CallbackInfo info) {
        if (!Main.isLogicalSide()) return;
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity instanceof ServerPlayerEntity player) {
            currentSeries.onPlayerHeal(player, amount);
        }
    }

    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    //? if <= 1.21 {
    public void damage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
     //?} else
    /*public void damage(ServerWorld world, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {*/
        if (!Main.isLogicalSide()) return;
        ItemStack weapon = source.getWeaponStack();
        if (amount <= 2) return;
        if (weapon == null) return;
        if (weapon.isEmpty()) return;
        if (!weapon.isOf(Items.MACE)) return;
        if (!ItemStackUtils.hasCustomComponentEntry(weapon, "WindChargeSuperpower")) return;
        LivingEntity entity = (LivingEntity) (Object) this;
        //? if <= 1.21 {
        cir.setReturnValue(entity.damage(source, 2));
         //?} else
        /*cir.setReturnValue(entity.damage(world, source, 2));*/
    }

    //? if = 1.21.2 {
    /*@Inject(method = "isEntityLookingAtMe", at = @At("HEAD"), cancellable = true)
    public void isEntityLookingAtMe(LivingEntity entity, double d, boolean bl, boolean visualShape, Predicate<LivingEntity> predicate, DoubleSupplier[] entityYChecks, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity me = (LivingEntity) (Object) this;
        if (me instanceof CreakingEntity creaking) {
            if (creaking.isTeammate(entity)) cir.setReturnValue(false);
        }
    }
    *///?}


    /*
        Morphing
     */
    public boolean dummy;
    public PlayerEntity player;

    @Override
    public void makeDummy() {
        dummy = true;
    }
    @Override
    public void setPlayer(PlayerEntity playerEntity) {
        player = playerEntity;
    }

    @Inject(method = "tickMovement", at = @At("HEAD"), cancellable = true)
    public void stopTickingMovement(CallbackInfo ci){
        if(dummy){
            ci.cancel();
        }
    }
    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    public void stopTicking(CallbackInfo ci){
        if(dummy){
            ci.cancel();
        }
    }

    /*
        Superpowers
     */

    @Unique
    private DamageSource lastDamageSource;


    //? if <= 1.21 {
    @Inject(method = "damage", at = @At("HEAD"))
    private void captureDamageSource(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        this.lastDamageSource = source;
    }
    //?} else {
    /*@Inject(method = "damage", at = @At("HEAD"))
    private void captureDamageSource(ServerWorld world, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        this.lastDamageSource = source;
    }
    *///?}

    @ModifyArg(
            method = "damage",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;takeKnockback(DDD)V"),
            index = 0
    )
    private double modifyKnockback(double strength) {
        if (!Main.isLogicalSide()) return strength;
        if (lastDamageSource != null) {
            DamageSource source = lastDamageSource;
            if (source.getAttacker() instanceof ServerPlayerEntity attacker && source.getType().msgId().equalsIgnoreCase("player")) {
                if (SuperpowersWildcard.hasActivatedPower(attacker, Superpowers.SUPER_PUNCH)) {
                    return 3;
                }
            }
        }
        return strength;
    }


    @Inject(method = "jump", at = @At("TAIL"))
    private void onJump(CallbackInfo ci) {
        if (!Main.isClient()) return;
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity instanceof ClientPlayerEntity) {
            ClientEvents.onClientJump();
        }
    }
}
