package info.kovalski.movements.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class CooldownManager {

    private final Map<UUID, Long> cooldowns = new HashMap<>();

    public void setCooldown(UUID playerId, long durationMillis) {
        if (durationMillis <= 0) {
            cooldowns.remove(playerId);
            return;
        }
        cooldowns.put(playerId, System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(durationMillis));
    }

    public boolean isOnCooldown(UUID playerId) {
        Long expiresAt = cooldowns.get(playerId);
        if (expiresAt == null) return false;
        if (System.nanoTime() < expiresAt) return true;
        cooldowns.remove(playerId);
        return false;
    }

    public long getRemaining(UUID playerId) {
        Long expiresAt = cooldowns.get(playerId);
        if (expiresAt == null) return 0;
        long remaining = expiresAt - System.nanoTime();
        if (remaining <= 0) {
            cooldowns.remove(playerId);
            return 0;
        }
        return TimeUnit.NANOSECONDS.toMillis(remaining);
    }

    public void clearCooldown(UUID playerId) {
        cooldowns.remove(playerId);
    }
}
