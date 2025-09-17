package hs.generalControls.listeners;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.managers.RegionManager;
import hs.generalControls.combat.CombatManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Arrays;
import java.util.List;

public class CombatListener implements Listener {

    private final CombatManager combatManager;
    private final boolean worldGuardEnabled;

    // Commands that are allowed during combat
    private final List<String> ALLOWED_COMMANDS = Arrays.asList(
            "/msg", "/tell", "/w", "/whisper", "/reply", "/r",
            "/help", "/rules", "/discord", "/website", "/vote"
    );

    public CombatListener(CombatManager combatManager) {
        this.combatManager = combatManager;
        this.worldGuardEnabled = Bukkit.getPluginManager().isPluginEnabled("WorldGuard");

        if (worldGuardEnabled) {
            Bukkit.getLogger().info("[GeneralControls] WorldGuard integration enabled - PvP regions will be respected");
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        // Check if player is in combat
        if (!combatManager.isInCombat(player)) return;

        // Check if player moved to a different block (to avoid spam)
        if (event.getFrom().getBlockX() == event.getTo().getBlockX() &&
                event.getFrom().getBlockY() == event.getTo().getBlockY() &&
                event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }

        // Check if player moved into a PvP-disabled area
        if (!isPvPEnabledAtLocation(event.getTo())) {
            combatManager.removeCombatTag(player);
            player.sendMessage("§a§l[COMBAT] §aCombat ended - You entered a safe zone.");
        }
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
            killer.sendMessage("§a§l[COMBAT] §aCombat ended");
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
            // Check if PvP is enabled in this world/area
            if (!isPvPEnabledForPlayers(attacker, victim)) {
                // If PvP is disabled, don't tag players and remove existing combat tags
                if (combatManager.isInCombat(attacker)) {
                    combatManager.removeCombatTag(attacker);
                    attacker.sendMessage("§a§l[COMBAT] §aCombat ended - PvP disabled in this area.");
                }
                if (combatManager.isInCombat(victim)) {
                    combatManager.removeCombatTag(victim);
                    victim.sendMessage("§a§l[COMBAT] §aCombat ended - PvP disabled in this area.");
                }
                return;
            }

            // Tag both players for combat
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
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;

        Player player = (Player) event.getWhoClicked();

        // Check if player is in combat
        if (!combatManager.isInCombat(player)) return;

        // Check if it's the player's inventory
        if (!(event.getInventory() instanceof PlayerInventory)) return;

        ItemStack clickedItem = event.getCurrentItem();
        ItemStack cursorItem = event.getCursor();

        // Check if player is trying to equip elytra (either by clicking on chestplate slot or shift-clicking)
        boolean tryingToEquipElytra = false;

        // Case 1: Direct click on chestplate slot (slot 38)
        if (event.getSlot() == 38) {
            // Check if cursor has elytra
            if (cursorItem != null && cursorItem.getType() == Material.ELYTRA) {
                tryingToEquipElytra = true;
            }
        }

        // Case 2: Shift-click elytra from inventory to equip
        if (event.isShiftClick() && clickedItem != null && clickedItem.getType() == Material.ELYTRA) {
            // Check if chestplate slot is empty or can be replaced
            ItemStack currentChestplate = player.getInventory().getChestplate();
            if (currentChestplate == null || currentChestplate.getType() == Material.AIR) {
                tryingToEquipElytra = true;
            }
        }

        // Cancel if trying to equip elytra
        if (tryingToEquipElytra) {
            event.setCancelled(true);
            player.sendMessage("§c§l[COMBAT] §cYou cannot equip elytra while in combat!");
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

    /**
     * Check if PvP is enabled between two players
     */
    private boolean isPvPEnabledForPlayers(Player attacker, Player victim) {
        // Check basic world PvP settings
        if (!attacker.getWorld().getPVP() || !victim.getWorld().getPVP()) {
            return false;
        }

        // Check if players are in different worlds
        if (!attacker.getWorld().equals(victim.getWorld())) {
            return false;
        }

        // Check WorldGuard regions for both players
        return isPvPEnabledAtLocation(attacker.getLocation()) &&
                isPvPEnabledAtLocation(victim.getLocation());
    }

    /**
     * Check if PvP is enabled at a specific location
     */
    private boolean isPvPEnabledAtLocation(Location location) {
        // Check basic world PvP setting
        if (!location.getWorld().getPVP()) {
            return false;
        }

        // Check WorldGuard if available
        if (worldGuardEnabled) {
            return isWorldGuardPvPAllowed(location);
        }

        return true; // PvP is enabled if no restrictions found
    }

    /**
     * Check WorldGuard PvP flag at location
     */
    private boolean isWorldGuardPvPAllowed(Location location) {
        try {
            // Get WorldGuard's RegionManager for the world
            RegionManager regionManager = WorldGuard.getInstance().getPlatform()
                    .getRegionContainer().get(BukkitAdapter.adapt(location.getWorld()));

            if (regionManager == null) {
                return true; // No regions in this world, allow PvP
            }

            // Convert Bukkit location to WorldEdit vector
            com.sk89q.worldedit.math.BlockVector3 vector = BukkitAdapter.asBlockVector(location);

            // Get applicable regions at this location
            ApplicableRegionSet regions = regionManager.getApplicableRegions(vector);

            // Check if PvP flag is set to DENY in any region
            // If no flag is set, it defaults to the world's PvP setting
            if (regions.queryState(null, Flags.PVP) == com.sk89q.worldguard.protection.flags.StateFlag.State.DENY) {
                return false;
            }

            return true; // PvP allowed
        } catch (Exception e) {
            return true;
        }
    }
}