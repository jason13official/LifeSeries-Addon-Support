package com.cursee.ls_addon_support.mixin;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.component.MergedComponentMap;

@Mixin(value = ItemStack.class, priority = 1)
public class ItemStackMixin {

  @Inject(method = "areItemsAndComponentsEqual", at = @At("HEAD"), cancellable = true)
  private static void areItemsAndComponentsEqual(ItemStack stack, ItemStack otherStack,
      CallbackInfoReturnable<Boolean> cir) {
      if (!stack.isOf(otherStack.getItem())) {
          return;
      }

    if (stack.isEmpty() && otherStack.isEmpty()) {
      cir.setReturnValue(true);
      return;
    }
    if (stack.equals(otherStack)) {
      cir.setReturnValue(true);
      return;
    }
    MergedComponentMap comp1 = new MergedComponentMap(stack.getComponents());
    MergedComponentMap comp2 = new MergedComponentMap(otherStack.getComponents());

    comp1.set(DataComponentTypes.FOOD, stack.getDefaultComponents().get(DataComponentTypes.FOOD));
    comp2.set(DataComponentTypes.FOOD, stack.getDefaultComponents().get(DataComponentTypes.FOOD));

    comp1.set(DataComponentTypes.CONSUMABLE, stack.getDefaultComponents().get(DataComponentTypes.CONSUMABLE));
    comp2.set(DataComponentTypes.CONSUMABLE, stack.getDefaultComponents().get(DataComponentTypes.CONSUMABLE));
    if (Objects.equals(comp1, comp2)) {
      cir.setReturnValue(true);
      return;
    }

    boolean componentsEqual = true;

    Set<ComponentType<?>> allTypes = new HashSet<>();
    allTypes.addAll(comp1.getTypes());
    allTypes.addAll(comp2.getTypes());

    for (ComponentType<?> type : allTypes) {
        if (type.equals(DataComponentTypes.FOOD)) {
            continue;
        }
      if (type.equals(DataComponentTypes.CONSUMABLE)) continue;

      Object value1 = comp1.get(type);
      Object value2 = comp2.get(type);

      if (!Objects.equals(value1, value2)) {
        componentsEqual = false;
        break;
      }
    }

    cir.setReturnValue(componentsEqual);
  }
}
