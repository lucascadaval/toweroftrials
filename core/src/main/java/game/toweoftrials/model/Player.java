package game.toweoftrials.model;

import com.badlogic.ashley.core.Entity;
import game.toweoftrials.ecs.components.*;

public class Player extends GameEntity {
    
    public Player(Entity entity) {
        super(entity);
    }

    public LevelComponent getLevel() {
        return entity.getComponent(LevelComponent.class);
    }

    public void healFull() {
        StatsComponent stats = getStats();
        stats.hp = stats.maxHp;
        stats.shield = 0;
    }
}
