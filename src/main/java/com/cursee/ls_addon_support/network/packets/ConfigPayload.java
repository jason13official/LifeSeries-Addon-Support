package com.cursee.ls_addon_support.network.packets;

import com.cursee.ls_addon_support.LSAddonSupport;
import java.util.List;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record ConfigPayload(String configType, String id, int index, String name,
                            String description, List<String> args) implements CustomPayload {

  public static final Id<ConfigPayload> ID = new Id<>(
      Identifier.of(LSAddonSupport.MOD_ID, "config"));
  public static final PacketCodec<RegistryByteBuf, ConfigPayload> CODEC = PacketCodec.tuple(
      PacketCodecs.STRING, ConfigPayload::configType,
      PacketCodecs.STRING, ConfigPayload::id,
      PacketCodecs.INTEGER, ConfigPayload::index,
      PacketCodecs.STRING, ConfigPayload::name,
      PacketCodecs.STRING, ConfigPayload::description,
      PacketCodecs.STRING.collect(PacketCodecs.toList()), ConfigPayload::args,
      ConfigPayload::new
  );

  @Override
  public Id<? extends CustomPayload> getId() {
    return ID;
  }
}