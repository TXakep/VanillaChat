package org.truexakep.vanillaChat.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.configuration.ConfigurationSection;
import org.truexakep.vanillaChat.VanillaChat;
import org.truexakep.vanillaChat.config.ConfigManager;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatFormatter {

    private static final Pattern MENTION_PATTERN = Pattern.compile("@(\\w+)");
    private static final Pattern HEX_PATTERN = Pattern.compile("#[A-Fa-f0-9]{6}");

    public static String formatMessage(ConfigManager config, Player player, String message, boolean global) {
        message = stripColorCodes(message);

        String symbol = config.getSymbol(global);
        String format = getFormatForPlayer(player);

        message = highlightTagsAndMentions(message, config.getTags(), config);

        format = format
                .replace("{symbol}", symbol)
                .replace("{username}", player.getName())
                .replace("{message}", message);

        return applyColorCodes(format);
    }

    public static String getFormatForPlayer(Player player) {
        ConfigurationSection chatSection = VanillaChat.getInstance().getConfig().getConfigurationSection("chat");

        if ((player.isOp() || player.hasPermission("vanillachat.admin")) && chatSection.contains("admin")) {
            return chatSection.getString("admin");
        }

        for (String key : chatSection.getKeys(false)) {
            if (player.hasPermission("vanillachat." + key)) {
                return chatSection.getString(key);
            }
        }

        return chatSection.getString("user");
    }

    public static String translateHexColors(String text) {
        Matcher matcher = HEX_PATTERN.matcher(text);
        StringBuffer buffer = new StringBuffer();

        while (matcher.find()) {
            String hexCode = matcher.group().substring(1);
            StringBuilder replacement = new StringBuilder("§x");
            for (char c : hexCode.toCharArray()) {
                replacement.append("§").append(c);
            }
            matcher.appendReplacement(buffer, replacement.toString());
        }

        matcher.appendTail(buffer);
        return buffer.toString();
    }

    private static String highlightTagsAndMentions(String message, List<String> tags, ConfigManager config) {
        for (String tag : tags) {
            if (message.contains(tag)) {
                message = message.replace(tag, ChatColor.YELLOW + tag + ChatColor.RESET);
            }
        }

        String mentionNotify = ChatColor.translateAlternateColorCodes('&', config.getString("messages.mention_notification"));

        Matcher matcher = MENTION_PATTERN.matcher(message);
        StringBuilder buffer = new StringBuilder();

        while (matcher.find()) {
            String mentionedName = matcher.group(1);
            Player mentionedPlayer = getOnlinePlayerIgnoreCase(mentionedName);

            if (mentionedPlayer != null) {
                String replacement = ChatColor.YELLOW + "@" + mentionedPlayer.getName() + ChatColor.RESET;

                mentionedPlayer.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                        new TextComponent(mentionNotify));

                matcher.appendReplacement(buffer, Matcher.quoteReplacement(replacement));
            } else {
                matcher.appendReplacement(buffer, matcher.group());
            }
        }
        matcher.appendTail(buffer);

        return buffer.toString();
    }

    private static Player getOnlinePlayerIgnoreCase(String name) {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer.getName().equalsIgnoreCase(name)) {
                return onlinePlayer;
            }
        }
        return null;
    }

    private static String stripColorCodes(String message) {
        return message.replaceAll("(?i)[&§][0-9A-FK-OR]", "");
    }

    private static String applyColorCodes(String message) {
        message = translateHexColors(message);
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}