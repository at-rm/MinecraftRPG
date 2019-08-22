package dev.atarim.xplevel.skills;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

public class HealthSkill extends Skill{

    private double health;

    /**
     * Player initially has 20 hearts.
     * @param player    the player
     */
    public HealthSkill(Player player) {
        super(player);
        health = 20;
        name = "Health";
    }

    @Override
    public void applySkill() {
        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(health);
    }

    @Override
    public void increaseSkillLevel() {
        health ++;
        skillLevel ++;
    }
}
