package game.toweoftrials;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.ashley.core.Entity;
import com.kotcrab.vis.ui.VisUI;
import game.toweoftrials.ecs.HeroManager;
import game.toweoftrials.ecs.components.*;
import game.toweoftrials.screens.StartMenuScreen;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {

    @Override
    public void create () {
        // Load custom Commodore 64 skin
        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("Commodore_64_UI_Skin/commodore64ui/uiskin.atlas"));
        Skin skin = new Skin(Gdx.files.internal("Commodore_64_UI_Skin/commodore64ui/uiskin.json"), atlas);
        VisUI.load(skin);

        // Initialize persistent Hero
        Entity hero = new Entity();
        hero.add(new StatsComponent("Hero", 100, 20, 10, 15));
        hero.add(new APComponent(5));
        hero.add(new BattleComponent(true));
        hero.add(new AbilitiesComponent());
        hero.add(new LevelComponent());
        hero.add(new VisualComponent("player/hero_player.png"));
        HeroManager.setHero(hero);

        setScreen(new StartMenuScreen(this));
    }


    @Override
    public void dispose () {
        super.dispose();
        VisUI.dispose();
    }
}
