package dev.atarim.xplevel.skills;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

public class SpeedSkill extends Skill {

    private double movementSpeed;
    private double attackSpeed;

    /**
     * Player initially has 0.1 movement speed and 4.0 attack speed.
     * @param player    the player
     */
    public SpeedSkill(Player player) {
        super(player);
        movementSpeed = 0.1;
        attackSpeed = 4.0;
        name = "Speed";
    }

    @Override
    public void applySkill() {
        player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(movementSpeed);
        player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(attackSpeed);
    }

    @Override
    public void increaseSkillLevel() {
        movementSpeed *= 1.1;
        attackSpeed += 0.5;
        skillLevel ++;
    }
}
