package info.kovalski.movements.listener;

import info.kovalski.movements.utils.MomentumBarManager;
import info.kovalski.movements.utils.MomentumUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

public class CombatListener implements Listener {

    private final Plugin plugin;

    public CombatListener(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityHit(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player attacker)) return;
        if (!(event.getEntity() instanceof LivingEntity victim)) return;

        double currentMomentum = MomentumUtil.get(attacker);
        double maxMomentum = MomentumUtil.getMaxMomentum();

        if (currentMomentum < maxMomentum * 0.25) return;

        // Apply directional knockback
        double momentumRatio = currentMomentum / maxMomentum;
        double knockbackStrength = 0.3 + (momentumRatio * 0.7); // range: 0.3 – 1.0

        Vector direction = attacker.getLocation().getDirection().normalize();
        Vector knockback = direction.multiply(knockbackStrength).setY(0.25);

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (victim.isValid()) victim.setVelocity(knockback);
        }, 1L);

        // Reset attacker momentum
        MomentumUtil.set(attacker, 0.0);
        MomentumBarManager.update(attacker); // ✅ update HUD

        // If victim is a player, reset their momentum too
        if (victim instanceof Player hitPlayer) {
            MomentumUtil.set(hitPlayer, 0.0);
            MomentumBarManager.update(hitPlayer); // ✅ update HUD
        }
    }
}
