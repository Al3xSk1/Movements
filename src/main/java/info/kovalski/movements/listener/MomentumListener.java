package info.kovalski.movements.listener;

import info.kovalski.movements.utils.MomentumBarManager;
import info.kovalski.movements.utils.MomentumUtil;
import info.kovalski.movements.utils.PlayerUtil;
import org.bukkit.plugin.Plugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
public class MomentumListener implements Listener {

    private final Plugin plugin;
    private final Set<UUID> jumpedRecently = new HashSet<>();

    public MomentumListener(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!event.hasChangedPosition()) return;

        Player player = event.getPlayer();
        UUID id = player.getUniqueId();

        if (!PlayerUtil.isMovementAllowed(player)) return;

        if (player.isSprinting() && player.isOnGround()) {
            double previousMomentum = MomentumUtil.get(player);
            MomentumUtil.add(player, MomentumUtil.getMomentumIncrease());
            if (MomentumUtil.get(player) != previousMomentum) MomentumBarManager.update(player);
        } else if (!player.isSprinting() && player.isOnGround() && MomentumUtil.get(player) > 0.0) {
            MomentumUtil.set(player, 0.0);
            MomentumBarManager.update(player);
        }

        if (!jumpedRecently.contains(id)
                && !player.isOnGround()
                && event.getFrom().getY() < event.getTo().getY()) {

            jumpedRecently.add(id);
            Bukkit.getScheduler().runTaskLater(plugin, () -> jumpedRecently.remove(id), 10L);

            double currentMomentum = MomentumUtil.get(player);
            if (player.isSprinting()) {
                double reducedMomentum = Math.max(0.0, currentMomentum - MomentumUtil.getMomentumDecrease());
                if (reducedMomentum != currentMomentum) {
                    MomentumUtil.set(player, reducedMomentum);
                    MomentumBarManager.update(player);
                }
            } else {
                if (currentMomentum > 0.0) {
                    MomentumUtil.set(player, 0.0);
                    MomentumBarManager.update(player);
                }
            }
        }

    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        jumpedRecently.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        jumpedRecently.remove(event.getEntity().getUniqueId());
    }
}
