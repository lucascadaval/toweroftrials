package game.toweoftrials.model;

public class Skill {
    public enum SkillType {
        OFFENSIVE, DEFENSIVE, HEAL
    }

    private final String name;
    private final int apCost;
    private final int cooldown;
    private final float multiplier;
    private final SkillType type;
    private final String animationName;

    public Skill(String name, SkillType type, int apCost, int cooldown, float multiplier, String animationName) {
        this.name = name;
        this.type = type;
        this.apCost = apCost;
        this.cooldown = cooldown;
        this.multiplier = multiplier;
        this.animationName = animationName;
    }

    public String getName() { return name; }
    public SkillType getType() { return type; }
    public int getApCost() { return apCost; }
    public int getCooldown() { return cooldown; }
    public float getMultiplier() { return multiplier; }
    public String getAnimationName() { return animationName; }
}
