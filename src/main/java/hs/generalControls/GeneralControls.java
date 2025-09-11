package hs.generalControls;

import hs.generalControls.combat.CombatManager;
import hs.generalControls.commands.SetEconSpawnCommand;
import hs.generalControls.commands.SpawnCommand;
import hs.generalControls.listeners.CombatListener;
import hs.generalControls.listeners.TNTMinecartListener;
import hs.generalControls.managers.DiscordPromotion;
import hs.generalControls.managers.SpawnManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class GeneralControls extends JavaPlugin {

    private static GeneralControls instance;
    private CombatManager combatManager;
    private SpawnManager spawnManager;
    private DiscordPromotion discordPromotion; // Added Discord promotion

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;

        // Initialize managers
        combatManager = new CombatManager(this);
        spawnManager = new SpawnManager(this);
        discordPromotion = new DiscordPromotion(this); // Initialize Discord promotion

        // Register event listeners
        getServer().getPluginManager().registerEvents(new CombatListener(combatManager), this);
        getServer().getPluginManager().registerEvents(new TNTMinecartListener(), this);

        // Register commands
        getCommand("spawn").setExecutor(new SpawnCommand(spawnManager));
        getCommand("seteconspawn").setExecutor(new SetEconSpawnCommand(spawnManager));

        // Start tasks
        combatManager.startCleanupTask();
        discordPromotion.startPromotionTask(); // Start Discord promotion task

        getLogger().info("GeneralControls has been enabled!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        if (spawnManager != null) {
            spawnManager.saveSpawnLocation();
        }

        if (discordPromotion != null) {
            discordPromotion.stopPromotionTask(); // Stop Discord promotion task
        }

        getLogger().info("GeneralControls has been disabled!");
    }

    public static GeneralControls getInstance() {
        return instance;
    }

    public CombatManager getCombatManager() {
        return combatManager;
    }

    public SpawnManager getSpawnManager() {
        return spawnManager;
    }

    public DiscordPromotion getDiscordPromotion() { // Added getter for Discord promotion
        return discordPromotion;
    }

    private void removeMaceRecipe() {
        getServer().getScheduler().runTaskLater(this, () -> {
            try {
                getServer().removeRecipe(org.bukkit.NamespacedKey.minecraft("mace"));
                getLogger().info("Mace recipe has been disabled!");
            } catch (Exception e) {
                getLogger().warning("Could not remove mace recipe: " + e.getMessage());
            }
        }, 1L); // Run after server fully loads
    }
}