package game.toweoftrials.model;

import com.badlogic.ashley.core.Entity;

public class Enemy extends GameEntity {
    public enum EnemyType {
        NORMAL, MINI_BOSS, BOSS
    }

    private final EnemyType type;

    public Enemy(Entity entity, EnemyType type) {
        super(entity);
        this.type = type;
    }
}
