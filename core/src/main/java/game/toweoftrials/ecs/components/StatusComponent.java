package game.toweoftrials.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Array;

public class StatusComponent implements Component {
    public enum EffectType {
        POISON, BURN, STUN, FEAR, HEAL_BLOCK, 
        ATK_BUFF, DEF_BUFF, SPD_BUFF, CRIT_BUFF,
        ATK_DEBUFF, DEF_DEBUFF, SPD_DEBUFF
    }

    public static class StatusEffect {
        public EffectType type;
        public int duration; // Remaining turns
        public float value;  // Intensity (e.g., 0.15f for 15% reduction)
        public String sourceSkill;

        public StatusEffect(EffectType type, int duration, float value, String sourceSkill) {
            this.type = type;
            this.duration = duration;
            this.value = value;
            this.sourceSkill = sourceSkill;
        }
    }

    public Array<StatusEffect> activeEffects = new Array<>();

    public void addEffect(EffectType type, int duration, float value, String source) {
        // Replace existing of same type from same skill or different skill (simple stack/refresh)
        for (int i = 0; i < activeEffects.size; i++) {
            if (activeEffects.get(i).type == type) {
                activeEffects.get(i).duration = Math.max(activeEffects.get(i).duration, duration);
                activeEffects.get(i).value = value;
                return;
            }
        }
        activeEffects.add(new StatusEffect(type, duration, value, source));
    }

    public boolean hasEffect(EffectType type) {
        for (StatusEffect e : activeEffects) if (e.type == type) return true;
        return false;
    }
    
    public float getEffectValue(EffectType type) {
        float total = 0;
        for (StatusEffect e : activeEffects) if (e.type == type) total += e.value;
        return total;
    }
}
