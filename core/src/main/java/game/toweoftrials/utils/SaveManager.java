package game.toweoftrials.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import game.toweoftrials.ecs.HeroManager;
import game.toweoftrials.ecs.components.AbilitiesComponent;
import game.toweoftrials.ecs.components.LevelComponent;
import game.toweoftrials.ecs.components.StatsComponent;
import game.toweoftrials.model.Item;
import game.toweoftrials.model.ItemRegistry;
import game.toweoftrials.model.Player;
import game.toweoftrials.model.Skill;

public class SaveManager {
    private static final String PREFS_NAME = "TowerOfTrialsSave";
    private static final String SAVE_KEY = "saveData";
    private static final Json json = new Json();

    public static class SaveData {
        public int level, currentXp, xpToNextLevel;
        public int maxHp, attack, defense, speed;
        public Array<String> inventoryItemNames = new Array<>();
        public Array<String> equippedItemNames = new Array<>();
        public Array<String> unlockedSkillNames = new Array<>();
        public int highestFloor = 1;
        public Array<Integer> clearedDungeons = new Array<>();
        public Array<Integer> clearedBosses = new Array<>();
        public Array<Integer> metBosses = new Array<>();
        public float musicVolume = 0.5f;
        public float soundVolume = 1.0f;
    }

    public static void saveGame(int highestFloor, Array<Integer> clearedDungeons, Array<Integer> clearedBosses, Array<Integer> metBosses, float musicVolume, float soundVolume) {
        SaveData data;
        if (hasSave()) {
            data = loadGame();
            if (data == null) data = new SaveData();
        } else {
            data = new SaveData();
        }

        Player player = HeroManager.getPlayer();
        if (player != null) {
            LevelComponent lc = player.getLevel();
            data.level = lc.level;
            data.currentXp = lc.currentXp;
            data.xpToNextLevel = lc.xpToNextLevel;
            
            StatsComponent sc = player.getStats();
            data.maxHp = sc.maxHp;
            data.attack = sc.attack;
            data.defense = sc.defense;
            data.speed = sc.speed;
            
            data.inventoryItemNames.clear();
            for (Item item : player.getInventory()) {
                data.inventoryItemNames.add(item.getName());
            }
            
            data.equippedItemNames.clear();
            for (Item.ItemType type : Item.ItemType.values()) {
                Item equipped = player.getEquipped(type);
                if (equipped != null) data.equippedItemNames.add(equipped.getName());
            }

            AbilitiesComponent ac = player.getEntity().getComponent(AbilitiesComponent.class);
            if (ac != null) {
                data.unlockedSkillNames.clear();
                for (Skill s : ac.skills) {
                    data.unlockedSkillNames.add(s.getName());
                }
            }
        }
        
        data.highestFloor = highestFloor;
        data.clearedDungeons.addAll(clearedDungeons);
        data.clearedBosses.addAll(clearedBosses);
        data.metBosses.addAll(metBosses);
        data.musicVolume = musicVolume;
        data.soundVolume = soundVolume;

        Preferences prefs = Gdx.app.getPreferences(PREFS_NAME);
        prefs.putString(SAVE_KEY, json.toJson(data));
        prefs.flush();
    }

    public static boolean hasSave() {
        return Gdx.app.getPreferences(PREFS_NAME).contains(SAVE_KEY);
    }

    public static SaveData loadGame() {
        Preferences prefs = Gdx.app.getPreferences(PREFS_NAME);
        String jsonStr = prefs.getString(SAVE_KEY, null);
        if (jsonStr == null) return null;
        return json.fromJson(SaveData.class, jsonStr);
    }
    
    public static void deleteSave() {
        Preferences prefs = Gdx.app.getPreferences(PREFS_NAME);
        prefs.remove(SAVE_KEY);
        prefs.flush();
    }
}
