package info.kovalski.movements.listener;

import info.kovalski.movements.utils.MomentumBarManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeathListener implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        MomentumBarManager.clear(event.getPlayer());
    }
}
