package org.truexakep.vanillaChat.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.truexakep.vanillaChat.VanillaChat;

import java.util.List;

public class ConfigManager {
    private final VanillaChat plugin;

    public ConfigManager(VanillaChat plugin) {
        this.plugin = plugin;
    }

    public double getLocalChatRange() {
        return plugin.getConfig().getDouble("local-chat", 100);
    }

    public String getGlobalChatPrefix() {
        return plugin.getConfig().getString("global-chat-prefix", "!");
    }

    public String getSpyChatPrefix() {
        return plugin.getConfig().getString("spy-chat-prefix", "&f");
    }

    public String getSymbol(boolean global) {
        return plugin.getConfig().getString(global ? "symbols.global" : "symbols.local", "&f");
    }

    public List<String> getTags() {
        return plugin.getConfig().getStringList("tags");
    }

    public String getString(String path) {
        return VanillaChat.getInstance().getConfig().getString(path);
    }
}