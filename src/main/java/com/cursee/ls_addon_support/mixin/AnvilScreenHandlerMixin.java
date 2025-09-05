package com.cursee.ls_addon_support.mixin;

import static com.cursee.ls_addon_support.LSAddonSupport.blacklist;

import com.cursee.ls_addon_support.LSAddonSupport;
import com.cursee.ls_addon_support.utils.world.ItemStackUtils;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import java.util.Optional;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.AnvilScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = AnvilScreenHandler.class, priority = 1)
public abstract class AnvilScreenHandlerMixin {

  @Inject(method = "updateResult", at = @At("TAIL"))
  private void modifyAnvilResultName(CallbackInfo ci) {
      if (!LSAddonSupport.isLogicalSide()) {
          return;
      }
      if (blacklist == null) {
          return;
      }
    ForgingScreenHandlerAccessor accessor = (ForgingScreenHandlerAccessor) this;
    Inventory outputInventory = accessor.getOutput();

    ItemStack resultStack = outputInventory.getStack(0);
    if (ItemStackUtils.hasCustomComponentEntry(resultStack, "NoAnvil") ||
        ItemStackUtils.hasCustomComponentEntry(resultStack, "NoModifications")) {
      outputInventory.setStack(0, ItemStack.EMPTY);
    }

      if (!resultStack.hasEnchantments()) {
          return;
      }

    resultStack.set(DataComponentTypes.ENCHANTMENTS,
        blacklist.clampAndBlacklistEnchantments(resultStack.getEnchantments()));
    if (ItemStackUtils.hasCustomComponentEntry(resultStack, "NoMending")) {
      for (Entry<RegistryEntry<Enchantment>> enchant : resultStack.getEnchantments()
          .getEnchantmentEntries()) {
        Optional<RegistryKey<Enchantment>> enchantRegistry = enchant.getKey().getKey();
          if (enchantRegistry.isEmpty()) {
              continue;
          }
        if (enchantRegistry.get() == Enchantments.MENDING) {
          outputInventory.setStack(0, ItemStack.EMPTY);
        }
      }
    }

  }
}
