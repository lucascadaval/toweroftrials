package game.toweoftrials.ecs.components;

import com.badlogic.ashley.core.Component;

public class BattleComponent implements Component {
    public boolean isPlayer;
    public boolean isDead = false;

    public BattleComponent(boolean isPlayer) {
        this.isPlayer = isPlayer;
    }
}
