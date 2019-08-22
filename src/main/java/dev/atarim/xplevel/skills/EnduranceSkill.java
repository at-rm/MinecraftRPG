package dev.atarim.xplevel.skills;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

public class EnduranceSkill extends Skill {

    private double endurance;

    /**
     * Increases Player's armor toughness?
     * @param player    the player the skill is applied to
     */
    public EnduranceSkill(Player player) {
        super(player);
        endurance = 0;
        name = "Endurance";
    }

    @Override
    public void applySkill() {
        player.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS).setBaseValue(endurance);
    }

    @Override
    public void increaseSkillLevel() {
        endurance += 0.1;
        skillLevel ++;
    }
}
