package net.mat0u5.lifeseries.mixin.superpowers;

import net.mat0u5.lifeseries.series.wildlife.wildcards.wildcard.superpowers.Superpowers;
import net.mat0u5.lifeseries.series.wildlife.wildcards.wildcard.superpowers.SuperpowersWildcard;
import net.mat0u5.lifeseries.utils.PlayerUtils;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(value = EnchantmentHelper.class, priority = 1)
public class EnchantmentHelperMixin {

    @Inject(
            method = "onTargetDamaged(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/Entity;Lnet/minecraft/entity/damage/DamageSource;)V", at = @At("HEAD")
    )
    private static void onTargetDamaged(ServerWorld world, Entity victimEntity, DamageSource damageSource, CallbackInfo ci) {
        if (!(victimEntity instanceof ServerPlayerEntity victim)) return;
        if (damageSource == null) return;
        if (damageSource.getAttacker() == null) return;
        if (!SuperpowersWildcard.hasActivatedPower(victim, Superpowers.SUPER_PUNCH)) return;
        //? if <= 1.21 {
        damageSource.getAttacker().damage(victim.getDamageSources().thorns(victim), 1F);
        //?} else {
        /*damageSource.getAttacker().damage(PlayerUtils.getServerWorld(victim), victim.getDamageSources().thorns(victim), 1F);
        *///?}
    }
}
