package net.mat0u5.lifeseries.series.wildlife.wildcards.wildcard.superpowers.superpower;

import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.mat0u5.lifeseries.series.wildlife.wildcards.wildcard.superpowers.Superpower;
import net.mat0u5.lifeseries.series.wildlife.wildcards.wildcard.superpowers.Superpowers;
import net.mat0u5.lifeseries.utils.ItemStackUtils;
import net.mat0u5.lifeseries.utils.OtherUtils;
import net.mat0u5.lifeseries.utils.TaskScheduler;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.*;
import net.minecraft.component.type.UnbreakableComponent;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.Unit;

import java.util.Optional;

public class Flight extends Superpower {
    public boolean cancelNextFallDamage = false;
    private int onGroundTicks = 0;

    public Flight(ServerPlayerEntity player) {
        super(player);
        giveHelmet();
    }

    @Override
    public Superpowers getSuperpower() {
        return Superpowers.FLIGHT;
    }

    @Override
    public int getCooldownMillis() {
        return 45000;
    }

    @Override
    public void tick() {
        if (!cancelNextFallDamage) return;
        ServerPlayerEntity player = getPlayer();
        if (player == null) return;
        if (player.isOnGround()) {
            onGroundTicks++;
        }
        else {
            onGroundTicks = 0;
        }

        if (onGroundTicks >= 5) {
            cancelNextFallDamage = false;
        }
    }

    @Override
    public void activate() {
        super.activate();
        ServerPlayerEntity player = getPlayer();
        if (player == null) return;
        giveHelmet();

        player.getServerWorld().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_FIREWORK_ROCKET_LAUNCH, SoundCategory.MASTER, 1, 1);
        player.playSoundToPlayer(SoundEvents.ENTITY_FIREWORK_ROCKET_LAUNCH, SoundCategory.MASTER, 1, 1);

        StatusEffectInstance effect = new StatusEffectInstance(StatusEffects.JUMP_BOOST, 20, 54, false, false, false);
        player.addStatusEffect(effect);
        NetworkHandlerServer.sendStringPacket(player, "jump", "");


        cancelNextFallDamage = true;
    }

    @Override
    public void deactivate() {
        super.deactivate();
        ServerPlayerEntity player = getPlayer();
        if (player == null) return;
        TaskScheduler.scheduleTask(1, () -> player.getInventory().markDirty());
    }

    private void giveHelmet() {
        ServerPlayerEntity player = getPlayer();
        if (player != null) {
            if (ItemStackUtils.hasCustomComponentEntry(player.getInventory().getArmorStack(3), "FlightSuperpower")) return;

            ItemStack helmet = new ItemStack(Items.IRON_NUGGET);
            helmet.addEnchantment(ItemStackUtils.getEnchantmentEntry(Enchantments.BINDING_CURSE), 1);
            helmet.addEnchantment(ItemStackUtils.getEnchantmentEntry(Enchantments.VANISHING_CURSE), 1);
            helmet.set(DataComponentTypes.UNBREAKABLE, new UnbreakableComponent(true));
            helmet.set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, false);
            //? if >= 1.21.2 {
            /*helmet.set(DataComponentTypes.ITEM_MODEL, Identifier.of("lifeseries","winged_helmet"));
            helmet.set(DataComponentTypes.GLIDER, Unit.INSTANCE);
            helmet.set(DataComponentTypes.EQUIPPABLE, new EquippableComponent(EquipmentSlot.HEAD, SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, Optional.empty(), Optional.empty(), Optional.empty(), false, false, false));
            *///?}
            ItemStackUtils.setCustomComponentBoolean(helmet, "IgnoreBlacklist", true);
            ItemStackUtils.setCustomComponentBoolean(helmet, "FromSuperpower", true);
            ItemStackUtils.setCustomComponentBoolean(helmet, "FlightSuperpower", true);


            ItemStackUtils.spawnItemForPlayer(player.getServerWorld(), player.getPos(), player.getInventory().getArmorStack(3).copy(), player);
            player.equipStack(EquipmentSlot.HEAD, helmet);
        }
    }
}
