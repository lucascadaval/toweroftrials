package game.toweoftrials.screens;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.VisUI;
import game.toweoftrials.Main;
import game.toweoftrials.model.Enemy;

public class FloorMenuScreen extends BaseScreen {

    public FloorMenuScreen(Main game, int floor) {
        super(game);

        root.add(new Label("FLOOR " + floor, VisUI.getSkin())).pad(20).row();

        TextButton farmButton = createStyledButton("Farm Dungeon");
        farmButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Array<Enemy.EnemyType> encounters = new Array<>();
                encounters.add(Enemy.EnemyType.NORMAL);
                encounters.add(Enemy.EnemyType.NORMAL);
                encounters.add(Enemy.EnemyType.NORMAL);
                encounters.add(Enemy.EnemyType.MINI_BOSS);
                game.setScreen(new BattleScreen(game, floor, encounters));
            }
        });

        TextButton bossButton = createStyledButton("Boss Room");
        bossButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new BattleScreen(game, floor, Enemy.EnemyType.BOSS));
            }
        });

        TextButton backButton = createStyledButton("Back to Hub");
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new HubScreen(game));
            }
        });

        float btnWidth = 300;
        root.add(farmButton).width(btnWidth).pad(5).row();
        root.add(bossButton).width(btnWidth).pad(5).row();
        root.add(backButton).width(btnWidth).pad(20).row();
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
