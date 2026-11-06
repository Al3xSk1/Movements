package info.kovalski.movements.utils;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class MomentumBarManager {

    private static final HashMap<UUID, BossBar> bars = new HashMap<>();
    private static final HashMap<UUID, Long> lastActive = new HashMap<>();
    private static final long HIDE_DELAY_TICKS = 40L; // 2 seconds

    public static void update(Player p) {
        UUID id = p.getUniqueId();
        double momentum = MomentumUtil.get(p);
        double value = momentum / MomentumUtil.getMaxMomentum();
        value = Math.max(0.0, Math.min(1.0, value)); // safety clamp

        BossBar bar = bars.get(id);
        if (bar == null) {
            bar = Bukkit.createBossBar("Momentum", getColor(value), BarStyle.SOLID);
            bar.addPlayer(p);
            bars.put(id, bar);
        }

        if (momentum > 0.01) {
            lastActive.put(id, System.currentTimeMillis());
            bar.setVisible(true);
        } else {
            long last = lastActive.getOrDefault(id, 0L);
            long now = System.currentTimeMillis();
            if (now - last > HIDE_DELAY_TICKS * 50L) {
                bar.setVisible(false); // hide after delay
            }
        }

        bar.setProgress(value);
        bar.setColor(getColor(value));
    }


    private static BarColor getColor(double value) {
        if (value >= 0.75) return BarColor.RED;
        if (value >= 0.5) return BarColor.YELLOW;
        return BarColor.BLUE;
    }

    public static void clear(Player p) {
        UUID id = p.getUniqueId();
        if (bars.containsKey(id)) {
            bars.get(id).removeAll();
            bars.remove(id);
        }
        lastActive.remove(id);
    }
}