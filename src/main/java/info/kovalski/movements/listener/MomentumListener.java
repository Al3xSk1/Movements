package info.kovalski.movements.listener;

import info.kovalski.movements.Movements;
import info.kovalski.movements.utils.MomentumBarManager;
import info.kovalski.movements.utils.MomentumUtil;
import info.kovalski.movements.utils.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
public class MomentumListener implements Listener {

    private final Set<UUID> jumpedRecently = new HashSet<>();

    private static final Set<UUID> xpAwardedThisChain = new HashSet<>();

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        UUID id = player.getUniqueId();

        if (!PlayerUtil.isMovementAllowed(player)) return;

        // Momentum gain/loss logic
        if (player.isSprinting() && player.isOnGround()) {
            MomentumUtil.add(player, MomentumUtil.getMOMENTUM_INCREASE());
            MomentumBarManager.update(player);
        } else if (!player.isSprinting() && player.isOnGround()) {
            MomentumUtil.set(player, 0.0);
            MomentumBarManager.update(player);
        }

        // Jump detection
        if (!jumpedRecently.contains(id)
                && !player.isOnGround()
                && event.getFrom().getY() < event.getTo().getY()) {

            jumpedRecently.add(id);
            Bukkit.getScheduler().runTaskLater(Movements.getPlugin(), () -> jumpedRecently.remove(id), 10L);

            double currentMomentum = MomentumUtil.get(player);
            if (player.isSprinting()) {
                MomentumUtil.set(player, Math.max(0.0, currentMomentum - MomentumUtil.getMOMENTUM_DECREASE()));
                MomentumBarManager.update(player);
            } else {
                MomentumUtil.set(player, 0.0);
                MomentumBarManager.update(player);
            }
        }

        double currentMomentum = MomentumUtil.get(player);


    }

    public static void resetXPFlagForPlayer(UUID playerId) {
        xpAwardedThisChain.remove(playerId);
    }
}
