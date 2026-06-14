package game.toweoftrials.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.VisUI;
import game.toweoftrials.Main;
import game.toweoftrials.utils.SaveManager;

public class StartMenuScreen extends BaseScreen {

    public StartMenuScreen(final Main game) {
        super(game);

        Label title = new Label("TOWER OF TRIALS", VisUI.getSkin());
        title.setFontScale(2.0f); // Make the main title bigger
        root.add(title).pad(50).row();

        float btnWidth = 350;

        if (SaveManager.hasSave()) {
            TextButton continueButton = createStyledButton("CONTINUE");
            continueButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    game.continueGame();
                }
            });
            root.add(continueButton).width(btnWidth).pad(10).row();
        }

        TextButton newGameButton = createStyledButton("NEW GAME");
        newGameButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.startNewGame();
            }
        });
        root.add(newGameButton).width(btnWidth).pad(10).row();

        TextButton exitButton = createStyledButton("EXIT GAME");
        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });
        root.add(exitButton).width(btnWidth).pad(10).row();
        
        root.add(new Label("C64 Edition - 2026", VisUI.getSkin())).padTop(100);
    }

    private TextButton createStyledButton(String text) {
        TextButton button = new TextButton(text, VisUI.getSkin());
        button.setColor(com.badlogic.gdx.graphics.Color.WHITE); // Ensure background is bright

        // Initial text color setup
        updateButtonFontColor(button);

        button.addListener(new com.badlogic.gdx.scenes.scene2d.InputListener() {
            @Override
            public void enter(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (!button.isDisabled()) button.getLabel().setColor(com.badlogic.gdx.graphics.Color.WHITE);
            }

            @Override
            public void exit(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y, int pointer, Actor toActor) {
                updateButtonFontColor(button);
            }
        });
        return button;
    }

    private void updateButtonFontColor(TextButton button) {
        if (button.isDisabled()) button.getLabel().setColor(com.badlogic.gdx.graphics.Color.GRAY);
        else button.getLabel().setColor(com.badlogic.gdx.graphics.Color.LIGHT_GRAY);
    }
}
