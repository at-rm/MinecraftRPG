package dev.atarim.xplevel.skills;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

public class StrengthSkill extends Skill{

    private double strength;

    /**
     * Player initially does 1 damage.
     * @param player    the player
     */
    public StrengthSkill(Player player) {
        super(player);
        strength = 1;
        name = "Strength";
    }

    @Override
    public void applySkill() {
        player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(strength);
    }

    @Override
    public void increaseSkillLevel() {
        strength += 0.5;
        skillLevel ++;
    }
}
