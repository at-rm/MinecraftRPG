package dev.atarim.xplevel;

import com.connorlinfoot.titleapi.TitleAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

/**
 * Methods from LevelLogic
 */
public class LevelSQL implements Listener {

    private XPLevel plugin = XPLevel.getPlugin(XPLevel.class);
    private BossBar levelBar;


    /**
     * When players join a new database entry is created if they're not in the database yet.
     * @param playerJoinEvent   the event
     */
    @EventHandler
    public void onJoin (PlayerJoinEvent playerJoinEvent) {
        Player player = playerJoinEvent.getPlayer();
        createPlayer(player.getUniqueId());
        if (player.hasPlayedBefore()) {
            player.sendMessage(ChatColor.GOLD + "Welcome Back to Lapyta Skyblock, " + player.getName() + "!");
        } else {
            player.sendMessage(ChatColor.GOLD + "Welcome to Lapyta Skyblock, " + player.getName() + "!");
        }
        int speedLevel = getValueSQL("speedLevel", player.getUniqueId().toString());
        player.setWalkSpeed(0.15F + speedLevel * 0.01F);
        int healthLevel = getValueSQL("healthLevel", player.getUniqueId().toString());
        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20 + healthLevel);
        int strengthLevel = getValueSQL("strengthLevel", player.getUniqueId().toString());
        player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(1 + strengthLevel * 0.5);

        // Level Bar says: Level 23 | 76050/84200 XP | (Bold) +30xp
        levelBar = newLevelBar(player);

        // Need Player's current level to get their nickname.
        int level = getValueSQL("level", player.getUniqueId().toString());
        // Set their nickname
        String levelString = ChatColor.DARK_GRAY + "[" + getLevelColor(level) + "✶" + ChatColor.BOLD + level
                +  ChatColor.RESET + getLevelColor(level) + "✶"  +  ChatColor.DARK_GRAY + "] " + ChatColor.WHITE + player.getName();
        player.setDisplayName(levelString);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onChat(final AsyncPlayerChatEvent e) {
        e.setFormat("%1$s: %2$s");
    }

    @EventHandler
    public void onBreak (BlockBreakEvent blockBreakEvent) {
        gainXP(10, blockBreakEvent.getPlayer());
    }

    @EventHandler
    public void onPlace (BlockPlaceEvent blockPlaceEvent) {
        gainXP (10, blockPlaceEvent.getPlayer());
    }

    @EventHandler
    public void onRespawn (PlayerRespawnEvent playerRespawnEvent) {
        Player player = playerRespawnEvent.getPlayer();
        int playerHealth = 20 + getValueSQL("healthLevel", player.getUniqueId().toString());
        player.setHealth(playerHealth);
    }

    // TODO implement Entity level
    @EventHandler
    public void onKill (EntityDamageByEntityEvent entityEvent) {
        if (entityEvent.getEntity().isDead()) {
            if (entityEvent.getDamager() instanceof Player) {
                Player player = (Player) entityEvent.getDamager();
                // TODO implement Mob level
                gainXP (10, player);
            }
        }
    }

    @EventHandler
    public void onEnchant (EnchantItemEvent enchantEvent) {
        Player player = enchantEvent.getEnchanter();
        int xpLevelCost = enchantEvent.getExpLevelCost();
        // TODO set Enchantment xp
        gainXP(xpLevelCost, player);
    }

    /**
     * Creates a player if the player's uuid is not in the database yet.
     * @param uuid  the player's uuid
     */
    private void createPlayer(UUID uuid) {
        try {
            PreparedStatement statement = plugin.getConnection()
                    .prepareStatement("SELECT * FROM " + plugin.tablePlayers + " WHERE UUID=?");
            statement.setString(1, uuid.toString());
            ResultSet results = statement.executeQuery();
            results.next();
            if (!playerExists(uuid)) {
                PreparedStatement insert = plugin.getConnection()
                        .prepareStatement("INSERT INTO " + plugin.tablePlayers
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
            PreparedStatement statement = plugin.getConnection()
                    .prepareStatement("SELECT * FROM " + plugin.tablePlayers + " WHERE UUID=?");
            statement.setString(1, uuid.toString());

            ResultSet results = statement.executeQuery();
            if (results.next()) {
                return true;
            }

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
        // add xp to xp-column and maybe increase level, calls isNextLevel and levelUp
        // get current xp, get current level, get intelligence, get player's uuid as string
            String playerUuid = player.getUniqueId().toString();
            int xp = getValueSQL("xp", playerUuid);
            int level = getValueSQL("level", playerUuid);
            int intelligenceLevel = getValueSQL("intelligenceLevel", playerUuid);
        // skill points in case the player levels up
            int skillPoints = getValueSQL("skillPoints", playerUuid);
        // newXp = formerXP + gainedXp*(intelligence + 1)
        int newXp = xp + gainedXp * (intelligenceLevel + 1);
        // if xp < level requirement, just update xp
        int levelReq = levelRequirement(level);
        if (newXp < levelReq || level >= 100) {
            updateValueSQL("xp", newXp, playerUuid);
        } else {
            int levelUpCounter = 0;
            // if xp >= level requirement, update level, adjust xp then update xp
            while (newXp >= levelRequirement(level)) {
                levelUpCounter ++;
                skillPoints++;
                newXp -= levelRequirement(level);
                level++;
            }
            updateValueSQL("level", level, playerUuid);
            updateValueSQL("skillPoints", skillPoints, playerUuid);
            updateValueSQL("xp", newXp, playerUuid);
            player.playSound(player.getLocation(), Sound.BLOCK_PORTAL_TRAVEL, 0.5f, 1);
            TitleAPI.sendTitle(player,20, 40,20,
                    ChatColor.YELLOW + "Level Up! " + ChatColor.RED + ChatColor.BOLD + level,
                    ChatColor.GREEN + Integer.toString(levelUpCounter) + " " + ChatColor.GRAY + "New Skill Point(s) Available. "
                            + ChatColor.WHITE + "/skills" );

            String levelString = ChatColor.DARK_GRAY + "[" + getLevelColor(level) + "✶" + ChatColor.BOLD + level
                    +  ChatColor.RESET + getLevelColor(level) + "✶"  +  ChatColor.DARK_GRAY + "] " + ChatColor.WHITE + player.getName();
            player.setDisplayName(levelString);
        }

        // newLevelBar(player);
        updateLevelBar(gainedXp * (intelligenceLevel + 1 ), player);
    }

    /**
     * Gets a specific column entry for a player.
     * @param value the name of the column the value is in
     * @param playerUuid    the player's uuid as a String
     * @return  the value of the field
     */
    public int getValueSQL (String value, String playerUuid) {
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
        // UPDATE players SET xp = newXp WHERE uuid = player's uuid
        String command = "UPDATE players SET " + value + " = ? WHERE uuid = ?";
        try {
            PreparedStatement update = plugin.getConnection().prepareStatement(command);
            update.setInt(1, newValue);
            update.setString(2, playerUuid);
            update.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int levelRequirement (int level) {
        //level up logic
        return 100 * (level * level);
    }

    /**
     * Level up by selecting a skill.
     * @param skill the skill that the player selected
     * @param player    the player that selected the skill
     */
    public void skillUp (String skill, Player player) {
        // check skill points > 0, increase specified skill
        String playersName = player.getUniqueId().toString();
        int availableSkillPoints = getValueSQL("skillPoints", playersName);
        if (availableSkillPoints == 0) {
            player.sendMessage(ChatColor.RED + "You don't have any skill points available.");
            return;
        }
        // Player has more than 0 skill points. He can level up.
        switch (skill) {
            case "health":
                int healthLevel = getValueSQL("healthLevel", playersName);
                if (healthLevel >= 20) {
                    player.sendMessage(ChatColor.RED + "Your health is already maxed out!");
                    break;
                }
                updateValueSQL("healthLevel", healthLevel + 1, playersName);
                player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20 + 1 + healthLevel);
                player.sendMessage(ChatColor.DARK_AQUA + "Your health has increased!");
                break;
            case "strength":
                int strengthLevel = getValueSQL("strengthLevel", playersName);
                if (strengthLevel >= 20) {
                    player.sendMessage(ChatColor.RED + "Your strength is already maxed out!");
                    break;
                }
                updateValueSQL("strengthLevel", strengthLevel + 1, playersName);
                player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(1 + (strengthLevel + 1) * 0.5);
                player.sendMessage(ChatColor.DARK_AQUA + "Your strength has increased!");
                updateValueSQL("skillPoints", availableSkillPoints - 1, playersName);
                break;
            case "speed":
                int speedLevel = getValueSQL("speedLevel", playersName);
                if (speedLevel >= 20) {
                    player.sendMessage(ChatColor.RED + "Your speed is already maxed out!");
                    break;
                }
                updateValueSQL("speedLevel", speedLevel + 1, playersName);
                player.sendMessage(ChatColor.DARK_AQUA + "Your speed has increased!");
                player.setWalkSpeed(0.15f+ (speedLevel + 1) * 0.01f);
                updateValueSQL("skillPoints", availableSkillPoints - 1, playersName);
                break;
            case "endurance":
                int enduranceLevel = getValueSQL("enduranceLevel", playersName);
                if (enduranceLevel >= 20) {
                    player.sendMessage(ChatColor.RED + "Your endurance is already maxed out!");
                    break;
                }
                updateValueSQL("enduranceLevel", enduranceLevel + 1, playersName);
                player.sendMessage(ChatColor.DARK_AQUA + "Your endurance has increased!");
                // TODO what should endurance do? :c
                updateValueSQL("skillPoints", availableSkillPoints - 1, playersName);
                break;
            case "intelligence":
                int intelligenceLevel = getValueSQL("intelligenceLevel", playersName);
                if (intelligenceLevel >= 20) {
                    player.sendMessage(ChatColor.RED + "Your intelligence is already maxed out!");
                    break;
                }
                updateValueSQL("intelligenceLevel", intelligenceLevel + 1, playersName);
                player.sendMessage(ChatColor.DARK_AQUA + "Your intelligence has increased!");
                updateValueSQL("skillPoints", availableSkillPoints - 1, playersName);
                break;
            default:
                plugin.getServer().broadcastMessage("Uh-oh something went wrong.");
                break;
        }

        // give them full health
        int playerHealth = 20 + getValueSQL("healthLevel", player.getUniqueId().toString());
        player.setHealth(playerHealth);
    }

    /**
     * Level Bar that activates when a player gains XP, showing how much XP they earned.
     * @param gainedXp  the xp they gained
     * @param player    the player that earned it
     */
    private void updateLevelBar (int gainedXp, Player player) {
        int level = getValueSQL("level", player.getUniqueId().toString());
        int currentXp = getValueSQL("xp", player.getUniqueId().toString());
        int xpToNextLevel = levelRequirement(level);
        String titleForLevelBar = ChatColor.LIGHT_PURPLE + "Level " + ChatColor.WHITE + level +  " | "
                + ChatColor.LIGHT_PURPLE + currentXp + "/" + xpToNextLevel + "EXP";

        if (gainedXp > 0) {
            titleForLevelBar = titleForLevelBar + ChatColor.WHITE + " | "
                    + ChatColor.GREEN + ChatColor.ITALIC + "+" + gainedXp + "EXP";
        }

        levelBar.removeAll();
        BossBar updatedLevelBar = Bukkit.createBossBar(titleForLevelBar, BarColor.PINK, BarStyle.SOLID);
        double xpDouble = (double) currentXp;
        updatedLevelBar.setProgress(xpDouble/xpToNextLevel);
        updatedLevelBar.addPlayer(player);
        levelBar = updatedLevelBar;
    }

    /**
     * Makes a Level Bar for a player without any XP gained.
     * @param player    the player
     * @return          the new Level Bar
     */
    private BossBar newLevelBar (Player player) {
        int level = getValueSQL("level", player.getUniqueId().toString());
        int currentXp = getValueSQL("xp", player.getUniqueId().toString());
        int xpToNextLevel = levelRequirement(level);
        String titleForLevelBar = ChatColor.LIGHT_PURPLE + "Level " + ChatColor.WHITE + level +  " | "
                + ChatColor.LIGHT_PURPLE + currentXp + "/" + xpToNextLevel + "EXP";
        BossBar newBar = Bukkit.createBossBar(titleForLevelBar, BarColor.PINK, BarStyle.SOLID);

        double xpDouble = (double) currentXp;
        newBar.setProgress(xpDouble/xpToNextLevel);
        newBar.addPlayer(player);
        return newBar;
    }

    /**
     * Get Level Color for Player's names.
     * @param level the level the player is
     * @return  the corresponding color
     */
    private ChatColor getLevelColor (int level) {
        if (level < 10) {
            return ChatColor.GRAY;
        } else if (level < 20) {
            return ChatColor.WHITE;
        } else if (level < 30) {
            return ChatColor.GREEN;
        } else if (level < 40) {
            return ChatColor.DARK_GREEN;
        } else if (level < 50) {
            return ChatColor.DARK_AQUA;
        } else if (level < 60) {
            return ChatColor.AQUA;
        } else if (level < 70) {
            return ChatColor.YELLOW;
        } else if (level < 80) {
            return ChatColor.GOLD;
        } else if (level < 90) {
            return ChatColor.LIGHT_PURPLE;
        } else if (level < 100) {
            return ChatColor.RED;
        }
        return ChatColor.DARK_PURPLE;
    }

}
