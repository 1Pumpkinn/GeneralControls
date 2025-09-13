package hs.generalControls;

import hs.generalControls.combat.CombatManager;
import hs.generalControls.commands.SetEconSpawnCommand;
import hs.generalControls.commands.SpawnCommand;
import hs.generalControls.listeners.CombatListener;
import hs.generalControls.listeners.RecipeListener;
import hs.generalControls.listeners.TNTMinecartListener;
import hs.generalControls.managers.DiscordPromotion;
import hs.generalControls.managers.SpawnManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public final class GeneralControls extends JavaPlugin {

    private static GeneralControls instance;
    private CombatManager combatManager;
    private SpawnManager spawnManager;
    private DiscordPromotion discordPromotion;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;

        // Initialize managers with config
        combatManager = new CombatManager(this);
        spawnManager = new SpawnManager(this);
        discordPromotion = new DiscordPromotion(this);

        // Register event listeners
        getServer().getPluginManager().registerEvents(new CombatListener(combatManager), this);
        getServer().getPluginManager().registerEvents(new TNTMinecartListener(), this);
        getServer().getPluginManager().registerEvents(new RecipeListener(), this);

        // Register commands
        getCommand("spawn").setExecutor(new SpawnCommand(spawnManager));
        getCommand("seteconspawn").setExecutor(new SetEconSpawnCommand(spawnManager));

        // Start tasks
        combatManager.startCleanupTask();




        getLogger().info("GeneralControls has been enabled!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic - save everything before shutdown
        getLogger().info("Shutting down GeneralControls...");

        if (spawnManager != null) {
            spawnManager.saveSpawnLocation();
            getLogger().info("Spawn location saved.");
        }

        if (discordPromotion != null) {
            discordPromotion.stopPromotionTask();
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

    public DiscordPromotion getDiscordPromotion() {
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