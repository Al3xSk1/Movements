package info.kovalski.movements.listener;

import info.kovalski.movements.utils.MomentumBarManager;
import info.kovalski.movements.utils.MomentumUtil;
import info.kovalski.movements.utils.PlayerDataManager;
import info.kovalski.movements.utils.StaminaHUD;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {

    private final PlayerDataManager playerDataManager;

    public JoinListener(PlayerDataManager playerDataManager) {
        this.playerDataManager = playerDataManager;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        playerDataManager.loadPlayerData(player);
        StaminaHUD.updateStaminaBar(player);
        MomentumUtil.reset(player);
        MomentumBarManager.update(player);
    }
}
