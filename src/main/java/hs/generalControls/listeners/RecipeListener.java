package hs.generalControls.listeners;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;

public class RecipeListener implements Listener {

    @EventHandler
    public void onPrepareItemCraft(PrepareItemCraftEvent event) {
        // Check if the result is a mace
        ItemStack result = event.getRecipe() != null ? event.getRecipe().getResult() : null;

        if (result != null && result.getType() == Material.MACE) {
            // Cancel the crafting by setting result to null
            event.getInventory().setResult(new ItemStack(Material.AIR));
        }
    }

    @EventHandler
    public void onCraftItem(CraftItemEvent event) {
        // Double-check to prevent any mace crafting, including in crafters
        ItemStack result = event.getRecipe().getResult();

        if (result.getType() == Material.MACE) {
            event.setCancelled(true);

            // Send message if it's a player trying to craft
            if (event.getWhoClicked() != null) {
                event.getWhoClicked().sendMessage("§c§l[CRAFTING] §cMace crafting has been disabled!");
            }
        }
    }
}