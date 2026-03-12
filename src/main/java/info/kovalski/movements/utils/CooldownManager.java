package info.kovalski.movements.utils;

import java.util.HashMap;
import java.util.UUID;

public class CooldownManager {

    private final HashMap<UUID, Long> cooldowns = new HashMap<>();

    public void setCooldown(UUID playerId, long durationMillis) {
        cooldowns.put(playerId, System.currentTimeMillis() + durationMillis);
    }

    public boolean isOnCooldown(UUID playerId) {
        if (!cooldowns.containsKey(playerId)) return false;
        return System.currentTimeMillis() < cooldowns.get(playerId);
    }

    public long getRemaining(UUID playerId) {
        return Math.max(0, cooldowns.getOrDefault(playerId, 0L) - System.currentTimeMillis());
    }

    public void clearCooldown(UUID playerId) {
        cooldowns.remove(playerId);
    }
}
