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

        String floorName = getFloorName(floor);
        root.add(new Label(floorName, VisUI.getSkin())).pad(20).row();

        TextButton farmButton = createStyledButton("Farm Dungeon");
        farmButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new BattleScreen(game, FloorMenuScreen.this.floor, getDungeonWaves(FloorMenuScreen.this.floor)));
            }
        });

        TextButton bossButton = createStyledButton("Boss Room");
        bossButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new BattleScreen(game, FloorMenuScreen.this.floor, getBossWave(FloorMenuScreen.this.floor)));
            }
        });

        TextButton backButton = createStyledButton("Back to Tower");
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

    private String getFloorName(int floor) {
        switch (floor) {
            case 1: return "1. SEWER";
            case 2: return "2. THE UNSEEN";
            case 3: return "3. DEAD SEA";
            case 4: return "4. FORGOTTEN LIBRARY";
            case 5: return "5. JUNGLE";
            case 6: return "6. SWAMP";
            case 7: return "7. ABANDONED MANSION";
            case 8: return "8. UNDEAD CEMETERY";
            default: return "FLOOR " + floor;
        }
    }

    private Array<Array<String>> getDungeonWaves(int floor) {
        Array<Array<String>> waves = new Array<>();
        switch (floor) {
            case 1:
                waves.add(createWave("slime", "slime", "slime"));
                waves.add(createWave("gremlin", "gremlin", "gremlin"));
                break;
            case 2:
                waves.add(createWave("3eye_head", "3eye_head", "3eye_head"));
                waves.add(createWave("1eye_demonic_angel", "1eye_demonic_angel", "1eye_demonic_angel"));
                waves.add(createWave("5eye_head", "5eye_head", "5eye_head"));
                break;
            case 3:
                waves.add(createWave("lobster", "lobster", "lobster"));
                waves.add(createWave("crab", "crab", "crab"));
                waves.add(createWave("tentacle_warrior", "tentacle_warrior", "tentacle_warrior"));
                break;
            case 4:
                waves.add(createWave("fiery_book", "fiery_book", "fiery_book"));
                waves.add(createWave("frozen_book", "frozen_book", "frozen_book"));
                waves.add(createWave("flower", "flower", "flower"));
                break;
            case 5:
                waves.add(createWave("snake", "snake", "snake"));
                waves.add(createWave("killer_bee", "killer_bee", "killer_bee"));
                waves.add(createWave("spider", "spider", "spider"));
                break;
            case 6:
                waves.add(createWave("alien", "alien", "alien"));
                waves.add(createWave("mammoth", "mammoth", "mammoth"));
                waves.add(createWave("reptile_samurai", "reptile_samurai", "reptile_samurai"));
                break;
            case 7:
                waves.add(createWave("lion_statue", "lion_statue", "lion_statue"));
                waves.add(createWave("tiger", "tiger", "tiger"));
                waves.add(createWave("wolf", "wolf", "wolf"));
                break;
            case 8:
                waves.add(createWave("undead_human", "undead_human", "undead_human"));
                waves.add(createWave("skeletal_warrior", "skeletal_warrior", "skeletal_warrior"));
                waves.add(createWave("skeletal_worm", "skeletal_worm", "skeletal_worm"));
                waves.add(createWave("undead_knight"));
                break;
        }
        return waves;
    }

    private Array<Array<String>> getBossWave(int floor) {
        Array<Array<String>> waves = new Array<>();
        switch (floor) {
            case 1: waves.add(createWave("3head_slug")); break;
            case 2: waves.add(createWave("3eye_monster")); break;
            case 3: waves.add(createWave("octopus_summoner")); break;
            case 4: waves.add(createWave("plague_doctor")); break;
            case 5: waves.add(createWave("ogre")); break;
            case 6: waves.add(createWave("reptile_warrior")); break;
            case 7: waves.add(createWave("undead_mage")); break;
            case 8: waves.add(createWave("lich")); break;
        }
        return waves;
    }

    private Array<String> createWave(String... ids) {
        Array<String> wave = new Array<>();
        for (String id : ids) wave.add(id);
        return wave;
    }


}
