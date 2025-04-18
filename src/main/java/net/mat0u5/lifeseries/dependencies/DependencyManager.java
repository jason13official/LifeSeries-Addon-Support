package net.mat0u5.lifeseries.dependencies;

import net.fabricmc.loader.api.FabricLoader;

public class DependencyManager {
    public static boolean cardinalComponentsLoaded() {
        if (isModLoaded("cardinal-components")) return true;
        return isModLoaded("cardinal-components-base") && isModLoaded("cardinal-components-entity");
    }

    public static boolean blockbenchImportLibraryLoaded() {
        return isModLoaded("bil");
    }

    public static boolean polymerLoaded() {
        return isModLoaded("polymer-bundled");
    }

    public static boolean clothConfigLoaded() {
        return isModLoaded("cloth-config");
    }

    public static boolean voicechatLoaded() {
        return isModLoaded("voicechat");
    }

    public static boolean wildLifeModsLoaded() {
        return cardinalComponentsLoaded() && blockbenchImportLibraryLoaded() && polymerLoaded();
    }

    public static boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }
}
