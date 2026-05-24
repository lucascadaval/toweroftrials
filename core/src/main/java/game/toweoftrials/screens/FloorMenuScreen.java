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
    private final int floor;

    public FloorMenuScreen(Main game, int floor) {
        super(game);
        this.floor = floor;

        String floorName = floor == 1 ? "SEWER" : "FLOOR " + floor;
        root.add(new Label(floorName, VisUI.getSkin())).pad(20).row();

        TextButton farmButton = createStyledButton("Farm Dungeon");
        farmButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // Wave 1: 3 Slimes, Wave 2: 3 Gremlins
                Array<Array<Enemy.EnemyType>> waves = new Array<>();
                
                Array<Enemy.EnemyType> wave1 = new Array<>();
                wave1.add(Enemy.EnemyType.NORMAL);
                wave1.add(Enemy.EnemyType.NORMAL);
                wave1.add(Enemy.EnemyType.NORMAL);
                
                Array<Enemy.EnemyType> wave2 = new Array<>();
                wave2.add(Enemy.EnemyType.NORMAL); // We'll assume NORMAL is Slime for wave 1 and something else for others?
                wave2.add(Enemy.EnemyType.NORMAL); // Actually, for now, let's keep it simple.
                wave2.add(Enemy.EnemyType.NORMAL);
                
                waves.add(wave1);
                waves.add(wave2);
                
                game.setScreen(new BattleScreen(game, floor, waves));
            }
        });

        TextButton bossButton = createStyledButton("Boss Room");
        bossButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Array<Array<Enemy.EnemyType>> waves = new Array<>();
                Array<Enemy.EnemyType> bossWave = new Array<>();
                bossWave.add(Enemy.EnemyType.BOSS);
                waves.add(bossWave);
                game.setScreen(new BattleScreen(game, floor, waves));
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
