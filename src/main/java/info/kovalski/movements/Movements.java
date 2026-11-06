package info.kovalski.movements;

import info.kovalski.movements.listener.*;
import info.kovalski.movements.utils.PlayerDataManager;
import info.kovalski.movements.utils.StaminaUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class Movements extends JavaPlugin {

    private PlayerDataManager playerDataManager;

    @Override
    public void onEnable() {
        this.playerDataManager = new PlayerDataManager(this);

        getServer().getPluginManager().registerEvents(new SavePlayerDataListener(playerDataManager), this);
        getServer().getPluginManager().registerEvents(new JoinListener(playerDataManager), this);
        getServer().getPluginManager().registerEvents(new WallRunListener(), this);
        getServer().getPluginManager().registerEvents(new WallJumpListener(), this);
        getServer().getPluginManager().registerEvents(new ChargeDashListener(), this);
        getServer().getPluginManager().registerEvents(new MomentumListener(this), this);
        getServer().getPluginManager().registerEvents(new RollListener(), this);
        getServer().getPluginManager().registerEvents(new LedgeShimmyListener(), this);
        getServer().getPluginManager().registerEvents(new SlideListener(), this);
        getServer().getPluginManager().registerEvents(new ChainSwingListener(), this);
        getServer().getPluginManager().registerEvents(new LadderListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(), this);
        getServer().getPluginManager().registerEvents(new JumpPadListener(), this);
        getServer().getPluginManager().registerEvents(new QuitListener(), this);
        getServer().getPluginManager().registerEvents(new CombatListener(this), this);

        Bukkit.getScheduler().runTaskTimer(this, () -> {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.isOnGround()) {
                    StaminaUtil.regenerate(p, 2.5);
                }
            }
        }, 0L, 2L);

        getLogger().info("Movement plugin enabled.");
    }
    @Override
    public void onDisable() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            playerDataManager.savePlayerData(player);
        }
        getLogger().info("Movement plugin disabled.");
    }
}
