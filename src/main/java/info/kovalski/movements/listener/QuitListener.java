package info.kovalski.movements.listener;

import info.kovalski.movements.utils.MomentumBarManager;
import info.kovalski.movements.utils.MomentumUtil;
import info.kovalski.movements.utils.StaminaUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class QuitListener implements Listener {
    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        MomentumBarManager.clear(e.getPlayer());
        MomentumUtil.reset(e.getPlayer());
        StaminaUtil.clear(e.getPlayer());
    }
}
