package org.truexakep.vanillaChat.events;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.truexakep.vanillaChat.VanillaChat;
import org.truexakep.vanillaChat.config.ConfigManager;
import org.truexakep.vanillaChat.utils.ChatFormatter;
import static org.truexakep.vanillaChat.utils.ChatFormatter.translateHexColors;

public class PlayerChatHandler implements Listener {
    private final ConfigManager config;

    public PlayerChatHandler(VanillaChat plugin) {
        this.config = plugin.getConfigManager();
    }

    @org.bukkit.event.EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player sender = event.getPlayer();
        String message = event.getMessage();
        String prefix = config.getGlobalChatPrefix();
        boolean isGlobal = message.startsWith(prefix);

        String cleanMessage = isGlobal ? message.substring(1).trim() : message;
        String formatted = ChatFormatter.formatMessage(config, sender, cleanMessage, isGlobal);
        event.setCancelled(true);

        if (isGlobal) {
            Bukkit.getOnlinePlayers().forEach(p -> p.sendMessage(formatted));
            return;
        }

        double range = config.getLocalChatRange();
        String spyPrefix = ChatColor.translateAlternateColorCodes('&', config.getSpyChatPrefix()) + " ";
        String spyFormatted = ChatFormatter.translateHexColors(spyPrefix) + formatted;

        boolean isHeard = false;

        for (Player target : Bukkit.getOnlinePlayers()) {
            if (target.equals(sender)) continue;

            boolean sameWorld = target.getWorld().equals(sender.getWorld());
            boolean inRange = sameWorld && target.getLocation().distance(sender.getLocation()) <= range;

            if (inRange) {
                target.sendMessage(formatted);
                isHeard = true;
            } else if (target.hasPermission("vanillachat.spy")) {
                target.sendMessage(spyFormatted);
            }
        }

        sender.sendMessage(formatted);

        if (!isHeard) {
            String coloredNotify = ChatFormatter.translateHexColors(
                    ChatColor.translateAlternateColorCodes('&', config.getString("messages.nobody-heard-message"))
            );
            sender.sendMessage(coloredNotify);
        }
    }
}