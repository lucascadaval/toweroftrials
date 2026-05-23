package game.toweoftrials;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.VisUI.SkinScale;
import game.toweoftrials.screens.HubScreen;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {

    @Override
    public void create () {
        VisUI.setSkipGdxVersionCheck(true);
        VisUI.load(SkinScale.X1);

        setScreen(new HubScreen(this));
    }

    @Override
    public void dispose () {
        super.dispose();
        VisUI.dispose();
    }
}
