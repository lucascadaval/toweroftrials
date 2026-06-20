package game.toweoftrials.screens;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.VisUI;
import game.toweoftrials.Main;
import game.toweoftrials.utils.AudioManager;

public class HubScreen extends BaseScreen {

    public HubScreen(final Main game) {
        super(game);
        setBackground("background/hub_background.jpeg");
        AudioManager.playMusic("menu");

//        Label title = new Label("TOWER OF TRIALS", VisUI.getSkin());
//        title.setFontScale(2.0f);
//        root.add(title).pad(50).row();

        Table menuTable = new Table();
        menuTable.setBackground(VisUI.getSkin().getDrawable("window"));
        menuTable.pad(10);

        int highest = game.getHighestFloor();

        ImageTextButton challengeButton = createStyledButton("CHALLENGE");
        challengeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new FloorMenuScreen(game, highest));
            }
        });

        final Label feedbackLabel = new Label("", VisUI.getSkin());
        feedbackLabel.setColor(com.badlogic.gdx.graphics.Color.GREEN);

        ImageTextButton saveButton = createStyledButton("SAVE GAME");
        saveButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.saveGame();
                feedbackLabel.setText("Game Saved!");
                feedbackLabel.addAction(com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence(
                    com.badlogic.gdx.scenes.scene2d.actions.Actions.alpha(1),
                    com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut(2f),
                    com.badlogic.gdx.scenes.scene2d.actions.Actions.run(() -> feedbackLabel.setText(""))
                ));
            }
        });

        ImageTextButton inventoryButton = createStyledButton("INVENTORY");
        inventoryButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new InventoryScreen(game));
            }
        });

        ImageTextButton characterButton = createStyledButton("CHARACTER");
        characterButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new CharacterScreen(game));
            }
        });

        ImageTextButton settingsButton = createStyledButton("SETTINGS");
        settingsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                stage.addActor(new SettingsWindow(game));
            }
        });

        ImageTextButton backButton = createStyledButton("BACK TO MENU");
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new StartMenuScreen(game));
            }
        });

        menuTable.add(feedbackLabel).height(20).pad(2).row();
        menuTable.add(challengeButton).width(350).pad(15).row(); // Make challenge button stand out
        menuTable.add(inventoryButton).width(300).pad(5).row();
        menuTable.add(characterButton).width(300).pad(5).row();
        menuTable.add(saveButton).width(300).pad(5).row();
        menuTable.add(settingsButton).width(300).pad(5).row();
        menuTable.add(backButton).width(300).pad(20).row();

        root.add(menuTable).center();
    }


}
