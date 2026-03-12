package info.kovalski.movements.placeholders;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.roborepo.movements.utils.LevelingUtil;
import net.roborepo.movements.utils.SkillsUtil;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class MovementsPlaceholders extends PlaceholderExpansion {

    @Override
    public @NotNull String getIdentifier() {
        return "movements"; // Allows %movements_level%, %movements_skillchainxp%
    }

    @Override
    public @NotNull String getAuthor() {
        return "YourName"; // Optional: Replace with your dev name
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String identifier) {
        if (player == null) return "";

        switch (identifier.toLowerCase()) {
            case "skillchainxp":
                return String.valueOf(SkillsUtil.getSkillChainXP(player));
            case "level":
                return String.valueOf(LevelingUtil.getLevel(player));
            case "currentskill":
                return SkillsUtil.getLastSkillUsed(player); // Make sure this returns a string
            default:
                return null;
        }
    }
}
