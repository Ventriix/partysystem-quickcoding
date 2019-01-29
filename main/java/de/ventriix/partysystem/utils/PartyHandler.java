package de.ventriix.partysystem.utils;

import de.ventriix.partysystem.core.PartySystem;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * (c) Ventriix 2019, All rights reserved.
 * Contact E-Mail: ventriix@variaty.eu
 **/
public class PartyHandler {

    private List<Party> allPartys = new ArrayList<>();

    public class Party {
        private String id;
        private ProxiedPlayer owner;
        private List<ProxiedPlayer> members, invitations;

        public Party(String id, ProxiedPlayer owner) {
            this.id = id;
            this.owner = owner;
            this.members = new ArrayList<>();
            this.members.add(owner);
            this.invitations = new ArrayList<>();
        }

        public String getId() {
            return id;
        }

        public List<ProxiedPlayer> getInvitations() {
            return invitations;
        }

        public void addInvitation(ProxiedPlayer player) {
            this.invitations.add(player);
        }

        public void removeInvitation(ProxiedPlayer player) {
            this.invitations.remove(player);
        }

        public List<ProxiedPlayer> getMembers() {
            return members;
        }

        public void addMember(ProxiedPlayer player) {
            this.members.add(player);
        }

        public void removeMember(ProxiedPlayer player) {
            this.members.remove(player);
        }

        public ProxiedPlayer getOwner() {
            return owner;
        }

        public void setOwner(ProxiedPlayer owner) {
            this.owner = owner;
        }

        public void broadcastMessage(String msg) {
            members.forEach(player -> {
                player.sendMessage(new TextComponent(PartySystem.getPrefix() + msg));
            });
        }

        public void sendPartyMessage(ProxiedPlayer player, String msg) {
            members.forEach(member -> {
                member.sendMessage(new TextComponent(PartySystem.getPrefix() + "{playerDisplayName}§8: §e{message}"
                        .replace("{playerDisplayName}", player.getDisplayName())
                        .replace("{message}", msg)));
            });
        }
    }

    public void schedule() {
        ProxyServer.getInstance().getScheduler().schedule(PartySystem.getInstance(), () -> {
            try {
                if (!allPartys.isEmpty()) {
                    for (Party party : allPartys) {
                        int currentMembers = party.getMembers().size();

                        if (currentMembers <= 1) {
                            party.broadcastMessage("§cDie Party wurde aufgelöst, da zu wenige §cSpieler drin §cwaren");
                            deleteParty(party);
                        }
                    }
                }
            } catch (Exception ignored) {}
        }, 0, 30, TimeUnit.SECONDS);
    }

    public Party createParty(ProxiedPlayer owner) {
        Party party = new Party(UUID.randomUUID().toString().substring(0, 8).replace("-", "").toUpperCase(), owner);
        allPartys.add(party);
        return party;
    }

    public boolean isInParty(ProxiedPlayer player) {
        return getParty(player) != null;
    }

    public void deleteParty(Party party) {
        allPartys.remove(party);
    }

    public void promotePlayer(ProxiedPlayer player, Party party) {
        party.setOwner(player);
        party.broadcastMessage("Der Spieler {targetDisplayName} §7ist nun der Party-Leader".replace("{targetDisplayName}", player.getDisplayName()));
        party.getOwner().sendMessage(new TextComponent(PartySystem.getPrefix() + "Du bist nun der Party-Leader"));
    }

    public Party getParty(ProxiedPlayer player) {
        for(Party party : allPartys) {
            if(party.getMembers().contains(player)) {
                return party;
            }
        }

        return null;
    }

    public Party getPartyById(String id) {
        for(Party party : allPartys) {
            if(party.getId().equals(id)) {
                return party;
            }
        }

        return null;
    }

    public void sendInvite(ProxiedPlayer sender, ProxiedPlayer player, Party party) {
        TextComponent accept = new TextComponent();
        accept.setText("§a§l[ANNEHMEN]");
        accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party accept {partyId}".replace("{partyId}", party.getId())));
        accept.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§aNehme die Anfrage an").bold(true).create()));

        TextComponent deny = new TextComponent();
        deny.setText("§c§l[ABLEHNEN]");
        deny.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party deny {partyId}".replace("{partyId}", party.getId())));
        deny.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§aLehne die Anfrage ab").bold(true).create()));

        TextComponent send = new TextComponent();
        send.addExtra(accept);
        send.addExtra(" §7oder ");
        send.addExtra(deny);

        player.sendMessage(new TextComponent(PartySystem.getPrefix() + "{senderDisplayName} §7hat dich zu einer Party eingeladen".replace("{senderDisplayName}", sender.getDisplayName())));
        player.sendMessage(new TextComponent("§7ID§8: §5{partyId}".replace("{partyId}", party.getId())));
        player.sendMessage(send);
        party.addInvitation(player);

        ProxyServer.getInstance().getScheduler().schedule(PartySystem.getInstance(), () -> {
            Party targetParty = getPartyById(party.getId());

            if(targetParty != null) {
                if (targetParty.getInvitations().contains(player)) {
                    party.removeInvitation(player);
                }
            }
        }, 30, 30, TimeUnit.SECONDS);
    }

    public void addPlayerToParty(ProxiedPlayer player, Party party) {
        party.removeInvitation(player);
        party.addMember(player);
        party.broadcastMessage("{playerDisplayName} §7ist der Party beigetreten"
                .replace("{playerDisplayName}", player.getDisplayName()));
    }

    public void removePlayerFromParty(ProxiedPlayer player, Party party) {
        party.removeMember(player);
        party.broadcastMessage("{playerDisplayName} §7hat die Party verlassen"
                .replace("{playerDisplayName}", player.getDisplayName()));
    }
}
