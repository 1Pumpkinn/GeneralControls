package hs.generalControls.config;

import hs.generalControls.GeneralControls;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class GeneralConfig {

    private final GeneralControls plugin;
    private FileConfiguration config;

    public GeneralConfig(GeneralControls plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    public void loadConfig() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        config = plugin.getConfig();
    }

    public void saveConfig() {
        plugin.saveConfig();
    }

    // Combat Settings
    public int getCombatDuration() {
        return config.getInt("combat.duration", 15);
    }

    public String getCombatStartMessage() {
        return ChatColor.translateAlternateColorCodes('&',
                config.getString("combat.messages.start", "&c&l[COMBAT] &cYou are now in combat for %duration% seconds!"));
    }

    public String getCombatEndMessage() {
        return ChatColor.translateAlternateColorCodes('&',
                config.getString("combat.messages.end", "&a&l[COMBAT] &aYou are no longer in combat!"));
    }

    public String getCombatDeathMessage() {
        return ChatColor.translateAlternateColorCodes('&',
                config.getString("combat.messages.death", "&a&l[COMBAT] &aCombat ended due to death."));
    }

    public String getCombatKillMessage() {
        return ChatColor.translateAlternateColorCodes('&',
                config.getString("combat.messages.kill", "&a&l[COMBAT] &aCombat ended - opponent eliminated."));
    }

    public String getCombatElytraMessage() {
        return ChatColor.translateAlternateColorCodes('&',
                config.getString("combat.messages.elytra", "&c&l[COMBAT] &cYou cannot use elytra while in combat!"));
    }

    public String getCombatCommandMessage() {
        return ChatColor.translateAlternateColorCodes('&',
                config.getString("combat.messages.command", "&c&l[COMBAT] &cYou cannot use commands while in combat! &c(%seconds%s remaining)"));
    }

    public List<String> getAllowedCommands() {
        return config.getStringList("combat.allowed-commands");
    }

    // Discord Promotion Settings
    public boolean isDiscordPromotionEnabled() {
        return config.getBoolean("discord.promotion.enabled", true);
    }

    public int getDiscordPromotionInterval() {
        return config.getInt("discord.promotion.interval-minutes", 15);
    }

    public String getDiscordInvite() {
        return config.getString("discord.invite", "https://discord.gg/yourserver");
    }

    public List<String> getDiscordPromotionMessages() {
        return config.getStringList("discord.promotion.messages");
    }

    // Spawn Settings
    public String getSpawnSetMessage() {
        return ChatColor.translateAlternateColorCodes('&',
                config.getString("spawn.messages.set", "&a&l[SPAWN] &aSpawn location set to %location%&a!"));
    }

    public String getSpawnTeleportMessage() {
        return ChatColor.translateAlternateColorCodes('&',
                config.getString("spawn.messages.teleport", "&a&l[SPAWN] &aTeleported to spawn!"));
    }

    public String getSpawnNotSetMessage() {
        return ChatColor.translateAlternateColorCodes('&',
                config.getString("spawn.messages.not-set", "&cSpawn location has not been set yet!"));
    }

    public String getSpawnFailedMessage() {
        return ChatColor.translateAlternateColorCodes('&',
                config.getString("spawn.messages.failed", "&c&l[SPAWN] &cFailed to teleport to spawn! Please contact an administrator."));
    }

    public String getSpawnNoPermissionMessage() {
        return ChatColor.translateAlternateColorCodes('&',
                config.getString("spawn.messages.no-permission", "&c&l[SPAWN] &cYou must be an operator to use this command!"));
    }

    // TNT Minecart Settings
    public boolean isTntMinecartBlocked() {
        return config.getBoolean("tnt-minecart.blocked", true);
    }

    public String getTntMinecartMessage() {
        return ChatColor.translateAlternateColorCodes('&',
                config.getString("tnt-minecart.message", "&c&l[BLOCKED] &cTNT Minecarts are disabled on this server!"));
    }

    // Recipe Settings
    public boolean isMaceRecipeDisabled() {
        return config.getBoolean("recipes.mace.disabled", true);
    }

    public String getMaceCraftingMessage() {
        return ChatColor.translateAlternateColorCodes('&',
                config.getString("recipes.mace.message", "&c&l[CRAFTING] &cMace crafting has been disabled!"));
    }

    // General Messages
    public String getPlayerOnlyMessage() {
        return ChatColor.translateAlternateColorCodes('&',
                config.getString("messages.player-only", "&cThis command can only be used by players!"));
    }

    public String getNoPermissionMessage() {
        return ChatColor.translateAlternateColorCodes('&',
                config.getString("messages.no-permission", "&cYou don't have permission to use this command!"));
    }
}