package de.ventriix.partysystem.listener;

import de.ventriix.partysystem.core.PartySystem;
import de.ventriix.partysystem.utils.PartyHandler;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.concurrent.TimeUnit;

/**
 * (c) Ventriix 2019, All rights reserved.
 * Contact E-Mail: ventriix@variaty.eu
 **/
public class SwitchListener implements Listener {

    @EventHandler
    public void onSwitch(ServerSwitchEvent event) {
        ProxiedPlayer player = event.getPlayer();

        if(PartySystem.getInstance().getPartyHandler().isInParty(player)) {
            PartyHandler.Party party = PartySystem.getInstance().getPartyHandler().getParty(player);

            if(party.getOwner().equals(player)) {
                String name = event.getPlayer().getServer().getInfo().getName();

                party.broadcastMessage("§7Die Party betritt den folgenden Server§8: §e{serverName}".replace("{serverName}", name));
                party.getMembers().forEach(all -> {
                    if(!all.equals(player)) {
                        ProxyServer.getInstance().getScheduler().schedule(PartySystem.getInstance(), () -> {
                            all.connect(player.getServer().getInfo());
                        }, 687, TimeUnit.MILLISECONDS);
                    }
                });
            }
        }
    }
}
