package com.cursee.ls_addon_support.network.packets;

import com.cursee.ls_addon_support.LSAddonSupport;
import java.util.List;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record TriviaQuestionPayload(String question, int difficulty, long timestamp,
                                    int timeToComplete, List<String> answers) implements
    CustomPayload {

  public static final Id<TriviaQuestionPayload> ID = new Id<>(
      Identifier.of(LSAddonSupport.MOD_ID, "triviaquestion"));
  public static final PacketCodec<RegistryByteBuf, TriviaQuestionPayload> CODEC = PacketCodec.tuple(
      PacketCodecs.STRING, TriviaQuestionPayload::question,
      PacketCodecs.INTEGER, TriviaQuestionPayload::difficulty,
      PacketCodecs.VAR_LONG, TriviaQuestionPayload::timestamp,
      PacketCodecs.INTEGER, TriviaQuestionPayload::timeToComplete,
      PacketCodecs.STRING.collect(PacketCodecs.toList()), TriviaQuestionPayload::answers,
      TriviaQuestionPayload::new
  );

  @Override
  public Id<? extends CustomPayload> getId() {
    return ID;
  }
}