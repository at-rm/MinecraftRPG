package dev.atarim.xplevel.skills;

import org.bukkit.entity.Player;

public abstract class Skill {

    protected int skillLevel;
    protected Player player;
    protected String name;

    public Skill (Player player) {
        skillLevel = 0;
        this.player = player;
    }

    public abstract void applySkill();
    public abstract void increaseSkillLevel();
    public String getName () {
        return name;
    }

    public int getSkillLevel() {
        return skillLevel;
    }

}
