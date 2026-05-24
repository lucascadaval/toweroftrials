package game.toweoftrials.screens;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.VisUI;
import game.toweoftrials.Main;
import game.toweoftrials.model.Enemy;

public class HubScreen extends BaseScreen {

    public HubScreen(Main game) {
        super(game);

        root.add(new Label("TOWER OF TRIALS", VisUI.getSkin())).pad(20).row();

        for (int i = 1; i <= 10; i++) {
            final int floor = i;
            TextButton floorButton = createStyledButton("Floor " + i);
            
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

            root.add(floorButton).width(300).pad(5).row();
        }
    }

    private TextButton createStyledButton(String text) {
        TextButton button = new TextButton(text, VisUI.getSkin());
        button.addListener(new com.badlogic.gdx.scenes.scene2d.InputListener() {
            @Override
            public void enter(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (!button.isDisabled()) button.setColor(com.badlogic.gdx.graphics.Color.LIGHT_GRAY);
            }

            @Override
            public void exit(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y, int pointer, Actor toActor) {
                button.setColor(com.badlogic.gdx.graphics.Color.WHITE);
            }
        });
        return button;
    }
}
