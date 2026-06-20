package game.toweoftrials.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.VisUI;
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

    protected void setBackground(String texturePath) {
        Texture bgTexture = new Texture(Gdx.files.internal(texturePath));
        Image bgImage = new Image(bgTexture);
        bgImage.setScaling(Scaling.fill);
        bgImage.setFillParent(true);
        stage.addActor(bgImage);
        bgImage.setZIndex(0);
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

    protected ImageTextButton createStyledButton(String text) {
        final ImageTextButton button = new ImageTextButton(text, VisUI.getSkin());
        button.setColor(Color.WHITE);

        updateButtonFontColor(button);

        button.addListener(new InputListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (!button.isDisabled() && pointer == -1) {
                    button.getLabel().setColor(Color.WHITE);
                    AudioManager.playSound("hover");
                }
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                updateButtonFontColor(button);
            }
        });

        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!button.isDisabled()) {
                    AudioManager.playSound("confirm");
                } else {
                    AudioManager.playSound("denied");
                }
            }
        });

        return button;
    }

    protected void updateButtonFontColor(ImageTextButton button) {
        if (button.isDisabled()) button.getLabel().setColor(Color.GRAY);
        else button.getLabel().setColor(Color.LIGHT_GRAY);
    }
}
