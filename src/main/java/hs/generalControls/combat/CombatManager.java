package hs.generalControls.combat;

import hs.generalControls.GeneralControls;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class CombatManager {

    private final GeneralControls plugin;
    private final Map<UUID, Long> combatTags;
    private final long COMBAT_TAG_DURATION = 15000; // 15 seconds in milliseconds

    public CombatManager(GeneralControls plugin) {
        this.plugin = plugin;
        this.combatTags = new HashMap<>();
    }

    public void tagPlayer(Player player) {
        UUID playerId = player.getUniqueId();
        long expireTime = System.currentTimeMillis() + COMBAT_TAG_DURATION;

        if (!isInCombat(player)) {
            combatTags.put(playerId, expireTime);
            player.sendMessage("§c§l[COMBAT] §cYou are now in combat for 15 seconds!");
        } else {
            // Refresh the combat tag
            combatTags.put(playerId, expireTime);
        }
    }

    public boolean isInCombat(Player player) {
        UUID playerId = player.getUniqueId();
        if (!combatTags.containsKey(playerId)) {
            return false;
        }

        long expireTime = combatTags.get(playerId);
        if (System.currentTimeMillis() >= expireTime) {
            combatTags.remove(playerId);
            return false;
        }

        return true;
    }

    public void removeCombatTag(Player player) {
        combatTags.remove(player.getUniqueId());
    }

    public long getRemainingCombatTime(Player player) {
        UUID playerId = player.getUniqueId();
        if (!combatTags.containsKey(playerId)) {
            return 0;
        }

        long expireTime = combatTags.get(playerId);
        long remainingTime = expireTime - System.currentTimeMillis();
        return Math.max(0, remainingTime);
    }

    public void startCleanupTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                cleanupExpiredTags();
            }
        }.runTaskTimer(plugin, 20L, 20L); // Run every second
    }

    private void cleanupExpiredTags() {
        long currentTime = System.currentTimeMillis();
        Iterator<Map.Entry<UUID, Long>> iterator = combatTags.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<UUID, Long> entry = iterator.next();
            if (currentTime >= entry.getValue()) {
                iterator.remove();

                // Notify player that combat has ended
                Player player = plugin.getServer().getPlayer(entry.getKey());
                if (player != null && player.isOnline()) {
                    player.sendMessage("§a§l[COMBAT] §aYou are no longer in combat!");
                }
            }
        }
    }
}