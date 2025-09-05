package com.cursee.ls_addon_support.dependencies;

import com.cursee.ls_addon_support.LSAddonSupport;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;

public class PolymerDependency {

  public static void onInitialize() {
    PolymerResourcePackUtils.addModAssets(LSAddonSupport.MOD_ID);
    PolymerResourcePackUtils.markAsRequired();
  }
}
