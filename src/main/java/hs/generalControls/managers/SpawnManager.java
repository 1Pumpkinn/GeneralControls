package hs.generalControls.managers;

import hs.generalControls.GeneralControls;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class SpawnManager {

    private final GeneralControls plugin;
    private Location spawnLocation;

    public SpawnManager(GeneralControls plugin) {
        this.plugin = plugin;
        loadSpawnLocation();

        // Auto-save spawn location every 5 minutes
        startAutoSaveTask();
    }

    public void setSpawnLocation(Location location) {
        this.spawnLocation = location.clone();
        saveSpawnLocation();
    }

    public Location getSpawnLocation() {
        return spawnLocation != null ? spawnLocation.clone() : null;
    }

    public boolean hasSpawnLocation() {
        return spawnLocation != null;
    }

    public boolean teleportToSpawn(Player player) {
        if (spawnLocation == null) {
            return false;
        }

        // Ensure the world is loaded
        World world = spawnLocation.getWorld();
        if (world == null) {
            return false;
        }

        return player.teleport(spawnLocation);
    }

    public void loadSpawnLocation() {
        FileConfiguration config = plugin.getConfig();

        if (config.contains("spawn.world") &&
                config.contains("spawn.x") &&
                config.contains("spawn.y") &&
                config.contains("spawn.z")) {

            String worldName = config.getString("spawn.world");
            World world = plugin.getServer().getWorld(worldName);

            if (world != null) {
                double x = config.getDouble("spawn.x");
                double y = config.getDouble("spawn.y");
                double z = config.getDouble("spawn.z");
                float yaw = (float) config.getDouble("spawn.yaw", 0.0);
                float pitch = (float) config.getDouble("spawn.pitch", 0.0);

                spawnLocation = new Location(world, x, y, z, yaw, pitch);
                plugin.getLogger().info("Spawn location loaded: " + worldName + " at " + x + ", " + y + ", " + z);
            } else {
                plugin.getLogger().warning("Spawn world '" + worldName + "' not found!");
            }
        } else {
            plugin.getLogger().info("No spawn location set yet.");
        }
    }

    public void saveSpawnLocation() {
        try {
            FileConfiguration config = plugin.getConfig();

            if (spawnLocation != null) {
                config.set("spawn.world", spawnLocation.getWorld().getName());
                config.set("spawn.x", spawnLocation.getX());
                config.set("spawn.y", spawnLocation.getY());
                config.set("spawn.z", spawnLocation.getZ());
                config.set("spawn.yaw", spawnLocation.getYaw());
                config.set("spawn.pitch", spawnLocation.getPitch());

                plugin.saveConfig();
                plugin.getLogger().info("Spawn location saved successfully.");
            } else {
                config.set("spawn", null);
                plugin.saveConfig();
            }
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to save spawn location: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void startAutoSaveTask() {
        // Auto-save every 5 minutes (6000 ticks)
        new BukkitRunnable() {
            @Override
            public void run() {
                if (spawnLocation != null) {
                    saveSpawnLocation();
                }
            }
        }.runTaskTimer(plugin, 6000L, 6000L);
    }
}