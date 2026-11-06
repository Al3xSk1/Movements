package info.kovalski.movements.listener;


import info.kovalski.movements.utils.PlayerDataManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class SavePlayerDataListener implements Listener {

    private final PlayerDataManager playerDataManager;

    public SavePlayerDataListener(PlayerDataManager playerDataManager) {
        this.playerDataManager = playerDataManager;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        playerDataManager.savePlayerData(event.getPlayer());
    }
}
