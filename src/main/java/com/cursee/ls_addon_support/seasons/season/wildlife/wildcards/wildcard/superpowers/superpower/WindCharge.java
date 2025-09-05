package com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.wildcard.superpowers.superpower;

import com.cursee.ls_addon_support.network.NetworkHandlerServer;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.wildcard.superpowers.Superpowers;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.wildcard.superpowers.ToggleableSuperpower;
import com.cursee.ls_addon_support.utils.other.TaskScheduler;
import com.cursee.ls_addon_support.utils.player.AttributeUtils;
import com.cursee.ls_addon_support.utils.world.ItemStackUtils;
import java.util.Set;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Unit;

public class WindCharge extends ToggleableSuperpower {

  public static int MAX_MACE_DAMAGE = 2;

  public WindCharge(ServerPlayerEntity player) {
    super(player);
  }

  @Override
  public Superpowers getSuperpower() {
    return Superpowers.WIND_CHARGE;
  }

  @Override
  public void activate() {
    super.activate();
    ServerPlayerEntity player = getPlayer();
      if (player == null) {
          return;
      }
    player.playSoundToPlayer(SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.MASTER, 0.3f, 1);
    AttributeUtils.setSafeFallHeight(player, 100000);
    giveMace();
    giveWindCharge();
    NetworkHandlerServer.sendVignette(player, 300);
  }

  @Override
  public void deactivate() {
    super.deactivate();
    ServerPlayerEntity player = getPlayer();
    if (player != null) {
      player.playSoundToPlayer(SoundEvents.ENTITY_WIND_CHARGE_WIND_BURST.value(),
          SoundCategory.MASTER, 0.3f, 1);
      TaskScheduler.scheduleTask(1, () -> player.getInventory().markDirty());
      AttributeUtils.resetSafeFallHeight(player);
    }
  }

  private void giveWindCharge() {
    ServerPlayerEntity player = getPlayer();
    if (player != null && !player.getInventory().containsAny(Set.of(Items.WIND_CHARGE))) {
      ItemStack windCharge = new ItemStack(Items.WIND_CHARGE, 4);
      player.getInventory().insertStack(windCharge);
    }

  }

  private void giveMace() {
    ServerPlayerEntity player = getPlayer();
    if (player != null) {
      ItemStack mace = new ItemStack(Items.MACE);
      mace.addEnchantment(ItemStackUtils.getEnchantmentEntry(Enchantments.VANISHING_CURSE), 1);
      mace.addEnchantment(ItemStackUtils.getEnchantmentEntry(Enchantments.WIND_BURST), 3);
        mace.set(DataComponentTypes.UNBREAKABLE, Unit.INSTANCE);
      mace.set(DataComponentTypes.MAX_DAMAGE, 1);
      mace.set(DataComponentTypes.DAMAGE, 1);
      ItemStackUtils.setCustomComponentBoolean(mace, "IgnoreBlacklist", true);
      ItemStackUtils.setCustomComponentBoolean(mace, "FromSuperpower", true);
      ItemStackUtils.setCustomComponentBoolean(mace, "WindChargeSuperpower", true);
      player.getInventory().insertStack(mace);
    }
  }
}
