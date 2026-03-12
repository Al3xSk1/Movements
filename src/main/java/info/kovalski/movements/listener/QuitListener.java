package info.kovalski.movements.listener;

import info.kovalski.movements.utils.MomentumBarManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class QuitListener implements Listener {
    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        MomentumBarManager.clear(e.getPlayer());
    }
}
