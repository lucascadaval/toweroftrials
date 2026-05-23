package game.toweoftrials.model;

public class Skill {
    public enum SkillType {
        DAMAGE, DEFENSE, SUPPORT
    }

    private String name;
    private int apCost;
    private int cooldown;
    private float multiplier;
    private SkillType type;

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
