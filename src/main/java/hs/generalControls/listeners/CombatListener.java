package hs.generalControls.listeners;

import hs.generalControls.combat.CombatManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class CombatListener implements Listener {

    private final CombatManager combatManager;

    // Commands that are allowed during combat
    private final List<String> ALLOWED_COMMANDS = Arrays.asList(
            "/msg", "/tell", "/w", "/whisper", "/reply", "/r",
            "/help", "/rules", "/discord", "/website", "/vote"
    );

    public CombatListener(CombatManager combatManager) {
        this.combatManager = combatManager;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();

        // Remove combat tag from the victim (who died)
        if (combatManager.isInCombat(victim)) {
            combatManager.removeCombatTag(victim);
            victim.sendMessage("§a§l[COMBAT] §aCombat ended due to death.");
        }

        // Remove combat tag from the killer (who killed the victim)
        if (killer != null && combatManager.isInCombat(killer)) {
            combatManager.removeCombatTag(killer);
            killer.sendMessage("§a§l[COMBAT] §aCombat ended - opponent eliminated.");
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) return;

        Player attacker = null;
        Player victim = null;

        // Get attacker - ONLY if it's a player
        if (event.getDamager() instanceof Player) {
            attacker = (Player) event.getDamager();
        }

        // Get victim - ONLY if it's a player
        if (event.getEntity() instanceof Player) {
            victim = (Player) event.getEntity();
        }

        // Only tag players if BOTH are players (PvP only)
        if (attacker != null && victim != null) {
            combatManager.tagPlayer(attacker);
            combatManager.tagPlayer(victim);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerToggleFlight(PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();

        // Check if player is trying to start flying/gliding
        if (!event.isFlying()) return;

        // Check if player is in combat
        if (!combatManager.isInCombat(player)) return;

        // Check if player has elytra equipped
        ItemStack chestplate = player.getInventory().getChestplate();
        if (chestplate != null && chestplate.getType() == Material.ELYTRA) {
            event.setCancelled(true);
            player.sendMessage("§c§l[COMBAT] §cYou cannot use elytra while in combat!");

            // Also stop any existing gliding
            if (player.isGliding()) {
                player.setGliding(false);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();

        // Check if player is in combat
        if (!combatManager.isInCombat(player)) return;

        String command = event.getMessage().toLowerCase();
        String[] args = command.split(" ");
        String baseCommand = args[0];

        // Check if command is in allowed list
        boolean isAllowed = false;
        for (String allowedCmd : ALLOWED_COMMANDS) {
            if (baseCommand.startsWith(allowedCmd.toLowerCase())) {
                isAllowed = true;
                break;
            }
        }

        if (!isAllowed) {
            event.setCancelled(true);
            long remainingTime = combatManager.getRemainingCombatTime(player);
            int seconds = (int) Math.ceil(remainingTime / 1000.0);
            player.sendMessage("§c§l[COMBAT] §cYou cannot use commands while in combat! " +
                    "§c(" + seconds + "s remaining)");
        }
    }
}