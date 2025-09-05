package com.cursee.ls_addon_support.network.packets;

import com.cursee.ls_addon_support.LSAddonSupport;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record StringPayload(String name, String value) implements CustomPayload {

  public static final Id<StringPayload> ID = new Id<>(
      Identifier.of(LSAddonSupport.MOD_ID, "string"));
  public static final PacketCodec<RegistryByteBuf, StringPayload> CODEC = PacketCodec.tuple(
      PacketCodecs.STRING, StringPayload::name,
      PacketCodecs.STRING, StringPayload::value,
      StringPayload::new
  );

  @Override
  public Id<? extends CustomPayload> getId() {
    return ID;
  }
}