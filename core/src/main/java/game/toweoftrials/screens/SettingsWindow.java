package game.toweoftrials.screens;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.VisUI;
import game.toweoftrials.utils.AudioManager;
import game.toweoftrials.Main;

public class SettingsWindow extends Window {
    private Label floatingTitle;

    public SettingsWindow(final Main game) {
        super("", VisUI.getSkin()); // Empty title so it doesn't draw inside the window
        
        setModal(true);
        setMovable(true);

        floatingTitle = new Label("SETTINGS", VisUI.getSkin());
        floatingTitle.setAlignment(Align.center);
        
        Label musicLabel = new Label("Music Volume", VisUI.getSkin());
        final Slider musicSlider = new Slider(0f, 1f, 0.05f, false, VisUI.getSkin());
        musicSlider.setValue(AudioManager.getMusicVolume());
        
        final Label musicValLabel = new Label(String.valueOf((int)(AudioManager.getMusicVolume() * 100)) + "%", VisUI.getSkin());
        musicSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                AudioManager.setMusicVolume(musicSlider.getValue());
                musicValLabel.setText(String.valueOf((int)(musicSlider.getValue() * 100)) + "%");
                game.saveGame();
            }
        });
        
        Label soundLabel = new Label("Sound Volume", VisUI.getSkin());
        final Slider soundSlider = new Slider(0f, 1f, 0.05f, false, VisUI.getSkin());
        soundSlider.setValue(AudioManager.getSoundVolume());
        
        final Label soundValLabel = new Label(String.valueOf((int)(AudioManager.getSoundVolume() * 100)) + "%", VisUI.getSkin());
        soundSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                AudioManager.setSoundVolume(soundSlider.getValue());
                soundValLabel.setText(String.valueOf((int)(soundSlider.getValue() * 100)) + "%");
                game.saveGame();
            }
        });
        
        add(musicLabel).pad(10);
        add(musicSlider).pad(10);
        add(musicValLabel).width(45).pad(10).row();
        
        add(soundLabel).pad(10);
        add(soundSlider).pad(10);
        add(soundValLabel).width(45).pad(10).row();
        
        ImageTextButton closeBtn = new ImageTextButton("Close", VisUI.getSkin());
        closeBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                AudioManager.playSound("confirm");
                remove(); // Close the window
            }
        });
        
        // Add hover sound for close button
        closeBtn.addListener(new com.badlogic.gdx.scenes.scene2d.InputListener() {
            @Override
            public void enter(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (!closeBtn.isDisabled() && pointer == -1) {
                    AudioManager.playSound("hover");
                }
            }
        });

        add(closeBtn).colspan(3).pad(20);
        
        pack();
        
        // Center window on screen
        setPosition(
            (com.badlogic.gdx.Gdx.graphics.getWidth() - getWidth()) / 2f,
            (com.badlogic.gdx.Gdx.graphics.getHeight() - getHeight()) / 2f
        );
    }

    @Override
    protected void setStage(Stage stage) {
        super.setStage(stage);
        if (stage != null) {
            stage.addActor(floatingTitle);
            updateTitlePosition();
        } else {
            floatingTitle.remove();
        }
    }
    
    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x, y);
        updateTitlePosition();
    }

    private void updateTitlePosition() {
        if (floatingTitle != null) {
            floatingTitle.setPosition(getX() + getWidth() / 2f - floatingTitle.getWidth() / 2f, getY() + getHeight() + 15f);
        }
    }
}
