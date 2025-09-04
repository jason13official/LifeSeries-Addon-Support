package net.mat0u5.lifeseries.seasons.secretsociety;

import net.mat0u5.lifeseries.seasons.session.SessionAction;
import net.mat0u5.lifeseries.seasons.session.SessionTranscript;
import net.mat0u5.lifeseries.utils.other.OtherUtils;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.core.jmx.Server;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static net.mat0u5.lifeseries.Main.livesManager;
import static net.mat0u5.lifeseries.Main.server;

public class SecretSociety {
    public boolean SOCIETY_ENABLED = false;
    public double START_TIME = 5.0;
    public int MEMBER_COUNT = 3;
    public List<String> FORCE_MEMBERS = new ArrayList<>();
    public List<String> IGNORE_MEMBERS = new ArrayList<>();
    public List<String> POSSIBLE_WORDS = List.of("Duck");//TODO add more

    public List<SocietyMember> members = new ArrayList<>();
    public boolean societyStarted = false;
    public long ticks = 0;
    public String secretWord = "";
    public Random rnd = new Random();

    public void onReload() {
        //SOCIETY_ENABLED =
        //START_TIME =
        if (!SOCIETY_ENABLED) {
            onDisabledSociety();
        }
    }

    public SessionAction getAction() {
        return new SessionAction(OtherUtils.minutesToTicks(START_TIME), TextUtils.formatString("§7Begin Secret Society §f[{}]", OtherUtils.formatTime(OtherUtils.minutesToTicks(START_TIME))), "Begin Secret Society") {
            @Override
            public void trigger() {
                if (!SOCIETY_ENABLED) return;
                startSociety();
            }
        };
    }

    public void startSociety() {
        startSociety();
    }
    public void startSociety(String word) {
        if (!SOCIETY_ENABLED) return;
        if (server == null) return;
        if (word == null && !POSSIBLE_WORDS.isEmpty()) {
            word = POSSIBLE_WORDS.get(rnd.nextInt(POSSIBLE_WORDS.size()));
        }
        if (word != null) {
            this.secretWord = word;
        }

        societyStarted = true;
        resetMembers();
        chooseMembers(PlayerUtils.getAllFunctioningPlayers(), ChooseReason.NORMAL);
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

    public void chooseMembers(List<ServerPlayerEntity> allowedPlayers, ChooseReason chooseReason) {
        if (!SOCIETY_ENABLED) return;
        Collections.shuffle(allowedPlayers);
        List<ServerPlayerEntity> memberPlayers = new ArrayList<>();
        List<ServerPlayerEntity> nonMemberPlayers = new ArrayList<>();

        for (ServerPlayerEntity player : allowedPlayers) {
            if (IGNORE_MEMBERS.contains(player.getNameForScoreboard().toLowerCase())) continue;
            if (FORCE_MEMBERS.contains(player.getNameForScoreboard().toLowerCase())) {
                memberPlayers.add(player);
            }
        }

        int remainingMembers = MEMBER_COUNT;
        for (ServerPlayerEntity player : allowedPlayers) {
            if (remainingMembers <= 0) break;
            if (IGNORE_MEMBERS.contains(player.getNameForScoreboard().toLowerCase())) continue;
            if (FORCE_MEMBERS.contains(player.getNameForScoreboard().toLowerCase())) continue;
            if (memberPlayers.contains(player)) continue;
            memberPlayers.add(player);
            remainingMembers--;
        }

        for (ServerPlayerEntity player : allowedPlayers) {
            if (memberPlayers.contains(player)) continue;
            nonMemberPlayers.add(player);
        }


        PlayerUtils.playSoundToPlayers(nonMemberPlayers, SoundEvent.of(Identifier.of("minecraft","secretlife_task")));
        PlayerUtils.playSoundToPlayers(memberPlayers, SoundEvent.of(Identifier.of("minecraft","secretlife_task")));

        memberPlayers.forEach(this::addMember);

        SessionTranscript.membersChosen(memberPlayers);
    }

    public void tick() {
        if (!SOCIETY_ENABLED) return;
        ticks++;
        if (ticks % 200 == 0) {//TODO ticks num
            for (SocietyMember member : members) {
                if (member.initialized) continue;
                ServerPlayerEntity player = member.getPlayer();
                if (player == null) continue;
                player.sendMessage(Text.of("When you are alone, type \"/initialize\""));//TODO exact text
            }
        }
    }

    public void initializeMember(ServerPlayerEntity player) {
        if (!SOCIETY_ENABLED) return;
        SocietyMember member = getMember(player);
        if (member == null) return;
        if (member.initialized) {
            player.sendMessage(Text.of("§cYou have already been initialized"));//TODO formatting
            player.sendMessage(TextUtils.formatLoosely("The secret word is: \"{}\"", secretWord));//TODO formatting
            return;
        }
        member.initialized = true;
        afterInitialize(player);
    }

    public void afterInitialize(ServerPlayerEntity player) {
        //TODO send message
    }

    public void removeMember(ServerPlayerEntity player) {
        members.removeIf(member -> member.uuid == player.getUuid());
    }

    public void addMember(ServerPlayerEntity player) {
        if (!SOCIETY_ENABLED) return;
        members.add(new SocietyMember(player));
    }

    public void addMemberManually(ServerPlayerEntity player) {
        if (!SOCIETY_ENABLED) return;
        player.sendMessage(Text.of("§c [NOTICE] You are now a Secret Society member!"));
        addMember(player);
    }

    public void removeMemberManually(ServerPlayerEntity player) {
        if (!SOCIETY_ENABLED) return;
        player.sendMessage(Text.of("§c [NOTICE] You are no longer a Secret Society member!"));
        removeMember(player);
    }

    public void resetMembers() {
        for (SocietyMember member : members) {
            ServerPlayerEntity player = member.getPlayer();
            if (player == null) continue;
            player.sendMessage(Text.of("§c [NOTICE] You are no longer a Secret Society member!"));
        }
        members.clear();
    }

    public void onDisabledSociety() {
        resetMembers();
    }

    public enum ChooseReason {
        NORMAL,
        COMMAND;
    }
}
