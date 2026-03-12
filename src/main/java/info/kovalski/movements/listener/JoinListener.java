package info.kovalski.movements.listener;

import info.kovalski.movements.Movements;
import info.kovalski.movements.utils.MomentumBarManager;
import info.kovalski.movements.utils.MomentumUtil;
import info.kovalski.movements.utils.PlayerDataManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {

    private final PlayerDataManager playerDataManager;

    public JoinListener() {
        this.playerDataManager = Movements.getPlugin(Movements.class).getPlayerDataManager();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        playerDataManager.loadPlayerData(player); // Load player data on join
        if (player != null) {
            // Reset or initialize any momentum-related variables here if necessary
            MomentumUtil.reset(player);  // Make sure the player's momentum is initialized correctly
            MomentumBarManager.update(player);  // This will display the momentum bar again
        }

    }
}
