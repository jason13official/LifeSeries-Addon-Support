package com.cursee.ls_addon_support.registries;

import com.cursee.ls_addon_support.entity.pathfinder.PathFinder;
import com.cursee.ls_addon_support.entity.snail.Snail;
import com.cursee.ls_addon_support.entity.triviabot.TriviaBot;
import eu.pb4.polymer.core.api.entity.PolymerEntityUtils;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityType;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
//?}
//? if >= 1.21.2 {
/*import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityType;
import net.minecraft.registry.RegistryKey;
*///?}

public class MobRegistry {

  public static final EntityType<Snail> SNAIL = register(
      Snail.ID,
      FabricEntityType.Builder.createMob(Snail::new, SpawnGroup.MONSTER, x -> x
              .defaultAttributes(Snail::createAttributes))
          .dimensions(0.5f, 0.6f)
          .maxTrackingRange(10)
  );
  public static final EntityType<PathFinder> PATH_FINDER = register(
      PathFinder.ID,
      FabricEntityType.Builder.createMob(PathFinder::new, SpawnGroup.AMBIENT, x -> x
              .defaultAttributes(PathFinder::createAttributes))
          .dimensions(0.5f, 0.6f)
          .maxTrackingRange(10)
  );
  public static final EntityType<TriviaBot> TRIVIA_BOT = register(
      TriviaBot.ID,
      FabricEntityType.Builder.createMob(TriviaBot::new, SpawnGroup.AMBIENT, x -> x
              .defaultAttributes(TriviaBot::createAttributes))
          .dimensions(0.65f, 1.8f)
          .maxTrackingRange(10)
  );

  private static <T extends Entity> EntityType<T> register(Identifier id, EntityType.Builder<T> builder) {
    EntityType<T> type = builder.build(RegistryKey.of(Registries.ENTITY_TYPE.getKey(), id));
    PolymerEntityUtils.registerType(type);

    return Registry.register(Registries.ENTITY_TYPE, id, type);
  }

  public static void registerMobs() {
  }
}

