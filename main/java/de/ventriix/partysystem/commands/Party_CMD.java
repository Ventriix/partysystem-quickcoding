package de.ventriix.partysystem.commands;

import de.ventriix.partysystem.core.PartySystem;
import de.ventriix.partysystem.utils.PartyHandler;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

/**
 * (c) Ventriix 2019, All rights reserved.
 * Contact E-Mail: ventriix@variaty.eu
 **/
public class Party_CMD extends Command {

    public Party_CMD() {
        super("party");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(sender instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer) sender;

            if (args.length == 0) {
                player.sendMessage(new TextComponent(
                        PartySystem.getPrefix() +
                                "§6Party Hilfe\n" +
                                "§5/party invite <Spieler> §8» §7Lade einen Spieler in die Party ein\n" +
                                "§5/party deny <ID> §8» §7Lehne eine Einladung ab\n" +
                                "§5/party accept <ID> §8» §7Nimm eine Einladung an\n" +
                                "§5/party kick <Spieler> §8» §7Schmeiße ein Spieler aus der Party\n" +
                                "§5/party promote <Spieler> §8» §7Mache einen anderen Spieler zum §7Party-Leader\n" +
                                "§5/party create §8» §7Erstelle eine Party\n" +
                                "§5/party disband §8» §7Löse deine Party auf"));
            } else if(args[0].equalsIgnoreCase("invite")) {
                ProxiedPlayer targetPlayer = ProxyServer.getInstance().getPlayer(args[1]);

                if(targetPlayer != null) {
                    if(targetPlayer != player) {
                        if (!PartySystem.getInstance().getPartyHandler().isInParty(targetPlayer)) {
                            if (!PartySystem.getInstance().getPartyHandler().isInParty(player)) {
                                PartySystem.getInstance().getPartyHandler().createParty(player);
                                player.sendMessage(new TextComponent(PartySystem.getPrefix() + "Da du noch keine Party hattest, wurde eine für dich §7erstellt"));
                            }

                            PartyHandler.Party party = PartySystem.getInstance().getPartyHandler().getParty(player);

                            if (!party.getInvitations().contains(targetPlayer)) {
                                PartySystem.getInstance().getPartyHandler().sendInvite(player, targetPlayer, party);
                                player.sendMessage(new TextComponent(PartySystem.getPrefix() + "Du hast den Spieler {targetDisplayName} §7zu deiner Party §7eingeladen".replace("{targetDisplayName}", targetPlayer.getDisplayName())));
                            } else {
                                player.sendMessage(new TextComponent(PartySystem.getPrefix() + "§cDu hast diesen Spieler bereits eingeladen"));
                            }
                        } else {
                            player.sendMessage(new TextComponent(PartySystem.getPrefix() + "§cDieser Spieler ist bereits in einer anderen Party"));
                        }
                    } else {
                        player.sendMessage(new TextComponent(PartySystem.getPrefix() + "§cDu kannst dich nicht selber einladen."));
                    }
                } else {
                    player.sendMessage(new TextComponent(PartySystem.getPrefix() + "§cDieser Spieler ist nicht online"));
                }
            } else if(args[0].equalsIgnoreCase("deny")) {
                PartyHandler.Party party = PartySystem.getInstance().getPartyHandler().getPartyById(args[1]);

                if(party != null) {
                    if(!PartySystem.getInstance().getPartyHandler().isInParty(player)) {
                        if (party.getInvitations().contains(player)) {
                            party.getInvitations().remove(player);
                            player.sendMessage(new TextComponent(PartySystem.getPrefix() + "Du hast die Einladung abgelehnt"));
                        } else {
                            player.sendMessage(new TextComponent(PartySystem.getPrefix() + "Zu dieser Party wurdest du nicht eingeladen :/"));
                        }
                    } else {
                        player.sendMessage(new TextComponent(PartySystem.getPrefix() + "§cDu bist bereits in einer anderen Party"));
                    }
                } else {
                    player.sendMessage(new TextComponent(PartySystem.getPrefix() + "§cDiese Party existiert nicht"));
                }
            } else if(args[0].equalsIgnoreCase("accept")) {
                PartyHandler.Party party = PartySystem.getInstance().getPartyHandler().getPartyById(args[1]);

                if(party != null) {
                    if(!PartySystem.getInstance().getPartyHandler().isInParty(player)) {
                        if (party.getInvitations().contains(player)) {
                            PartySystem.getInstance().getPartyHandler().addPlayerToParty(player, party);
                            player.sendMessage(new TextComponent(PartySystem.getPrefix() + "Du hast die Einladung angenommen"));
                        } else {
                            player.sendMessage(new TextComponent(PartySystem.getPrefix() + "Zu dieser Party wurdest du nicht eingeladen :/"));
                        }
                    } else {
                        player.sendMessage(new TextComponent(PartySystem.getPrefix() + "§cDu bist bereits in einer anderen Party"));
                    }
                } else {
                    player.sendMessage(new TextComponent(PartySystem.getPrefix() + "§cDiese Party existiert nicht"));
                }
            } else if(args[0].equalsIgnoreCase("kick")) {
                PartyHandler.Party party = PartySystem.getInstance().getPartyHandler().getParty(player);

                if(party != null) {
                    ProxiedPlayer targetPlayer = ProxyServer.getInstance().getPlayer(args[1]);

                    if(targetPlayer != null) {
                        if (party.getMembers().contains(targetPlayer)) {
                            if(party.getOwner().equals(player)) {
                                PartySystem.getInstance().getPartyHandler().removePlayerFromParty(player, party);
                                player.sendMessage(new TextComponent(PartySystem.getPrefix() + "Du hast den Spieler {targetDisplayName} aus der Party geschmissen"));
                            } else {
                                player.sendMessage(new TextComponent(PartySystem.getPrefix() + "§cDu bist nicht der Party-Leader"));
                            }
                        } else {
                            player.sendMessage(new TextComponent(PartySystem.getPrefix() + "§cDieser Spieler ist nicht in deiner Party"));
                        }
                    } else {
                        player.sendMessage(new TextComponent(PartySystem.getPrefix() + "§cDieser Spieler ist nicht online"));
                    }
                } else {
                    player.sendMessage(new TextComponent(PartySystem.getPrefix() + "§cDu bist in keiner Party"));
                }
            } else if(args[0].equalsIgnoreCase("promote")) {
                PartyHandler.Party party = PartySystem.getInstance().getPartyHandler().getParty(player);

                if(party != null) {
                    ProxiedPlayer targetPlayer = ProxyServer.getInstance().getPlayer(args[1]);

                    if(targetPlayer != null) {
                        if (party.getMembers().contains(targetPlayer)) {
                            if(party.getOwner().equals(player)) {
                                PartySystem.getInstance().getPartyHandler().promotePlayer(targetPlayer, party);
                                player.sendMessage(new TextComponent(PartySystem.getPrefix() + "Du hast den Spieler {targetDisplayName} erfolgreich \n§7zum Party-Leader befördert.".replace("{targetDisplayName}", targetPlayer.getDisplayName())));
                            } else {
                                player.sendMessage(new TextComponent(PartySystem.getPrefix() + "§cDu bist nicht der Party-Leader"));
                            }
                        } else {
                            player.sendMessage(new TextComponent(PartySystem.getPrefix() + "§cDieser Spieler ist nicht in deiner Party"));
                        }
                    } else {
                        player.sendMessage(new TextComponent(PartySystem.getPrefix() + "§cDieser Spieler ist nicht online"));
                    }
                } else {
                    player.sendMessage(new TextComponent(PartySystem.getPrefix() + "§cDu bist in keiner Party"));
                }
            } else if(args[0].equalsIgnoreCase("create")) {
                if(!PartySystem.getInstance().getPartyHandler().isInParty(player)) {
                    PartyHandler.Party party = PartySystem.getInstance().getPartyHandler().createParty(player);
                    player.sendMessage(new TextComponent(PartySystem.getPrefix() + "Du hast eine Party erstellt\n" +
                            "§7ID§8: §5{partyId}".replace("{partyId}", party.getId())));
                } else {
                    player.sendMessage(new TextComponent(PartySystem.getPrefix() + "§cDu bist bereits in einer Party"));
                }
            } else if(args[0].equalsIgnoreCase("disband")) {
                PartyHandler.Party party = PartySystem.getInstance().getPartyHandler().getParty(player);

                if(party != null) {
                    if(party.getOwner().equals(player)) {
                        party.broadcastMessage("§cDie Party wurde aufgelöst");
                        player.sendMessage(new TextComponent(PartySystem.getPrefix() + "Du hast deine Party erfolgreich aufgelöst"));
                        PartySystem.getInstance().getPartyHandler().deleteParty(party);
                    }
                } else {
                    player.sendMessage(new TextComponent(PartySystem.getPrefix() + "§cDu bist in keiner Party"));
                }
            }
        } else {
            sender.sendMessage(new TextComponent(PartySystem.getPrefix() + "§fFür diese Aktion §cmusst §fdu ein Spieler sein!"));
        }
    }
}
