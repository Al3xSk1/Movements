package info.kovalski.movements.utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.logging.Level;

public final class PlayerDataManager {

    private static final int DEFAULT_LEVEL = 1;
    private static final double DEFAULT_MAX_STAMINA = 100.0;

    private final JavaPlugin plugin;
    private final File dataFolder;

    public PlayerDataManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.dataFolder = plugin.getDataFolder();
    }

    public void loadPlayerData(Player player) {
        UUID playerId = player.getUniqueId();
        File playerFile = getPlayerFile(playerId);
        YamlConfiguration data = YamlConfiguration.loadConfiguration(playerFile);

        // Migrate UUID-prefixed values written by older versions into the player file.
        FileConfiguration legacyData = plugin.getConfig();
        String legacyPath = playerId + ".";
        int level = data.getInt("level", data.getInt(legacyPath + "level",
                legacyData.getInt(legacyPath + "level", DEFAULT_LEVEL)));
        double xpProgress = data.getDouble("xp", data.getDouble(legacyPath + "xp",
                legacyData.getDouble(legacyPath + "xp", 0.0)));
        double maxStamina = data.getDouble("maxStamina", data.getDouble(legacyPath + "maxStamina",
                legacyData.getDouble(legacyPath + "maxStamina", DEFAULT_MAX_STAMINA)));
        double stamina = data.getDouble("stamina", maxStamina);

        player.setLevel(Math.max(0, level));
        player.setExp((float) clamp(xpProgress, 0.0, 1.0));
        StaminaUtil.setMaxStamina(player, maxStamina);
        StaminaUtil.setStamina(player, stamina);

        if (!playerFile.isFile() || !data.contains("maxStamina")) {
            savePlayerData(player);
        }
    }

    public void savePlayerData(Player player) {
        if (!dataFolder.exists() && !dataFolder.mkdirs()) {
            plugin.getLogger().severe("Could not create player data directory: " + dataFolder);
            return;
        }

        File playerFile = getPlayerFile(player.getUniqueId());
        YamlConfiguration data = YamlConfiguration.loadConfiguration(playerFile);
        data.set(player.getUniqueId().toString(), null);
        data.set("level", player.getLevel());
        data.set("xp", player.getExp());
        data.set("maxStamina", StaminaUtil.getMaxStamina(player));
        data.set("stamina", StaminaUtil.getStamina(player));

        try {
            data.save(playerFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not save player data for " + player.getName(), e);
        }
    }

    private File getPlayerFile(UUID playerId) {
        return new File(dataFolder, playerId + ".yml");
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}
