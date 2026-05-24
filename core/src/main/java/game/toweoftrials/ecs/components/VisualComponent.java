package game.toweoftrials.ecs.components;

import com.badlogic.ashley.core.Component;

public class VisualComponent implements Component {
    public String texturePath;
    public float scale = 1.0f;

    public VisualComponent(String texturePath) {
        this.texturePath = texturePath;
    }

    public VisualComponent(String texturePath, float scale) {
        this.texturePath = texturePath;
        this.scale = scale;
    }
}
