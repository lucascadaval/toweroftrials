package game.toweoftrials.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.VisUI;
import game.toweoftrials.Main;

public class StartMenuScreen extends BaseScreen {

    public StartMenuScreen(Main game) {
        super(game);

        Label title = new Label("TOWER OF TRIALS", VisUI.getSkin());
        title.setFontScale(2.0f); // Make the main title bigger
        root.add(title).pad(50).row();

        TextButton playButton = createStyledButton("START GAME");
        playButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new HubScreen(game));
            }
        });

        TextButton exitButton = createStyledButton("EXIT GAME");
        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });

        float btnWidth = 350;
        root.add(playButton).width(btnWidth).pad(10).row();
        root.add(exitButton).width(btnWidth).pad(10).row();
        
        root.add(new Label("C64 Edition - 2026", VisUI.getSkin())).padTop(100);
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
