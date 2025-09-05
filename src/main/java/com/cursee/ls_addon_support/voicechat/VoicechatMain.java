package com.cursee.ls_addon_support.voicechat;

import static com.cursee.ls_addon_support.LSAddonSupport.currentSeason;

import com.cursee.ls_addon_support.LSAddonSupport;
import com.cursee.ls_addon_support.entity.triviabot.TriviaBot;
import com.cursee.ls_addon_support.seasons.season.Seasons;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.WildcardManager;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.Wildcards;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.wildcard.superpowers.Superpowers;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.wildcard.superpowers.SuperpowersWildcard;
import com.cursee.ls_addon_support.seasons.season.wildlife.wildcards.wildcard.superpowers.superpower.Listening;
import com.cursee.ls_addon_support.utils.player.PlayerUtils;
import com.cursee.ls_addon_support.voicechat.soundeffects.RadioEffect;
import com.cursee.ls_addon_support.voicechat.soundeffects.RoboticVoice;
import de.maxhenkel.voicechat.api.Position;
import de.maxhenkel.voicechat.api.VoicechatApi;
import de.maxhenkel.voicechat.api.VoicechatConnection;
import de.maxhenkel.voicechat.api.VoicechatPlugin;
import de.maxhenkel.voicechat.api.VoicechatServerApi;
import de.maxhenkel.voicechat.api.events.EventRegistration;
import de.maxhenkel.voicechat.api.events.MicrophonePacketEvent;
import de.maxhenkel.voicechat.api.opus.OpusDecoder;
import de.maxhenkel.voicechat.api.opus.OpusEncoder;
import de.maxhenkel.voicechat.api.packets.LocationalSoundPacket;
import de.maxhenkel.voicechat.api.packets.MicrophonePacket;
import java.util.UUID;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;

public class VoicechatMain implements VoicechatPlugin {

  private OpusEncoder encoder;
  private OpusDecoder decoder;

  @Override
  public String getPluginId() {
    return "lifeseries";
  }

  @Override
  public void initialize(VoicechatApi api) {
    LSAddonSupport.LOGGER.info("Life Series Voice Chat plugin initialized!");
    this.encoder = api.createEncoder();
    this.decoder = api.createDecoder();
  }

  @Override
  public void registerEvents(EventRegistration registration) {
    registration.registerEvent(MicrophonePacketEvent.class, this::onAudioPacket);
  }

  private void onAudioPacket(MicrophonePacketEvent event) {
    roboticVoice(event);
    listeningPower(event);
  }

  private void roboticVoice(MicrophonePacketEvent event) {
    if (currentSeason.getSeason() != Seasons.WILD_LIFE) {
      return;
    }
    if (!WildcardManager.isActiveWildcard(Wildcards.TRIVIA)) {
      return;
    }
    try {
      VoicechatConnection connection = event.getSenderConnection();
        if (connection == null) {
            return;
        }
      UUID senderUUID = connection.getPlayer().getUuid();
      if (!TriviaBot.cursedRoboticVoicePlayers.contains(senderUUID)) {
        return;
      }

      byte[] opusData = event.getPacket().getOpusEncodedData();
      byte[] processedOpusData = processOpusAudioForRobot(senderUUID, opusData);

      event.getPacket().setOpusEncodedData(processedOpusData);
    } catch (Exception e) {
      LSAddonSupport.LOGGER.error("Error processing audio", e);
    }
  }

  private void listeningPower(MicrophonePacketEvent event) {
    if (currentSeason.getSeason() != Seasons.WILD_LIFE) {
      return;
    }
    if (Listening.listeningPlayers.isEmpty()) {
      return;
    }

    MicrophonePacket voicePacket = event.getPacket();
    VoicechatConnection connection = event.getSenderConnection();
    if (connection == null) {
      return;
    }

    UUID senderUUID = connection.getPlayer().getUuid();
    Position senderPosition = connection.getPlayer().getPosition();

    VoicechatServerApi api = event.getVoicechat();
    for (UUID uuid : Listening.listeningPlayers) {
      if (uuid == senderUUID) {
        continue;
      }
      VoicechatConnection playerConnection = api.getConnectionOf(uuid);
      ServerPlayerEntity player = PlayerUtils.getPlayer(uuid);

      if (playerConnection == null || player == null) {
        continue;
      }
      Vec3d lookingAt = null;
      if (SuperpowersWildcard.hasActivatedPower(player, Superpowers.LISTENING)) {
        if (SuperpowersWildcard.getSuperpowerInstance(player) instanceof Listening listeningPower) {
          lookingAt = listeningPower.lookingAt;
        }
      }

      if (lookingAt == null || api.getBroadcastRange() == 0) {
        continue;
      }

      Vec3d senderPos = new Vec3d(senderPosition.getX(), senderPosition.getY(),
          senderPosition.getZ());
      double distanceFromSound = senderPos.distanceTo(lookingAt);
      double maxDistance = Math.min(api.getBroadcastRange(), Listening.MAX_RANGE);
      if (distanceFromSound > maxDistance) {
        continue;
      }
      double scaled = api.getBroadcastRange() / Listening.MAX_RANGE;
      if ((distanceFromSound * scaled) > player.getPos().distanceTo(senderPos)) {
        continue;
      }

      byte[] processedAudio = processOpusAudioForRadio(voicePacket.getOpusEncodedData().clone());
      LocationalSoundPacket processedPacket = voicePacket.locationalSoundPacketBuilder()
          .position(api.createPosition(player.getX(), player.getY() + (distanceFromSound * scaled),
              player.getZ()))
          .distance((float) api.getBroadcastRange())
          .opusEncodedData(processedAudio)
          .build();
      api.sendLocationalSoundPacketTo(playerConnection, processedPacket);
    }
  }

  private byte[] processOpusAudioForRadio(byte[] opusData) {
    try {
      short[] pcmData = decoder.decode(opusData);
      if (pcmData == null) {
        LSAddonSupport.LOGGER.warn("Failed to decode Opus data");
        return opusData;
      }

      short[] processedPcm = RadioEffect.applyEffect(pcmData);

      return encoder.encode(processedPcm);
    } catch (Exception e) {
      LSAddonSupport.LOGGER.error("Error processing Opus audio", e);
      return opusData;
    }
  }

  private byte[] processOpusAudioForRobot(UUID uuid, byte[] opusData) {
    try {
      short[] pcmData = decoder.decode(opusData);
      if (pcmData == null) {
        LSAddonSupport.LOGGER.warn("Failed to decode Opus data");
        return opusData;
      }

      short[] processedPcm = RoboticVoice.applyEffect(uuid, pcmData);

      return encoder.encode(processedPcm);
    } catch (Exception e) {
      LSAddonSupport.LOGGER.error("Error processing Opus audio", e);
      return opusData;
    }
  }
}