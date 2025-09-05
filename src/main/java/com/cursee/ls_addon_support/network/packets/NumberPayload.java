package com.cursee.ls_addon_support.network.packets;

import com.cursee.ls_addon_support.LSAddonSupport;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record NumberPayload(String name, double number) implements CustomPayload {

  public static final Id<NumberPayload> ID = new Id<>(
      Identifier.of(LSAddonSupport.MOD_ID, "number"));
  public static final PacketCodec<RegistryByteBuf, NumberPayload> CODEC = PacketCodec.tuple(
      PacketCodecs.STRING, NumberPayload::name,
      PacketCodecs.DOUBLE, NumberPayload::number,
      NumberPayload::new
  );

  @Override
  public Id<? extends CustomPayload> getId() {
    return ID;
  }
}