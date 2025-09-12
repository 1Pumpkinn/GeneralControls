package hs.generalControls.commands;

import hs.generalControls.managers.SpawnManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetEconSpawnCommand implements CommandExecutor {

    private final SpawnManager spawnManager;

    public SetEconSpawnCommand(SpawnManager spawnManager) {
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

        // Check if player is an operator (OP)
        if (!player.isOp()) {
            player.sendMessage("§c§l[SPAWN] §cYou must be an operator to use this command!");
            return true;
        }

        // Set spawn location to player's current location
        spawnManager.setSpawnLocation(player.getLocation());

        String locationString = String.format("§7(§e%.1f§7, §e%.1f§7, §e%.1f§7) in §e%s",
                player.getLocation().getX(),
                player.getLocation().getY(),
                player.getLocation().getZ(),
                player.getWorld().getName());

        player.sendMessage("§a§l[SPAWN] §aSpawn location set to " + locationString + "§a!");

        return true;
    }
}