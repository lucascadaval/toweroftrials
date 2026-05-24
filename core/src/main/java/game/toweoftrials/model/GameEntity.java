package game.toweoftrials.model;

import com.badlogic.ashley.core.Entity;
import game.toweoftrials.ecs.components.*;

/**
 * Base wrapper for Ashley Entities to provide a structured domain model.
 */
public abstract class GameEntity {
    protected final Entity entity;

    public GameEntity(Entity entity) {
        this.entity = entity;
    }

    public Entity getEntity() {
        return entity;
    }

    public StatsComponent getStats() {
        return entity.getComponent(StatsComponent.class);
    }

    public APComponent getAP() {
        return entity.getComponent(APComponent.class);
    }

    public AbilitiesComponent getAbilities() {
        return entity.getComponent(AbilitiesComponent.class);
    }
    
    public String getName() {
        return getStats().name;
    }
}
