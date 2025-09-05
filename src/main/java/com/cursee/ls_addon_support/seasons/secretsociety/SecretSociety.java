package com.cursee.ls_addon_support.seasons.secretsociety;

import static com.cursee.ls_addon_support.LSAddonSupport.currentSession;
import static com.cursee.ls_addon_support.LSAddonSupport.seasonConfig;
import static com.cursee.ls_addon_support.LSAddonSupport.server;

import com.cursee.ls_addon_support.seasons.session.SessionAction;
import com.cursee.ls_addon_support.seasons.session.SessionTranscript;
import com.cursee.ls_addon_support.utils.other.OtherUtils;
import com.cursee.ls_addon_support.utils.other.TaskScheduler;
import com.cursee.ls_addon_support.utils.other.TextUtils;
import com.cursee.ls_addon_support.utils.player.PlayerUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class SecretSociety {

  public static final int INITIATE_MESSAGE_DELAYS = 15 * 20;
  public boolean SOCIETY_ENABLED = false;
  public double START_TIME = 5.0;
  public int MEMBER_COUNT = 3;
  public List<String> FORCE_MEMBERS = new ArrayList<>();
  public List<String> IGNORE_MEMBERS = new ArrayList<>();
  public List<String> POSSIBLE_WORDS = new ArrayList<>(
      List.of("Hammer", "Magnet", "Throne", "Gravity", "Puzzle", "Spiral", "Pivot", "Flare"));
  public List<SocietyMember> members = new ArrayList<>();
  public boolean societyStarted = false;
  public long ticks = 0;
  public String secretWord = "";
  public Random rnd = new Random();

  public void onReload() {
    SOCIETY_ENABLED = seasonConfig.SECRET_SOCIETY.get(seasonConfig);
    if (!SOCIETY_ENABLED) {
      onDisabledSociety();
    }

    MEMBER_COUNT = seasonConfig.SECRET_SOCIETY_MEMBER_AMOUNT.get(seasonConfig);
    START_TIME = seasonConfig.SECRET_SOCIETY_START_TIME.get(seasonConfig);

    FORCE_MEMBERS.clear();
    IGNORE_MEMBERS.clear();
    POSSIBLE_WORDS.clear();
    for (String name : seasonConfig.SECRET_SOCIETY_FORCE.get(seasonConfig).replaceAll("\\[", "")
        .replaceAll("]", "").replaceAll(" ", "").trim().split(",")) {
        if (!name.isEmpty()) {
            FORCE_MEMBERS.add(name.toLowerCase());
        }
    }
    for (String name : seasonConfig.SECRET_SOCIETY_IGNORE.get(seasonConfig).replaceAll("\\[", "")
        .replaceAll("]", "").replaceAll(" ", "").trim().split(",")) {
        if (!name.isEmpty()) {
            IGNORE_MEMBERS.add(name.toLowerCase());
        }
    }
    for (String name : seasonConfig.SECRET_SOCIETY_WORDS.get(seasonConfig).replaceAll("\\[", "")
        .replaceAll("]", "").replaceAll(" ", "").trim().split(",")) {
        if (!name.isEmpty()) {
            POSSIBLE_WORDS.add(name.toLowerCase());
        }
    }
  }

  public void addSessionActions() {
    currentSession.activeActions.add(new SessionAction(OtherUtils.minutesToTicks(START_TIME),
        TextUtils.formatString("§7Begin Secret Society §f[{}]",
            OtherUtils.formatTime(OtherUtils.minutesToTicks(START_TIME))), "Begin Secret Society") {
      @Override
      public void trigger() {
          if (!SOCIETY_ENABLED) {
              return;
          }
        startSociety();
      }
    });
  }

  public void startSociety() {
    startSociety();
  }

  public void startSociety(String word) {
      if (!SOCIETY_ENABLED) {
          return;
      }
      if (server == null) {
          return;
      }
    if (word == null && !POSSIBLE_WORDS.isEmpty()) {
      word = POSSIBLE_WORDS.get(rnd.nextInt(POSSIBLE_WORDS.size()));
    }
    if (word != null) {
      this.secretWord = word;
    }

    societyStarted = true;
    SessionTranscript.societyStarted();
    ticks = 0;
    resetMembers();
    chooseMembers(PlayerUtils.getAllFunctioningPlayers());
  }

  public void endSociety() {
    resetMembers();
    societyStarted = false;
  }

  public boolean isMember(ServerPlayerEntity player) {
    SocietyMember member = getMember(player);
    return member == null;
  }

  @Nullable
  public SocietyMember getMember(ServerPlayerEntity player) {
    for (SocietyMember member : members) {
      if (member.uuid == player.getUuid()) {
        return member;
      }
    }
    return null;
  }

  public void chooseMembers(List<ServerPlayerEntity> allowedPlayers) {
      if (!SOCIETY_ENABLED) {
          return;
      }
    Collections.shuffle(allowedPlayers);
    List<ServerPlayerEntity> memberPlayers = getRandomMembers(allowedPlayers);
    List<ServerPlayerEntity> nonMemberPlayers = new ArrayList<>();

    for (ServerPlayerEntity player : allowedPlayers) {
        if (memberPlayers.contains(player)) {
            continue;
        }
      nonMemberPlayers.add(player);
    }

    memberPlayers.forEach(this::addMember);
    SessionTranscript.societyMembersChosen(memberPlayers);

    PlayerUtils.playSoundToPlayers(nonMemberPlayers,
        SoundEvent.of(Identifier.of("minecraft", "secretlife_task")));
    PlayerUtils.playSoundToPlayers(memberPlayers,
        SoundEvent.of(Identifier.of("minecraft", "secretlife_task")));
    PlayerUtils.sendTitleToPlayers(memberPlayers, Text.of("§cThe Society calls"), 0, 30, 0);

    TaskScheduler.scheduleTask(15, () -> {
      PlayerUtils.sendTitleToPlayers(memberPlayers, Text.of("§cThe Society calls."), 0, 30, 0);
    });
    TaskScheduler.scheduleTask(30, () -> {
      PlayerUtils.sendTitleToPlayers(memberPlayers, Text.of("§cThe Society calls.."), 0, 30, 0);
    });
    TaskScheduler.scheduleTask(45, () -> {
      PlayerUtils.sendTitleToPlayers(memberPlayers, Text.of("§cThe Society calls..."), 0, 45, 30);
    });
    TaskScheduler.scheduleTask(115, () -> {
      PlayerUtils.sendTitleWithSubtitleToPlayers(memberPlayers, Text.empty(),
          Text.of("§cTake yourself somewhere quiet"), 20, 60, 20);
    });
  }

  public List<ServerPlayerEntity> getRandomMembers(List<ServerPlayerEntity> allowedPlayers) {
    List<ServerPlayerEntity> memberPlayers = new ArrayList<>();
    int remainingMembers = MEMBER_COUNT;
    for (ServerPlayerEntity player : allowedPlayers) {
        if (IGNORE_MEMBERS.contains(player.getNameForScoreboard().toLowerCase())) {
            continue;
        }
      if (FORCE_MEMBERS.contains(player.getNameForScoreboard().toLowerCase())) {
        memberPlayers.add(player);
        remainingMembers--;
      }
    }

    for (ServerPlayerEntity player : allowedPlayers) {
        if (remainingMembers <= 0) {
            break;
        }
        if (IGNORE_MEMBERS.contains(player.getNameForScoreboard().toLowerCase())) {
            continue;
        }
        if (FORCE_MEMBERS.contains(player.getNameForScoreboard().toLowerCase())) {
            continue;
        }
        if (memberPlayers.contains(player)) {
            continue;
        }
      memberPlayers.add(player);
      remainingMembers--;
    }
    return memberPlayers;
  }

  public void tick() {
      if (!SOCIETY_ENABLED) {
          return;
      }
      if (!societyStarted) {
          return;
      }
    ticks++;
      if (ticks < 250) {
          return;
      }
    if (ticks % INITIATE_MESSAGE_DELAYS == 0) {
      for (SocietyMember member : members) {
          if (member.initialized) {
              continue;
          }
        ServerPlayerEntity player = member.getPlayer();
          if (player == null) {
              continue;
          }
        player.sendMessage(Text.of("§7When you are alone, type \"/initiate\""));
      }
    }
  }

  public void initializeMember(ServerPlayerEntity player) {
      if (!SOCIETY_ENABLED) {
          return;
      }
    SocietyMember member = getMember(player);
      if (member == null) {
          return;
      }
      if (member.initialized) {
          return;
      }
    member.initialized = true;
    afterInitialize(player);
    SessionTranscript.societyMemberInitialized(player);
  }

  public void afterInitialize(ServerPlayerEntity player) {
    PlayerUtils.playSoundToPlayer(player, SoundEvent.of(Identifier.of("secretlife_task")), 1, 1);

    int currentTime = 20;
    TaskScheduler.scheduleTask(currentTime, () -> {
      player.sendMessage(Text.of("§7You have been chosen to be part of the §csecret society§7."),
          false);
    });
    currentTime += 50;
    TaskScheduler.scheduleTask(currentTime, () -> {
      player.sendMessage(Text.of("§7There are §c2§7 other members. Find them."), false);
    });
    currentTime += 80;
    TaskScheduler.scheduleTask(currentTime, () -> {
      player.sendMessage(
          Text.of("§7Together, secretly kill §c2§7 other players by §cnon-pvp§7 means."), false);
    });
    currentTime += 100;
    TaskScheduler.scheduleTask(currentTime, () -> {
      player.sendMessage(Text.of("§7Find the other members with the secret word:"), false);
    });
    currentTime += 80;
    TaskScheduler.scheduleTask(currentTime, () -> {
      player.sendMessage(Text.of("§d\"" + secretWord + "\""), false);
    });
    currentTime += 80;
    TaskScheduler.scheduleTask(currentTime, () -> {
      player.sendMessage(Text.of("§7Type \"/society success\" when you complete your goal."),
          false);
    });
    currentTime += 80;
    TaskScheduler.scheduleTask(currentTime, () -> {
      player.sendMessage(Text.of("§7Don't tell anyone else about the society."), false);
    });
    currentTime += 70;
    TaskScheduler.scheduleTask(currentTime, () -> {
      player.sendMessage(Text.of("§7If you fail..."), false);
    });
    currentTime += 70;
    TaskScheduler.scheduleTask(currentTime, () -> {
      player.sendMessage(Text.of("§7Type \"/society fail\", and you all lose §c2 lives§7."), false);
    });
  }

  public void removeMember(ServerPlayerEntity player) {
    members.removeIf(member -> member.uuid == player.getUuid());
  }

  public void addMember(ServerPlayerEntity player) {
      if (!SOCIETY_ENABLED) {
          return;
      }
    members.add(new SocietyMember(player));
  }

  public void addMemberManually(ServerPlayerEntity player) {
      if (!SOCIETY_ENABLED) {
          return;
      }
    player.sendMessage(Text.of("§c [NOTICE] You are now a Secret Society member!"));
    addMember(player);
  }

  public void removeMemberManually(ServerPlayerEntity player) {
      if (!SOCIETY_ENABLED) {
          return;
      }
    player.sendMessage(Text.of("§c [NOTICE] You are no longer a Secret Society member!"));
    removeMember(player);
  }

  public void resetMembers() {
    for (SocietyMember member : members) {
      ServerPlayerEntity player = member.getPlayer();
        if (player == null) {
            continue;
        }
      player.sendMessage(Text.of("§c [NOTICE] You are no longer a Secret Society member!"));
    }
    members.clear();
  }

  public void onDisabledSociety() {
    endSociety();
  }

  public void endSuccess() {
    societyStarted = false;
    SessionTranscript.societyEnded();
    //TODO
  }

  public void endFail() {
    societyStarted = false;
    SessionTranscript.societyEnded();
    //TODO
  }
}
