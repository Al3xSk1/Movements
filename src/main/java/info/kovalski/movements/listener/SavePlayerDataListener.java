package info.kovalski.movements.listener;


import info.kovalski.movements.Movements;
import info.kovalski.movements.utils.PlayerDataManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class SavePlayerDataListener implements Listener {

    private final PlayerDataManager playerDataManager;

    public SavePlayerDataListener() {
        this.playerDataManager = Movements.getPlugin(Movements.class).getPlayerDataManager();
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        playerDataManager.savePlayerData(player); // Save player data on quit
    }
}
