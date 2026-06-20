package game.toweoftrials.ecs.components;

import com.badlogic.ashley.core.Component;

public class APComponent implements Component {
    public int currentAP;
    public int maxAP;

    public APComponent(int currentAP, int maxAP) {
        this.currentAP = currentAP;
        this.maxAP = maxAP;
    }
}
