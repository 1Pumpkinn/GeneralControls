package hs.generalControls.managers;

import hs.generalControls.GeneralControls;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class SpawnManager {

    private final GeneralControls plugin;
    private Location spawnLocation;

    public SpawnManager(GeneralControls plugin) {
        this.plugin = plugin;
        loadSpawnLocation();
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
            } else {
                plugin.getLogger().warning("Spawn world '" + worldName + "' not found!");
            }
        }
    }

    public void saveSpawnLocation() {
        FileConfiguration config = plugin.getConfig();

        if (spawnLocation != null) {
            config.set("spawn.world", spawnLocation.getWorld().getName());
            config.set("spawn.x", spawnLocation.getX());
            config.set("spawn.y", spawnLocation.getY());
            config.set("spawn.z", spawnLocation.getZ());
            config.set("spawn.yaw", spawnLocation.getYaw());
            config.set("spawn.pitch", spawnLocation.getPitch());
        } else {
            config.set("spawn", null);
        }

        plugin.saveConfig();
    }
}