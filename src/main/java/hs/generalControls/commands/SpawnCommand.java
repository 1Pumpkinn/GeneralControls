package hs.generalControls.commands;

import hs.generalControls.managers.SpawnManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpawnCommand implements CommandExecutor {

    private final SpawnManager spawnManager;

    public SpawnCommand(SpawnManager spawnManager) {
        this.spawnManager = spawnManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check if sender is a player
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cThis command can only be used by players!");
            return true;
        }

        Player player = (Player) sender;

        // Check if spawn location is set
        if (!spawnManager.hasSpawnLocation()) {
            player.sendMessage("§cSpawn location has not been set yet!");
            return true;
        }

        // Teleport player to spawn
        if (spawnManager.teleportToSpawn(player)) {
            player.sendMessage("§a§l[SPAWN] §aTeleported to spawn!");
        } else {
            player.sendMessage("§c§l[SPAWN] §cFailed to teleport to spawn! Please contact an administrator.");
        }

        return true;
    }
}