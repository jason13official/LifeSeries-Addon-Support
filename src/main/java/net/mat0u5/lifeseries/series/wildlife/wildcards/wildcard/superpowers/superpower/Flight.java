package net.mat0u5.lifeseries.series.wildlife.wildcards.wildcard.superpowers.superpower;

import net.mat0u5.lifeseries.network.NetworkHandlerServer;
import net.mat0u5.lifeseries.series.wildlife.wildcards.wildcard.superpowers.Superpower;
import net.mat0u5.lifeseries.series.wildlife.wildcards.wildcard.superpowers.Superpowers;
import net.mat0u5.lifeseries.utils.ItemStackUtils;
import net.mat0u5.lifeseries.utils.OtherUtils;
import net.mat0u5.lifeseries.utils.PlayerUtils;
import net.mat0u5.lifeseries.utils.TaskScheduler;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.*;
import net.minecraft.component.type.*;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Unit;

import javax.tools.Tool;
import java.util.Optional;

public class Flight extends Superpower {
    public boolean isLaunchedUp = false;
    private int onGroundTicks = 0;
    private long ticks = 0;

    public Flight(ServerPlayerEntity player) {
        super(player);
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
        ticks++;
        ServerPlayerEntity player = getPlayer();
        if (player == null) {
            onGroundTicks = 0;
            return;
        }
        if (!isLaunchedUp) {
            onGroundTicks = 0;
            if (ticks % 5 == 0) NetworkHandlerServer.sendStringPacket(player, "prevent_gliding", "true");
            return;
        }

        if (player.isOnGround()) {
            onGroundTicks++;
            if (ticks % 5 == 0) NetworkHandlerServer.sendStringPacket(player, "prevent_gliding", "true");
        }

        else {
            onGroundTicks = 0;
        }

        if (onGroundTicks >= 10) {
            isLaunchedUp = false;
            onGroundTicks = 0;
        }
    }

    @Override
    public void activate() {
        super.activate();
        ServerPlayerEntity player = getPlayer();
        if (player == null) return;
        giveHelmet();

        PlayerUtils.getServerWorld(player).playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_FIREWORK_ROCKET_LAUNCH, SoundCategory.MASTER, 1, 1);
        player.playSoundToPlayer(SoundEvents.ENTITY_FIREWORK_ROCKET_LAUNCH, SoundCategory.MASTER, 1, 1);

        StatusEffectInstance effect = new StatusEffectInstance(StatusEffects.JUMP_BOOST, 20, 54, false, false, false);
        player.addStatusEffect(effect);
        NetworkHandlerServer.sendStringPacket(player, "jump", "");

        isLaunchedUp = true;
        NetworkHandlerServer.sendStringPacket(player, "prevent_gliding", "false");
    }

    @Override
    public void deactivate() {
        super.deactivate();
        ServerPlayerEntity player = getPlayer();
        if (player == null) return;
        TaskScheduler.scheduleTask(1, () -> player.getInventory().markDirty());
        NetworkHandlerServer.sendStringPacket(player, "prevent_gliding", "false");
    }

    private void giveHelmet() {
        ServerPlayerEntity player = getPlayer();
        if (player != null) {
            if (ItemStackUtils.hasCustomComponentEntry(PlayerUtils.getEquipmentSlot(player, 3), "FlightSuperpower")) return;

            ItemStack helmet = new ItemStack(Items.IRON_NUGGET);
            helmet.addEnchantment(ItemStackUtils.getEnchantmentEntry(Enchantments.BINDING_CURSE), 1);
            helmet.addEnchantment(ItemStackUtils.getEnchantmentEntry(Enchantments.VANISHING_CURSE), 1);
            ItemEnchantmentsComponent enchantmentsComponent = helmet.get(DataComponentTypes.ENCHANTMENTS);
            //? if <= 1.21.4 {
            if (enchantmentsComponent != null) {
                helmet.set(DataComponentTypes.ENCHANTMENTS, enchantmentsComponent.withShowInTooltip(false));
            }
            helmet.set(DataComponentTypes.UNBREAKABLE, new UnbreakableComponent(false));
            helmet.set(DataComponentTypes.HIDE_ADDITIONAL_TOOLTIP, Unit.INSTANCE);
             //?} else {
            /*helmet.set(DataComponentTypes.UNBREAKABLE, Unit.INSTANCE);
            helmet.set(DataComponentTypes.TOOLTIP_DISPLAY, TooltipDisplayComponent.DEFAULT
                    .with(DataComponentTypes.ENCHANTMENTS, true)
                    .with(DataComponentTypes.UNBREAKABLE, true)
            );
            *///?}

            helmet.set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, false);
            helmet.set(DataComponentTypes.ITEM_NAME, Text.of("Winged Helmet"));
            //? if >= 1.21.2 {
            /*helmet.set(DataComponentTypes.ITEM_MODEL, Identifier.of("lifeseries","winged_helmet"));
            helmet.set(DataComponentTypes.GLIDER, Unit.INSTANCE);
                //? if <= 1.21.4 {
                helmet.set(DataComponentTypes.EQUIPPABLE, new EquippableComponent(EquipmentSlot.HEAD, SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, Optional.empty(), Optional.empty(), Optional.empty(), false, false, false));
                //?} else if <= 1.21.5 {
                /^helmet.set(DataComponentTypes.EQUIPPABLE, new EquippableComponent(EquipmentSlot.HEAD, SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, Optional.empty(), Optional.empty(), Optional.empty(), false, false, false, false));
                ^///?} else {
                /^helmet.set(DataComponentTypes.EQUIPPABLE, new EquippableComponent(EquipmentSlot.HEAD, SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, Optional.empty(), Optional.empty(), Optional.empty(), false, false, false, false, false, Registries.SOUND_EVENT.getEntry(SoundEvents.ITEM_SHEARS_SNIP)));
                ^///?}
            *///?}
            ItemStackUtils.setCustomComponentBoolean(helmet, "IgnoreBlacklist", true);
            ItemStackUtils.setCustomComponentBoolean(helmet, "FromSuperpower", true);
            ItemStackUtils.setCustomComponentBoolean(helmet, "FlightSuperpower", true);

            ItemStackUtils.spawnItemForPlayer(PlayerUtils.getServerWorld(player), player.getPos(), PlayerUtils.getEquipmentSlot(player, 3).copy(), player);
            player.equipStack(EquipmentSlot.HEAD, helmet);
        }
    }
}
