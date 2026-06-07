package game.toweoftrials.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;

public class CustomAtlas {
    private final Texture texture;
    private final ObjectMap<String, TextureRegion> regions = new ObjectMap<>();

    public CustomAtlas(String texturePath, String jsonPath) {
        this.texture = new Texture(Gdx.files.internal(texturePath));
        this.texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        
        JsonReader reader = new JsonReader();
        JsonValue root = reader.parse(Gdx.files.internal(jsonPath));
        JsonValue frames = root.get("frames");

        for (JsonValue frameData : frames) {
            String name = frameData.name;
            JsonValue f = frameData.get("frame");
            int x = f.getInt("x");
            int y = f.getInt("y");
            int w = f.getInt("w");
            int h = f.getInt("h");
            regions.put(name, new TextureRegion(texture, x, y, w, h));
            Gdx.app.log("CustomAtlas", "Loaded region: " + name + " [" + x + "," + y + "," + w + "x" + h + "]");
        }
        Gdx.app.log("CustomAtlas", "Total regions loaded: " + regions.size);
    }

    public TextureRegion findRegion(String name) {
        if (name == null) return null;
        
        // Try exact match
        TextureRegion region = regions.get(name);
        if (region != null) return region;
        
        // Try stripping extension
        String stripped = name.contains(".") ? name.substring(0, name.lastIndexOf('.')) : name;
        region = regions.get(stripped);
        if (region != null) return region;
        
        // Try adding .png (common in some JSON formats)
        region = regions.get(stripped + ".png");
        if (region != null) return region;

        Gdx.app.error("CustomAtlas", "Region not found: " + name);
        return null;
    }

    public void dispose() {
        texture.dispose();
    }
}
