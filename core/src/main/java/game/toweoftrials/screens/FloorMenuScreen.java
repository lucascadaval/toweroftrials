package game.toweoftrials.screens;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTextButton;
import game.toweoftrials.Main;
import game.toweoftrials.model.Enemy;

public class FloorMenuScreen extends BaseScreen {

    public FloorMenuScreen(Main game, int floor) {
        super(game);

        root.add(new VisLabel("FLOOR " + floor)).pad(20).row();

        VisTextButton farmButton = new VisTextButton("Farm Dungeon");
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

        VisTextButton bossButton = new VisTextButton("Boss Room");
        bossButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new BattleScreen(game, floor, Enemy.EnemyType.BOSS));
            }
        });

        VisTextButton backButton = new VisTextButton("Back to Hub");
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new HubScreen(game));
            }
        });

        root.add(farmButton).width(200).pad(5).row();
        root.add(bossButton).width(200).pad(5).row();
        root.add(backButton).width(200).pad(20).row();
    }
}
