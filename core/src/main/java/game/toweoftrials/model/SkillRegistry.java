package game.toweoftrials.model;

import com.badlogic.gdx.utils.ObjectMap;
import game.toweoftrials.ecs.components.StatusComponent;

public class SkillRegistry {
    private static final ObjectMap<String, Skill> skills = new ObjectMap<>();

    static {
        // Starting Skills
        reg(new Skill("Strike", "A reliable basic attack dealing 100% damage.", Skill.SkillType.OFFENSIVE, 1, 0, 1.0f, "impactvfx"));
        reg(new Skill("Heavy Slash", "A powerful overhead swing dealing 180% damage.", Skill.SkillType.OFFENSIVE, 2, 2, 1.8f, "impactvfx"));
        reg(new Skill("Second Wind", "Focus your breathing to regain strength. (+50% Def as Shield)", Skill.SkillType.DEFENSIVE, 2, 3, 0.5f, "impactvfx"));

        // Floor 1: Sewer
        reg(new Skill("Slime Skin", "Reduces all direct damage taken by 15% for 3 turns.", Skill.SkillType.DEFENSIVE, 1, 3, 0.15f, "impactvfx")
            .setStatus(StatusComponent.EffectType.DEF_BUFF, 3, 0.15f));
        
        reg(new Skill("Acid Spit", "Applies Poison for 3 turns and reduces target Speed by 20%.", Skill.SkillType.OFFENSIVE, 2, 3, 0.8f, "impactvfx")
            .setStatus(StatusComponent.EffectType.POISON, 3, 0.05f)); // 5% MaxHP damage per turn

        // Floor 2: The Unseen
        reg(new Skill("Hidden Vision", "Increases Critical Hit Rate by 25% for 2 turns.", Skill.SkillType.DEFENSIVE, 1, 3, 0.25f, "impactvfx")
            .setStatus(StatusComponent.EffectType.CRIT_BUFF, 2, 0.25f));

        reg(new Skill("Abyssal Gaze", "Stuns a single target for 1 turn and reduces their Defense by 15%.", Skill.SkillType.OFFENSIVE, 3, 4, 0.5f, "impactvfx")
            .setStatus(StatusComponent.EffectType.STUN, 1, 0));

        // Floor 3: Dead Sea
        reg(new Skill("Abyssal Shell", "Creates a shield based on Max HP for 3 turns. Reflects 10% damage if broken.", Skill.SkillType.DEFENSIVE, 2, 4, 0.2f, "impactvfx"));
        
        reg(new Skill("Crushing Tentacles", "Physical damage to all enemies with a 30% chance to delay their turn.", Skill.SkillType.OFFENSIVE, 3, 3, 1.2f, "impactvfx")
            .setAoE(true));

        // Floor 4: Forgotten Library
        reg(new Skill("Incandescent Pages", "Fire magic damage to up to 3 targets. Applies Burn.", Skill.SkillType.OFFENSIVE, 3, 2, 1.0f, "impactvfx")
            .setAoE(true).setStatus(StatusComponent.EffectType.BURN, 2, 0.08f));

        reg(new Skill("Plague Miasma", "AoE debuff for 4 turns. Reduces enemy Attack by 30%.", Skill.SkillType.OFFENSIVE, 2, 5, 0.3f, "impactvfx")
            .setAoE(true).setStatus(StatusComponent.EffectType.ATK_DEBUFF, 4, 0.3f));

        // Floor 5: Jungle
        reg(new Skill("Lethal Stinger", "High physical pierce damage. Consumes Poison to deal double damage.", Skill.SkillType.OFFENSIVE, 2, 1, 1.5f, "impactvfx")
            .setIgnoreDef(true).setDetonate(true));

        reg(new Skill("Primal Fury", "+40% Attack, but -15% Defense for 3 turns.", Skill.SkillType.DEFENSIVE, 2, 5, 0.4f, "impactvfx")
            .setStatus(StatusComponent.EffectType.ATK_BUFF, 3, 0.4f));

        // Floor 6: Swamp
        reg(new Skill("Cold-Blooded Stance", "Heals 10% Max HP for 3 turns.", Skill.SkillType.HEAL, 2, 4, 0.1f, "impactvfx"));

        reg(new Skill("Shogun's Shadow Slash", "Single target attack that ignores 50% of enemy Defense.", Skill.SkillType.OFFENSIVE, 2, 0, 1.3f, "impactvfx")
            .setIgnoreDef(true));

        // Floor 7: Abandoned Mansion
        reg(new Skill("Phantasmal Howl", "AoE Fear for 1 turn. Enemies might miss.", Skill.SkillType.OFFENSIVE, 3, 4, 0.5f, "impactvfx")
            .setAoE(true).setStatus(StatusComponent.EffectType.FEAR, 1, 0.5f));

        reg(new Skill("Ectoplasmic Drain", "Heavy magic damage. 100% lifesteal.", Skill.SkillType.OFFENSIVE, 3, 3, 1.5f, "impactvfx")
            .setLifesteal(true));

        // Floor 8: Undead Cemetery
        reg(new Skill("Bone Prison", "Traps an enemy for 2 turns.", Skill.SkillType.OFFENSIVE, 3, 5, 0.8f, "impactvfx")
            .setStatus(StatusComponent.EffectType.STUN, 2, 0));

        reg(new Skill("Soul Reaper", "Massive AoE Dark damage. Extra Turn on kill.", Skill.SkillType.OFFENSIVE, 5, 6, 2.5f, "impactvfx")
            .setAoE(true));
    }

    private static void reg(Skill skill) {
        skills.put(skill.getName(), skill);
    }

    public static Skill get(String name) {
        return skills.get(name);
    }
}
