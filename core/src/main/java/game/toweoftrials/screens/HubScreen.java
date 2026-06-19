package game.toweoftrials.screens;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.VisUI;
import game.toweoftrials.Main;
import game.toweoftrials.utils.AudioManager;

public class HubScreen extends BaseScreen {

    public HubScreen(final Main game) {
        super(game);
        setBackground("background/hub_background.png");
        AudioManager.playMusic("menu");
        
        root.add(new Label("TOWER OF TRIALS", VisUI.getSkin())).pad(20).row();

        Table mainLayout = new Table();
        
        // Left Column: Floors
        Table floorTable = new Table();
        int highest = game.getHighestFloor();
        
        for (int i = 1; i <= 8; i++) {
            final int floor = i;
            TextButton floorButton = createStyledButton("Floor " + i);
            
            if (i > highest) {
                floorButton.setDisabled(true);
                updateButtonFontColor(floorButton);
            }

            floorButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    game.setScreen(new FloorMenuScreen(game, floor));
                }
            });
            floorTable.add(floorButton).width(300).pad(5).row();
        }
        
        // Right Column: Management
        Table managementTable = new Table();
        
        final Label feedbackLabel = new Label("", VisUI.getSkin());
        feedbackLabel.setColor(com.badlogic.gdx.graphics.Color.GREEN);
        
        TextButton saveButton = createStyledButton("SAVE GAME");
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

        TextButton inventoryButton = createStyledButton("INVENTORY");
        inventoryButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new InventoryScreen(game));
            }
        });
        
        TextButton characterButton = createStyledButton("CHARACTER");
        characterButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new CharacterScreen(game));
            }
        });

        TextButton settingsButton = createStyledButton("SETTINGS");
        settingsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                stage.addActor(new SettingsWindow(game));
            }
        });

        TextButton backButton = createStyledButton("BACK TO MENU");
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new StartMenuScreen(game));
            }
        });

        managementTable.add(feedbackLabel).height(20).pad(2).row();
        managementTable.add(saveButton).width(300).pad(5).row();
        managementTable.add(inventoryButton).width(300).pad(5).row();
        managementTable.add(characterButton).width(300).pad(5).row();
        managementTable.add(settingsButton).width(300).pad(5).row();
        managementTable.add(backButton).width(300).pad(20).row();

        mainLayout.add(floorTable).padRight(50);
        mainLayout.add(managementTable).top();
        
        root.add(mainLayout).center();
    }


}
