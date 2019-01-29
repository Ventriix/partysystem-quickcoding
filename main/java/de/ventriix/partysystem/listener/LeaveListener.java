package de.ventriix.partysystem.listener;

import de.ventriix.partysystem.core.PartySystem;
import de.ventriix.partysystem.utils.PartyHandler;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

/**
 * (c) Ventriix 2019, All rights reserved.
 * Contact E-Mail: ventriix@variaty.eu
 **/
public class LeaveListener implements Listener {

    @EventHandler
    public void onLeave(PlayerDisconnectEvent event) {
        ProxiedPlayer player = event.getPlayer();

        if(PartySystem.getInstance().getPartyHandler().isInParty(player)) {
            PartyHandler.Party party = PartySystem.getInstance().getPartyHandler().getParty(player);
            PartySystem.getInstance().getPartyHandler().removePlayerFromParty(player, party);
        }
    }
}
