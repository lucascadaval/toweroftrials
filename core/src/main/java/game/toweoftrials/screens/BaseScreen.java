package game.toweoftrials.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import game.toweoftrials.Main;
import game.toweoftrials.utils.AudioManager;

public abstract class BaseScreen implements Screen {
    protected final Main game;
    protected final Stage stage;
    protected final Table root;

    public BaseScreen(Main game) {
        this.game = game;
        this.stage = new Stage(new ScreenViewport());
        this.root = new Table();
        root.setFillParent(true);
        stage.addActor(root);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    protected com.badlogic.gdx.scenes.scene2d.ui.TextButton createStyledButton(String text) {
        final com.badlogic.gdx.scenes.scene2d.ui.TextButton button = new com.badlogic.gdx.scenes.scene2d.ui.TextButton(text, com.kotcrab.vis.ui.VisUI.getSkin());
        button.setColor(com.badlogic.gdx.graphics.Color.WHITE); 

        updateButtonFontColor(button);

        button.addListener(new com.badlogic.gdx.scenes.scene2d.InputListener() {
            @Override
            public void enter(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor fromActor) {
                if (!button.isDisabled() && pointer == -1) {
                    button.getLabel().setColor(com.badlogic.gdx.graphics.Color.WHITE);
                    AudioManager.playSound("hover");
                }
            }

            @Override
            public void exit(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor toActor) {
                updateButtonFontColor(button);
            }
        });

        button.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                if (!button.isDisabled()) {
                    AudioManager.playSound("confirm");
                } else {
                    AudioManager.playSound("denied");
                }
            }
        });
        
        return button;
    }

    protected void updateButtonFontColor(com.badlogic.gdx.scenes.scene2d.ui.TextButton button) {
        if (button.isDisabled()) button.getLabel().setColor(com.badlogic.gdx.graphics.Color.GRAY);
        else button.getLabel().setColor(com.badlogic.gdx.graphics.Color.LIGHT_GRAY);
    }
}
