package game.toweoftrials.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;
import com.kotcrab.vis.ui.VisUI;
import game.toweoftrials.Main;
import game.toweoftrials.ecs.HeroManager;
import game.toweoftrials.ecs.components.LevelComponent;
import game.toweoftrials.ecs.components.StatsComponent;
import game.toweoftrials.model.Item;
import game.toweoftrials.model.Player;

public class CharacterScreen extends BaseScreen {
    private final Player player;
    private final Array<Texture> loadedTextures = new Array<>();

    public CharacterScreen(Main game) {
        super(game);
        this.player = HeroManager.getPlayer();
        setupUI();
    }

    private void setupUI() {
        root.clear();
        root.setBackground(VisUI.getSkin().getDrawable("window"));

        Label title = new Label("CHARACTER PROFILE", VisUI.getSkin());
        title.setFontScale(1.5f);
        root.add(title).colspan(2).pad(20).row();

        Table mainTable = new Table();
        
        // Left Side: Stats & Level
        Table statsTable = new Table();
        statsTable.setBackground(VisUI.getSkin().getDrawable("window"));
        statsTable.add(new Label("--- PROGRESSION ---", VisUI.getSkin())).pad(10).row();
        
        LevelComponent lc = player.getLevel();
        statsTable.add(new Label("Name: " + player.getName(), VisUI.getSkin())).left().pad(5).row();
        statsTable.add(new Label("Level: " + lc.level, VisUI.getSkin())).left().pad(5).row();
        statsTable.add(new Label("XP: " + lc.currentXp + " / " + lc.xpToNextLevel, VisUI.getSkin())).left().pad(5).row();
        
        statsTable.add(new Label("--- BASE STATS ---", VisUI.getSkin())).pad(10).row();
        StatsComponent sc = player.getStats();
        statsTable.add(new Label("HP: " + sc.hp + " / " + sc.maxHp, VisUI.getSkin())).left().pad(5).row();
        statsTable.add(new Label("Attack: " + sc.attack, VisUI.getSkin())).left().pad(5).row();
        statsTable.add(new Label("Defense: " + sc.defense, VisUI.getSkin())).left().pad(5).row();
        statsTable.add(new Label("Speed: " + sc.speed, VisUI.getSkin())).left().pad(5).row();

        // Right Side: Equipment
        Table equipTable = new Table();
        equipTable.setBackground(VisUI.getSkin().getDrawable("window"));
        equipTable.add(new Label("--- EQUIPMENT ---", VisUI.getSkin())).colspan(2).pad(10).row();

        equipTable.add(createEquipSlot("SWORD", Item.ItemType.SWORD)).pad(10);
        equipTable.add(createEquipSlot("ARMOR", Item.ItemType.ARMOR)).pad(10).row();
        equipTable.add(createEquipSlot("HELMET", Item.ItemType.HELMET)).pad(10);
        equipTable.add(createEquipSlot("SHIELD", Item.ItemType.SHIELD)).pad(10).row();
        equipTable.add(createEquipSlot("RING", Item.ItemType.RING)).pad(10);
        equipTable.add(createEquipSlot("NECKLACE", Item.ItemType.NECKLACE)).pad(10).row();

        mainTable.add(statsTable).grow().pad(10);
        mainTable.add(equipTable).grow().pad(10);
        root.add(mainTable).grow().row();

        TextButton backBtn = createStyledButton("BACK");
        backBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new HubScreen(game));
            }
        });
        root.add(backBtn).width(200).pad(20).colspan(2);
    }

    private Table createEquipSlot(String title, Item.ItemType type) {
        Table slot = new Table();
        slot.add(new Label(title, VisUI.getSkin())).row();
        
        Item item = player.getEquipped(type);
        if (item != null) {
            Texture tex = new Texture(Gdx.files.internal("items/" + getFolder(type) + "/" + item.getIconPath()));
            loadedTextures.add(tex);
            Image img = new Image(tex);
            img.setScaling(Scaling.fit);
            slot.add(img).size(64).pad(5).row();
            Label name = new Label(item.getName(), VisUI.getSkin());
            name.setFontScale(0.8f);
            slot.add(name);
        } else {
            Image placeholder = new Image(VisUI.getSkin().getDrawable("white"));
            placeholder.setColor(Color.DARK_GRAY);
            slot.add(placeholder).size(64).pad(5).row();
            slot.add(new Label("None", VisUI.getSkin()));
        }
        return slot;
    }

    private String getFolder(Item.ItemType type) {
        switch (type) {
            case SWORD: return "swords";
            case SHIELD: return "shields";
            case HELMET: return "helmets";
            case ARMOR: return "armors";
            case RING: return "rings";
            case NECKLACE: return "necklaces";
            default: return "";
        }
    }



    @Override
    public void dispose() {
        super.dispose();
        for (Texture t : loadedTextures) t.dispose();
    }
}
