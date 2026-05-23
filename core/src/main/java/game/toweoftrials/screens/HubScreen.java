package game.toweoftrials.screens;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTextButton;
import game.toweoftrials.Main;

public class HubScreen extends BaseScreen {

    public HubScreen(Main game) {
        super(game);

        root.add(new VisLabel("TOWER OF TRIALS")).pad(20).row();

        for (int i = 1; i <= 10; i++) {
            final int floor = i;
            VisTextButton floorButton = new VisTextButton("Floor " + i);
            
            // For MVP, only Floor 1 is unlocked initially
            if (i > 1) {
                floorButton.setDisabled(true);
                floorButton.setText("Floor " + i + " (Locked)");
            }

            floorButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    game.setScreen(new FloorMenuScreen(game, floor));
                }
            });

            root.add(floorButton).width(200).pad(5).row();
        }
    }
}
