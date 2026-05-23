package game.toweoftrials;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.VisUI.SkinScale;
import com.badlogic.ashley.core.Entity;
import game.toweoftrials.ecs.HeroManager;
import game.toweoftrials.ecs.components.*;
import game.toweoftrials.screens.HubScreen;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {

    @Override
    public void create () {
        VisUI.setSkipGdxVersionCheck(true);
        VisUI.load(SkinScale.X1);

        // Initialize persistent Hero
        Entity hero = new Entity();
        hero.add(new StatsComponent("Hero", 100, 20, 10, 15));
        hero.add(new APComponent(5));
        hero.add(new BattleComponent(true));
        hero.add(new AbilitiesComponent());
        hero.add(new LevelComponent());
        HeroManager.setHero(hero);

        setScreen(new HubScreen(this));
    }

    @Override
    public void dispose () {
        super.dispose();
        VisUI.dispose();
    }
}
