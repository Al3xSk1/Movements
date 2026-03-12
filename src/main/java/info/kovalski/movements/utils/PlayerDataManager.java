package info.kovalski.movements.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class PlayerDataManager {

    private final JavaPlugin plugin;
    private final File dataFolder;

    public PlayerDataManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.dataFolder = plugin.getDataFolder();
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
    }

    public void loadPlayerData(Player player) {
        UUID playerId = player.getUniqueId();
        File playerFile = new File(dataFolder, playerId + ".yml");

        if (!playerFile.exists()) {
            createNewPlayerData(player);
        } else {
            FileConfiguration data = plugin.getConfig();
            // Load player level and XP data
            int level = data.getInt(playerId + ".level", 1);
            int xp = data.getInt(playerId + ".xp", 0);
            double maxStamina = data.getDouble(playerId + ".maxStamina", 100);

            // Set player's level, XP, and max stamina
            player.setLevel(level);
            player.setExp(xp / (float) getXPRequiredForNextLevel(level)); // Adjust player's XP bar
            StaminaUtil.setMaxStamina(player, maxStamina);

            // Load rewards and other data if needed
        }
    }

    public void savePlayerData(Player player) {
        UUID playerId = player.getUniqueId();
        File playerFile = new File(dataFolder, playerId + ".yml");
        FileConfiguration data = plugin.getConfig();

        // Save player level, XP, and max stamina
        data.set(playerId + ".level", player.getLevel());
        data.set(playerId + ".xp", (int) player.getExp()); // store the XP as an int
        data.set(playerId + ".maxStamina", StaminaUtil.getMaxStamina(player));

        // Apply rewards logic if needed
        applyLevelRewards(player);

        try {
            data.save(playerFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createNewPlayerData(Player player) {
        UUID playerId = player.getUniqueId();
        File playerFile = new File(dataFolder, playerId + ".yml");
        FileConfiguration data = plugin.getConfig();

        // Default values for new player
        data.set(playerId + ".level", 1);
        data.set(playerId + ".xp", 0);
        data.set(playerId + ".maxStamina", 100); // Default max stamina

        try {
            data.save(playerFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void applyLevelRewards(Player player) {
        // Add specific rewards logic based on player level
        int level = player.getLevel();
        if (level == 5) {
            // Apply reward for reaching level 5 (example: new ability unlocked)
            player.sendMessage("§aCongratulations! You unlocked a new ability at level 5!");
        }
        // Add more levels and their respective rewards here
    }

    private int getXPRequiredForNextLevel(int level) {
        return (int) (100 * Math.pow(1.2, level - 1)); // Example XP scaling
    }
}
