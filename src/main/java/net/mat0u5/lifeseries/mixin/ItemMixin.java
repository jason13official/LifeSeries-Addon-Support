package net.mat0u5.lifeseries.mixin;

import net.mat0u5.lifeseries.Main;
import net.mat0u5.lifeseries.series.SeriesList;
import net.mat0u5.lifeseries.series.wildlife.WildLife;
import net.mat0u5.lifeseries.series.wildlife.wildcards.WildcardManager;
import net.mat0u5.lifeseries.series.wildlife.wildcards.Wildcards;
import net.mat0u5.lifeseries.series.wildlife.wildcards.wildcard.Hunger;
import net.minecraft.component.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.mat0u5.lifeseries.Main.currentSeries;
import static net.mat0u5.lifeseries.MainClient.clientActiveWildcards;
import static net.mat0u5.lifeseries.MainClient.clientCurrentSeries;

@Mixin(value = Item.class, priority = 1)
public abstract class ItemMixin {
    @Accessor("components")
    public abstract ComponentMap normalComponents();

    @Inject(method = "getComponents", at = @At("HEAD"), cancellable = true)
    public void getComponents(CallbackInfoReturnable<ComponentMap> cir) {
        boolean isLogicalSide = Main.isLogicalSide();
        if ((currentSeries instanceof WildLife && isLogicalSide) || (clientCurrentSeries == SeriesList.WILD_LIFE && !isLogicalSide)) {
            if ((WildcardManager.isActiveWildcard(Wildcards.HUNGER) && isLogicalSide) || (clientActiveWildcards.contains(Wildcards.HUNGER) && !isLogicalSide)) {
                Item item = (Item) (Object) this;
                //? if <= 1.21 {
                /*ComponentMapImpl components = new ComponentMapImpl(normalComponents());
                 *///?} else {
                MergedComponentMap components = new MergedComponentMap(normalComponents());
                //?}
                Hunger.defaultFoodComponents(item, components);
                cir.setReturnValue(components);
            }
        }
    }

    @Inject(method = "finishUsing", at = @At("HEAD"))
    public void finishUsing(ItemStack stack, World world, LivingEntity user, CallbackInfoReturnable<ItemStack> cir) {
        if (!Main.isLogicalSide()) return;
        if (currentSeries instanceof WildLife && WildcardManager.isActiveWildcard(Wildcards.HUNGER)) {
            Item item = (Item) (Object) this;
            Hunger.finishUsing(item, normalComponents(), user);
        }
    }
}