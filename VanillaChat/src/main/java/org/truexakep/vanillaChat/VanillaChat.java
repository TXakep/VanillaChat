package org.truexakep.vanillaChat;

import org.truexakep.vanillaChat.config.ConfigManager;
import org.truexakep.vanillaChat.events.PlayerChatHandler;
import org.bukkit.plugin.java.JavaPlugin;

public class VanillaChat extends JavaPlugin {

    private static VanillaChat instance;
    private ConfigManager configManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        configManager = new ConfigManager(this);
        getServer().getPluginManager().registerEvents(new PlayerChatHandler(this), this);
        getLogger().info("VanillaChat enabled.");
    }

    public static VanillaChat getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }
}