package hs.generalControls.managers;

import hs.generalControls.GeneralControls;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class DiscordPromotion {

    private final GeneralControls plugin;
    private BukkitTask promotionTask;
    private final Random random;

    // Discord server invite link - change this to your actual invite
    private static final String DISCORD_INVITE = "https://discord.gg/yourserver";

    // List of promotional messages
    private final List<String> promotionMessages = Arrays.asList(
            ChatColor.LIGHT_PURPLE + "🎮 " + ChatColor.YELLOW + "Join our Discord community! " + ChatColor.AQUA + DISCORD_INVITE,
            ChatColor.GOLD + "💬 " + ChatColor.WHITE + "Chat with other players on Discord: " + ChatColor.GREEN + DISCORD_INVITE,
            ChatColor.BLUE + "📢 " + ChatColor.YELLOW + "Get server updates and announcements on Discord! " + ChatColor.LIGHT_PURPLE + DISCORD_INVITE,
            ChatColor.GREEN + "🌟 " + ChatColor.WHITE + "Connect with the community: " + ChatColor.AQUA + DISCORD_INVITE,
            ChatColor.RED + "🔥 " + ChatColor.YELLOW + "Discord server for events, giveaways and more! " + ChatColor.GOLD + DISCORD_INVITE
    );

    public DiscordPromotion(GeneralControls plugin) {
        this.plugin = plugin;
        this.random = new Random();
    }

    /**
     * Starts the Discord promotion task
     */
    public void startPromotionTask() {
        if (promotionTask != null) {
            promotionTask.cancel();
        }

        promotionTask = new BukkitRunnable() {
            @Override
            public void run() {
                sendPromotionMessage();
            }
        }.runTaskTimer(plugin, 20L * 60 * 15, 20L * 60 * 15); // 15 minutes in ticks

        plugin.getLogger().info("Discord promotion task started! Messages will be sent every 15 minutes.");
    }

    /**
     * Stops the Discord promotion task
     */
    public void stopPromotionTask() {
        if (promotionTask != null) {
            promotionTask.cancel();
            promotionTask = null;
            plugin.getLogger().info("Discord promotion task stopped.");
        }
    }

    /**
     * Sends a random promotional message to all online players
     */
    private void sendPromotionMessage() {
        // Only send if there are players online
        if (Bukkit.getOnlinePlayers().isEmpty()) {
            return;
        }

        String message = getRandomPromotionMessage();

        // Send to all online players
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage("");
            player.sendMessage(ChatColor.STRIKETHROUGH + "                                                    ");
            player.sendMessage(message);
            player.sendMessage(ChatColor.STRIKETHROUGH + "                                                    ");
            player.sendMessage("");
        }

        // Log to console
        plugin.getLogger().info("Discord promotion message sent to " + Bukkit.getOnlinePlayers().size() + " players.");
    }

    /**
     * Gets a random promotion message from the list
     */
    private String getRandomPromotionMessage() {
        return promotionMessages.get(random.nextInt(promotionMessages.size()));
    }

    /**
     * Manually sends a promotion message (for testing or commands)
     */
    public void sendManualPromotion() {
        sendPromotionMessage();
    }

    /**
     * Adds a new promotion message to the list
     */
    public void addPromotionMessage(String message) {
        promotionMessages.add(message);
    }

    /**
     * Gets all promotion messages
     */
    public List<String> getPromotionMessages() {
        return new ArrayList<>(promotionMessages);
    }

    /**
     * Sets the Discord invite link
     */
    public static String getDiscordInvite() {
        return DISCORD_INVITE;
    }
}