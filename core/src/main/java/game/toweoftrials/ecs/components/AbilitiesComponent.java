package game.toweoftrials.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.ObjectMap;

public class AbilitiesComponent implements Component {
    // Stores currently active cooldowns: Skill Name -> Remaining Turns
    public ObjectMap<String, Integer> currentCooldowns = new ObjectMap<>();

    public void startCooldown(String name, int turns) {
        if (turns > 0) {
            currentCooldowns.put(name, turns);
        }
    }

    public boolean isReady(String name) {
        return !currentCooldowns.containsKey(name) || currentCooldowns.get(name) <= 0;
    }

    public int getRemainingCooldown(String name) {
        return currentCooldowns.get(name, 0);
    }

    public void updateCooldowns() {
        for (String key : currentCooldowns.keys().toArray()) {
            int remaining = currentCooldowns.get(key);
            if (remaining > 0) {
                currentCooldowns.put(key, remaining - 1);
            }
        }
    }

    public void resetCooldowns() {
        currentCooldowns.clear();
    }
}
