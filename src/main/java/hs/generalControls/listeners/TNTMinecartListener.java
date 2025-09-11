package hs.generalControls.listeners;

import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.Dispenser;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockDispenseEvent;

public class TNTMinecartListener implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        // Check if the player is right-clicking with a TNT minecart
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
            if (event.getItem() != null && event.getItem().getType() == Material.TNT_MINECART) {
                // Cancel the event to prevent placing the TNT minecart
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockDispense(BlockDispenseEvent event) {
        // Check if a dispenser is trying to dispense a TNT minecart
        if (event.getItem().getType() == Material.TNT_MINECART) {
            BlockState blockState = event.getBlock().getState();

            if (blockState instanceof Dispenser) {
                event.setCancelled(true);
            }
        }
    }
}