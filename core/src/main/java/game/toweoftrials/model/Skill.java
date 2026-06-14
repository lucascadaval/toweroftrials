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
    private final String description;
    private final String animationName;

    // Advanced Mechanics
    public boolean isAoE = false;
    public boolean ignoreDef = false;
    public boolean lifesteal = false;
    public boolean detonatePoison = false;

    // Status Application
    public game.toweoftrials.ecs.components.StatusComponent.EffectType statusType = null;
    public int statusDuration = 0;
    public float statusValue = 0;

    public Skill(String name, String description, SkillType type, int apCost, int cooldown, float multiplier, String animationName) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.apCost = apCost;
        this.cooldown = cooldown;
        this.multiplier = multiplier;
        this.animationName = animationName;
    }

    public Skill setAoE(boolean aoe) { this.isAoE = aoe; return this; }
    public Skill setIgnoreDef(boolean ignore) { this.ignoreDef = ignore; return this; }
    public Skill setLifesteal(boolean ls) { this.lifesteal = ls; return this; }
    public Skill setDetonate(boolean det) { this.detonatePoison = det; return this; }
    
    public Skill setStatus(game.toweoftrials.ecs.components.StatusComponent.EffectType type, int duration, float value) {
        this.statusType = type;
        this.statusDuration = duration;
        this.statusValue = value;
        return this;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public SkillType getType() { return type; }
    public int getApCost() { return apCost; }
    public int getCooldown() { return cooldown; }
    public float getMultiplier() { return multiplier; }
    public String getAnimationName() { return animationName; }
}
