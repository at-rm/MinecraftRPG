package dev.atarim.xplevel;

import org.bukkit.entity.Player;

/**
 * Methods from LevelLogic
 */
public class LevelSQL {

    /*
    private Player player;  // PRIMARY KEY
    private int level;
    private int xp;
    private int skillPoints;
    private int strengthLevel;
    private int healthLevel;
    private int enduranceLevel;
    private int speedLevel;
    private int intelligenceLevel;
     */

    public LevelSQL () {
        // TODO connect database
    }

    public void initializePlayer(Player player) {
        //TODO create new entry for the player
    }

    public void gainXP (int xp) {
        //TODO add xp to xp-column and maybe increase level, calls isNextLevel and levelUp
    }

    private boolean isNextLevel (int level, int xp) {
        //TODO check if level requirement is reached with given xp
        return true;
    }

    private void levelUp (int level, int xp) {
        //TODO increase level, adjust xp, increase skill point
    }

    private int levelRequirement (int level) {
        //TODO level up logic
        return 0;
    }

    public void skillUp (String skill) {
        //TODO check skill points > 0, increase specified skill
    }
    
}
