package dev.atarim.xplevel.skills;

import org.bukkit.entity.Player;

public class IntelligenceSkill extends Skill {

    private int xpBoost;

    /**
     * Player initially has no xpBoost.
     * @param player    the player the skill is applied to
     */
    public IntelligenceSkill(Player player) {
        super(player);
        xpBoost = 1;
        name = "Intelligence";
    }

    @Override
    public void applySkill() {
        // in LevelLogic
    }

    @Override
    public void increaseSkillLevel() {
        xpBoost += 1;
        skillLevel ++;
    }

    public int getXpBoost () {
        return xpBoost;
    }
}
