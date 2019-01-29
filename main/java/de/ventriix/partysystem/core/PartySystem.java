package de.ventriix.partysystem.core;

import de.ventriix.partysystem.commands.Party_CMD;
import de.ventriix.partysystem.listener.LeaveListener;
import de.ventriix.partysystem.listener.SwitchListener;
import de.ventriix.partysystem.utils.PartyHandler;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;

/**
 * (c) Ventriix 2019, All rights reserved.
 * Contact E-Mail: ventriix@variaty.eu
 **/
public class PartySystem extends Plugin {

    private static PartySystem instance;

    /* Variables */
    private Configuration configuration;
    private PartyHandler partyHandler;
    private static String prefix = "§f[§5Party§f] §7";

    @Override
    public void onEnable() {
        try {
            instance = this;
            /* Config */
            File configFolder = new File("plugins/PartySystem");
            if(!configFolder.exists()) configFolder.mkdir();

            File configFile = new File("plugins/PartySystem/configuration.yml");
            if (!configFile.exists()) configFile.createNewFile();
            configuration = YamlConfiguration.getProvider(YamlConfiguration.class).load(configFile);

            addDefault("Info", "Coming soon!");

            saveConfig();
            /* Other stuff */
            partyHandler = new PartyHandler();
            partyHandler.schedule();

            ProxyServer.getInstance().getPluginManager().registerCommand(this, new Party_CMD());
            ProxyServer.getInstance().getPluginManager().registerListener(this, new LeaveListener());
            ProxyServer.getInstance().getPluginManager().registerListener(this, new SwitchListener());

            ProxyServer.getInstance().getConsole().sendMessage(new TextComponent(prefix + "§fPlugin §aenabled§f, have fun!"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onDisable() {

    }

    public static PartySystem getInstance() {
        return instance;
    }

    public void saveConfig() {
        try {
            YamlConfiguration.getProvider(YamlConfiguration.class).save(configuration, new File("plugins/PartySystem/configuration.yml"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void addDefault(String key, Object value) {
        if(configuration.get(key) == null) configuration.set(key, value);
    }

    public PartyHandler getPartyHandler() {
        return partyHandler;
    }

    public static String getPrefix() {
        return prefix;
    }
}
