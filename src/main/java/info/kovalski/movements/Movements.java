package info.kovalski.movements;

import info.kovalski.movements.listener.*;
import info.kovalski.movements.placeholders.MovementsPlaceholders;
import info.kovalski.movements.utils.PlayerDataManager;
import info.kovalski.movements.utils.StaminaUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class Movements extends JavaPlugin {

    private static Movements instance;
    private PlayerDataManager playerDataManager;
    public static @NotNull Plugin getPlugin() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        this.playerDataManager = new PlayerDataManager(this);

        getServer().getPluginManager().registerEvents(new SavePlayerDataListener(), this);
        getServer().getPluginManager().registerEvents(new JoinListener(), this);
        getServer().getPluginManager().registerEvents(new WallRunListener(), this);
        getServer().getPluginManager().registerEvents(new WallJumpListener(), this);
        getServer().getPluginManager().registerEvents(new ChargeDashListener(), this);
        getServer().getPluginManager().registerEvents(new MomentumListener(), this);
        getServer().getPluginManager().registerEvents(new RollListener(), this);
        getServer().getPluginManager().registerEvents(new LedgeShimmyListener(), this);
        getServer().getPluginManager().registerEvents(new SlideListener(), this);
        getServer().getPluginManager().registerEvents(new ChainSwingListener(), this);
        getServer().getPluginManager().registerEvents(new LadderListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(), this);
        getServer().getPluginManager().registerEvents(new JumpListener(), this);
        getServer().getPluginManager().registerEvents(new SprintListener(), this);
        getServer().getPluginManager().registerEvents(new JumpPadListener(), this);
        getServer().getPluginManager().registerEvents(new QuitListener(), this);
        getServer().getPluginManager().registerEvents(new CombatListener(), this);


        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new MovementsPlaceholders();  // Register the placeholder
        } else {
            getLogger().warning("PlaceholderAPI not found, skillchainxp placeholder will not work!");
        }

        Bukkit.getScheduler().runTaskTimer(this, () -> {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.isOnGround()) {
                    StaminaUtil.regenerate(p, 2.5);
                }
            }
        }, 0L, 2L);

        getLogger().info("Movement plugin enabled.");
    }
    public PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }

    @Override
    public void onDisable() {
        getLogger().info("Movement plugin disabled.");
    }
}
