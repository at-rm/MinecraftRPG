package dev.atarim.xplevel;

import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

/**
 * Methods from LevelLogic
 */
public class LevelSQL implements Listener {

    private XPLevel plugin = XPLevel.getPlugin(XPLevel.class);

    /**
     * When players join a new database entry is created if they're not in the database yet.
     * @param playerJoinEvent   the event
     */
    @EventHandler
    public void onJoin (PlayerJoinEvent playerJoinEvent) {
        Player player = playerJoinEvent.getPlayer();
       // plugin.getServer().broadcastMessage("onjoin is called");
        createPlayer(player.getUniqueId());
    }

    @EventHandler
    public void onBreak (BlockBreakEvent blockBreakEvent) {
        gainXP(2, blockBreakEvent.getPlayer());
    }

    /**
     * Creates a player if the player's uuid is not in the database yet.
     * @param uuid  the player's uuid
     */
    private void createPlayer(UUID uuid) {
        try {
            plugin.getServer().broadcastMessage("createPlayer is called");
            PreparedStatement statement = plugin.getConnection()
                    .prepareStatement("SELECT * FROM " + plugin.table + " WHERE UUID=?");
            statement.setString(1, uuid.toString());
            ResultSet results = statement.executeQuery();
            results.next();
            if (!playerExists(uuid)) {
                plugin.getServer().broadcastMessage("createPlayer for non existing player");
                PreparedStatement insert = plugin.getConnection()
                        .prepareStatement("INSERT INTO " + plugin.table
                                + " (uuid, level, xp, skillPoints, healthLevel, strengthLevel, speedLevel, enduranceLevel, intelligenceLevel) " +
                                "VALUES (?,?,?,?,?,?,?,?,?)");
                insert.setString(1, uuid.toString());
                insert.setInt(2, 1);
                insert.setInt(3, 0);
                insert.setInt(4, 0);
                insert.setInt(5, 0);
                insert.setInt(6, 0);
                insert.setInt(7, 0);
                insert.setInt(8, 0);
                insert.setInt(9, 0);
                insert.executeUpdate();

                plugin.getServer().broadcastMessage(ChatColor.GREEN + "Player Inserted");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks if the player exists in the database already.
     * @param uuid  the player's uuid
     * @return  if player exists
     */
    private boolean playerExists(UUID uuid) {
        try {
            plugin.getServer().broadcastMessage("playerExists is called");
            PreparedStatement statement = plugin.getConnection()
                    .prepareStatement("SELECT * FROM " + plugin.table + " WHERE UUID=?");
            statement.setString(1, uuid.toString());

            ResultSet results = statement.executeQuery();
            if (results.next()) {
                plugin.getServer().broadcastMessage(ChatColor.YELLOW + "Player Found");
                return true;
            }
            plugin.getServer().broadcastMessage(ChatColor.RED + "Player NOT Found");

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * What happens when a player gains xp.
     * @param gainedXp  the xp the player gains
     * @param player    the player that gained xp
     */
    public void gainXP (int gainedXp, Player player) {
        // TODO add xp to xp-column and maybe increase level, calls isNextLevel and levelUp
        // TODO get current xp, get current level, get intelligence, get player's uuid as string
            String playerUuid = player.getUniqueId().toString();
            int xp = getValueSQL("xp", playerUuid);
            int level = getValueSQL("level", playerUuid);
            int intelligenceLevel = getValueSQL("intelligenceLevel", playerUuid);
        // skill points in case the player levels up
            int skillPoints = getValueSQL("skillPoints", playerUuid);
        // TODO newXp = formerXP + gainedXp*(intelligence + 1)
        int newXp = xp + gainedXp * (intelligenceLevel + 1);
        // TODO if xp < level requirement, just update xp
        int levelReq = levelRequirement(level);
        if (newXp < levelReq) {
            updateValueSQL("xp", newXp, playerUuid);
            player.sendMessage("works");
        } else {
            // TODO if xp >= level requirement, update level, adjust xp then update xp
            level++;
            updateValueSQL("level", level, playerUuid);
            skillPoints++;
            updateValueSQL("skillPoints", skillPoints, playerUuid);
            newXp -= levelReq;
            updateValueSQL("xp", newXp, playerUuid);
            player.sendMessage(ChatColor.GOLD + "You achieved Level " + level);
        }
        // TODO UPDATE players SET xp = newXp WHERE uuid = player's uuid
    }

    /**
     * Gets a specific column entry for a player.
     * @param value the name of the column the value is in
     * @param playerUuid    the player's uuid as a String
     * @return  the value of the field
     */
    private int getValueSQL (String value, String playerUuid) {
        String command = "SELECT * FROM players WHERE uuid=?";
        try {
            PreparedStatement getValue = plugin.getConnection().prepareStatement(command);
            getValue.setString(1, playerUuid);
            ResultSet result = getValue.executeQuery();
            result.next();
            return result.getInt(value);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Updates the value of a field for a player.
     * @param value the column name of the value
     * @param newValue  the new value of the field
     * @param playerUuid    the player who this applies to
     */
    private void updateValueSQL (String value, int newValue, String playerUuid) {
        // TODO UPDATE players SET xp = newXp WHERE uuid = player's uuid
        String command = "UPDATE players SET " + value + " = ? WHERE uuid = ?";
        try {
            PreparedStatement update = plugin.getConnection().prepareStatement(command);
            // update.setString(1, value);
            update.setInt(1, newValue);
            update.setString(2, playerUuid);
            update.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int levelRequirement (int level) {
        //TODO level up logic
        return 10;
    }

    /**
     * Level up by selecting a skill.
     * @param skill the skill that the player selected
     * @param player    the player that selected the skill
     */
    public void skillUp (String skill, Player player) {
        //TODO check skill points > 0, increase specified skill
        String playersName = player.getUniqueId().toString();
        int availableSkillPoints = getValueSQL("skillPoints", playersName);
        if (availableSkillPoints == 0) {
            player.sendMessage(ChatColor.DARK_PURPLE + "You don't have any skill points available.");
            return;
        }
        switch (skill) {
            case "health":
                int healthLevel = getValueSQL("healthLevel", playersName);
                updateValueSQL("healthLevel", healthLevel + 1, playersName);
                player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20 + healthLevel);
                break;
            case "strength":
                int strengthLevel = getValueSQL("strengthLevel", playersName);
                updateValueSQL("strengthLevel", strengthLevel + 1, playersName);
                player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(1 + strengthLevel * 0.5);
                break;
            case "speed":
                int speedLevel = getValueSQL("speedLevel", playersName);
                updateValueSQL("strengthLevel", speedLevel + 1, playersName);
                player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.1 + speedLevel * 0.02);
                player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(4 + speedLevel * 0.5);
                break;
            case "endurance":
                //TODO what should endurance do? :c
                break;
            case "intelligence":
                int intelligenceLevel = getValueSQL("intelligenceLevel", playersName);
                updateValueSQL("intelligenceLevel", intelligenceLevel + 1, playersName);
                break;
            default:
                plugin.getServer().broadcastMessage("Uh-oh something went wrong.");
                break;
        }
        // used one skill point
        updateValueSQL("skillPoints", availableSkillPoints - 1, playersName);
    }

}
