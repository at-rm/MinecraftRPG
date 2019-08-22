package dev.atarim.xplevel;

import dev.atarim.xplevel.skills.*;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class LevelLogic {

    private int level;
    private int xp;
    private Skill[] skills;
    private int xpBoost;
    private int skillPoints;
    private Player player;

    /**
     * LevelLogic sets player's initial level to 1, xp to 0.
     * @param player    the player whom the level applies to
     */
    public LevelLogic (Player player) {
        level = 1;
        xp = 0;
        Skill[] s = {new HealthSkill(player), new StrengthSkill(player), new SpeedSkill(player),
                    new EnduranceSkill(player), new IntelligenceSkill(player)};
        skills = s;
        skillPoints = 0;
        xpBoost = 1;
        this.player = player;
    }

    /**
     * Triggered by block break, block place, farming, killing mob, enchanting,
     * quests, challenges, dungeons.
     * @param gained    the gained xp
     */
    public void gainXP (int gained) {
        xp = xp + xpBoost * gained;
        if (isNextLevel(xp, level)) {
            levelUp(level, xp);
        }
    }

    /**
     * Level up. Only possible with one level at the moment.
     * @param formerLevel   the level the player currently is
     * @param overflowXP    the xp the player gained to cause him to level up
     */
    private void levelUp (int formerLevel, int overflowXP) {
        level = level + 1;
        setXp(overflowXP - levelRequirement(formerLevel));
        skillPoints ++;
        player.sendMessage(ChatColor.LIGHT_PURPLE + "You are now Level " + level);
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
    }

    /**
     * Checks if player reached the next level by checking if the player's xp he just gained
     * is above the level requirement for the current level.
     * @param plusXP    gained xp
     * @param currentLevel  the player's current level
     * @return  whether the player has reached the threshold for the next level
     */
    private boolean isNextLevel (int plusXP, int currentLevel) {
        return (plusXP >= levelRequirement(currentLevel));
    }

    /**
     * The level requirement for the next level. Calculated for every level by [ TODO ]
     * @param level the level the player is right now
     * @return  the xp requirement to reach the next level
     */
    private int levelRequirement (int level) {
        return 10;
    }


    /**
     * Player levels up a skill by using a skill point. The skill is then being applied.
     * @param skill     the skill they want to level up
     */
    public void skillUp (String skill) {
        if (skillPoints != 0) {
            for (Skill s : skills) {
                if (s.getName().equalsIgnoreCase(skill)) {
                    s.increaseSkillLevel();
                    s.applySkill();
                    skillPoints --;
                    if (s.getName().equals("Intelligence")) {
                       xpBoost = ((IntelligenceSkill) s).getXpBoost();
                    }
                    player.sendMessage(ChatColor.GREEN + "Your " + s.getName() + " is now Level " + s.getSkillLevel() + "!");
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
                    break;
                }
            }
        } else {
            player.sendMessage(ChatColor.RED + "You don't have any skill points available.");
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
        }
    }

    /**
     * Upon death players and mobs drop xp.
     * @return  amount of xp that is dropped
     */
    public int dropXp () {
        return level;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }
}
