package com.cursee.ls_addon_support.network.packets;


import com.cursee.ls_addon_support.LSAddonSupport;
import java.util.List;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record StringListPayload(String name, List<String> value) implements CustomPayload {

  public static final Id<StringListPayload> ID = new Id<>(
      Identifier.of(LSAddonSupport.MOD_ID, "stringlist"));
  public static final PacketCodec<RegistryByteBuf, StringListPayload> CODEC = PacketCodec.tuple(
      PacketCodecs.STRING, StringListPayload::name,
      PacketCodecs.STRING.collect(PacketCodecs.toList()), StringListPayload::value,
      StringListPayload::new
  );

  @Override
  public Id<? extends CustomPayload> getId() {
    return ID;
  }
}