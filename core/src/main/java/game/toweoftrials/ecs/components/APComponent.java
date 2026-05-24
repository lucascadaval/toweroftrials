package game.toweoftrials.ecs.components;

import com.badlogic.ashley.core.Component;

public class APComponent implements Component {
    public int currentAP;
    public int maxAP = 5;

    public APComponent(int initialAP) {
        this.currentAP = initialAP;
    }
}
