package game.toweoftrials;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.VisUI;
import game.toweoftrials.ecs.HeroManager;
import game.toweoftrials.ecs.components.*;
import game.toweoftrials.model.*;
import game.toweoftrials.screens.HubScreen;
import game.toweoftrials.screens.StartMenuScreen;
import game.toweoftrials.screens.IntroScreen;
import game.toweoftrials.utils.SaveManager;

public class Main extends Game {
    private int highestFloor = 1;
    private final Array<Integer> clearedDungeons = new Array<>();
    private final Array<Integer> clearedBosses = new Array<>();
    private final Array<Integer> metBosses = new Array<>();

    @Override
    public void create () {
        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("Commodore_64_UI_Skin/commodore64ui/uiskin.atlas"));
        Skin skin = new Skin(Gdx.files.internal("Commodore_64_UI_Skin/commodore64ui/uiskin.json"), atlas);
        VisUI.load(skin);

        setScreen(new StartMenuScreen(this));
    }

    public void startNewGame() {
        highestFloor = 1;
        clearedDungeons.clear();
        clearedBosses.clear();
        metBosses.clear();
        SaveManager.deleteSave();
        createNewHero();
        setScreen(new IntroScreen(this));
    }

    public void continueGame() {
        SaveManager.SaveData data = SaveManager.loadGame();
        if (data != null) {
            highestFloor = data.highestFloor;
            clearedDungeons.clear(); clearedDungeons.addAll(data.clearedDungeons);
            clearedBosses.clear(); clearedBosses.addAll(data.clearedBosses);
            metBosses.clear(); metBosses.addAll(data.metBosses);
            loadHeroFromSave(data);
            setScreen(new HubScreen(this));
        } else {
            startNewGame();
        }
    }

    private void createNewHero() {
        Entity hero = new Entity();
        hero.add(new StatsComponent("Hero", 100, 20, 10, 15));
        hero.add(new APComponent(5));
        hero.add(new BattleComponent(true));
        hero.add(new StatusComponent());
        
        AbilitiesComponent abilities = new AbilitiesComponent();
        // Use SkillRegistry
        abilities.skills.add(SkillRegistry.get("Strike"));
        abilities.skills.add(SkillRegistry.get("Heavy Slash"));
        abilities.skills.add(SkillRegistry.get("Second Wind"));
        hero.add(abilities);
        
        hero.add(new LevelComponent());
        hero.add(new VisualComponent("player/img.png"));
        HeroManager.setHero(hero);
    }

    private void loadHeroFromSave(SaveManager.SaveData data) {
        Entity hero = new Entity();
        hero.add(new StatsComponent("Hero", data.maxHp, data.attack, data.defense, data.speed));
        hero.add(new APComponent(5));
        hero.add(new BattleComponent(true));
        hero.add(new StatusComponent());
        
        AbilitiesComponent abilities = new AbilitiesComponent();
        for (String sName : data.unlockedSkillNames) {
            Skill s = SkillRegistry.get(sName);
            if (s != null) abilities.skills.add(s);
        }
        // Fallback if empty
        if (abilities.skills.size == 0) {
            abilities.skills.add(SkillRegistry.get("Strike"));
            abilities.skills.add(SkillRegistry.get("Heavy Slash"));
            abilities.skills.add(SkillRegistry.get("Second Wind"));
        }
        hero.add(abilities);

        LevelComponent lc = new LevelComponent();
        lc.level = data.level;
        lc.currentXp = data.currentXp;
        lc.xpToNextLevel = data.xpToNextLevel;
        hero.add(lc);

        hero.add(new VisualComponent("player/img.png"));
        HeroManager.setHero(hero);
        
        Player player = HeroManager.getPlayer();
        for (String itemName : data.inventoryItemNames) {
            Item item = ItemRegistry.get(itemName);
            if (item != null) player.addItem(item);
        }
        
        for (String itemName : data.equippedItemNames) {
            Item item = ItemRegistry.get(itemName);
            if (item != null) player.equipItem(item);
        }
    }

    public int getHighestFloor() { return highestFloor; }
    public void setHighestFloor(int floor) { 
        if (floor > highestFloor) {
            highestFloor = floor;
            saveGame();
        }
    }

    public boolean isDungeonCleared(int floor) { return clearedDungeons.contains(floor, true); }
    public boolean isBossCleared(int floor) { return clearedBosses.contains(floor, true); }
    public boolean hasMetBoss(int floor) { return metBosses.contains(floor, true); }

    public void markBossMet(int floor) {
        if (!metBosses.contains(floor, true)) {
            metBosses.add(floor);
            saveGame();
        }
    }

    public void markDungeonCleared(int floor) {
        if (!clearedDungeons.contains(floor, true)) {
            clearedDungeons.add(floor);
            saveGame();
        }
    }

    public void markBossCleared(int floor) {
        if (!clearedBosses.contains(floor, true)) {
            clearedBosses.add(floor);
            saveGame();
        }
    }
    
    public void saveGame() {
        SaveManager.saveGame(highestFloor, clearedDungeons, clearedBosses, metBosses);
    }

    @Override
    public void dispose () {
        super.dispose();
        VisUI.dispose();
    }
}
