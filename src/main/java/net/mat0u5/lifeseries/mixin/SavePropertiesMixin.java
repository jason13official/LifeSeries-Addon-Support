package net.mat0u5.lifeseries.mixin;

import net.minecraft.world.SaveProperties;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = SaveProperties.class, priority = 1)
public interface SavePropertiesMixin {
    //? if = 1.21.2 {
    /*@Inject(method = "getEnabledFeatures", at = @At("HEAD"), cancellable = true)
    default void getEnabledFeatures(CallbackInfoReturnable<FeatureSet> cir) {
        SaveProperties defaultProperties = (SaveProperties) (Object) this;
        if (!defaultProperties.getDataConfiguration().enabledFeatures().contains(FeatureFlags.WINTER_DROP)) {
            cir.setReturnValue(defaultProperties.getDataConfiguration().withFeaturesAdded(FeatureSet.of(FeatureFlags.WINTER_DROP)).enabledFeatures());
        }
    }
    *///?}
}
