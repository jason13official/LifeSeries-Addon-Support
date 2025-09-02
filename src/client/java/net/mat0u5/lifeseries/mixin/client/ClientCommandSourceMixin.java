package net.mat0u5.lifeseries.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.mat0u5.lifeseries.MainClient;
import net.minecraft.client.network.ClientCommandSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Collection;

@Mixin(value = ClientCommandSource.class, priority = 1)
public class ClientCommandSourceMixin {

    @ModifyReturnValue(method = "getPlayerNames", at = @At("RETURN"))
    private Collection<String> addHiddenPlayers(Collection<String> original) {
        if (MainClient.hiddenTabPlayers != null) {
            for (String hiddenName : MainClient.hiddenTabPlayers) {
                if (original.contains(hiddenName)) continue;
                original.add(hiddenName);
            }
        }
        return original;
    }
}
