package game.toweoftrials.ecs.components;

import com.badlogic.ashley.core.Component;

public class LevelComponent implements Component {
    public int level = 1;
    public int currentXp = 0;
    public int xpToNextLevel = 100;

    public void addXp(int amount) {
        currentXp += amount;
    }

    public boolean canLevelUp() {
        return currentXp >= xpToNextLevel;
    }

    public void levelUp() {
        level++;
        currentXp -= xpToNextLevel;
        // Exponential XP requirement: 100, 220, 364, 536...
        xpToNextLevel = (int) (xpToNextLevel * 1.2f) + 100;
    }
}
