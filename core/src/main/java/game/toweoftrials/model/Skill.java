package game.toweoftrials.model;

public class Skill {
    public enum SkillType {
        DAMAGE, DEFENSE, SUPPORT
    }

    private final String name;
    private final int apCost;
    private final int cooldown;
    private final float multiplier;
    private final SkillType type;

    public Skill(String name, SkillType type, int apCost, int cooldown, float multiplier) {
        this.name = name;
        this.type = type;
        this.apCost = apCost;
        this.cooldown = cooldown;
        this.multiplier = multiplier;
    }

    public String getName() { return name; }
    public SkillType getType() { return type; }
    public int getApCost() { return apCost; }
    public int getCooldown() { return cooldown; }
    public float getMultiplier() { return multiplier; }
}
