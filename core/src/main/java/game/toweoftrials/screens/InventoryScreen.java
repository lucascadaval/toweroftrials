package game.toweoftrials.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;
import com.kotcrab.vis.ui.VisUI;
import game.toweoftrials.Main;
import game.toweoftrials.utils.AudioManager;
import game.toweoftrials.ecs.HeroManager;
import game.toweoftrials.model.Item;
import game.toweoftrials.model.Player;

import java.util.Comparator;

public class InventoryScreen extends BaseScreen {
    private final Player player;
    private Table listTable;
    private Table detailArea;
    private Item.ItemType currentFilter = null; // null means "All"
    private final Array<Texture> loadedTextures = new Array<>();

    public InventoryScreen(Main game) {
        super(game);
        this.player = HeroManager.getPlayer();
        setupUI();
    }

    private void setupUI() {
        root.clear();
        root.setBackground(VisUI.getSkin().getDrawable("window"));

        Label title = new Label("INVENTORY", VisUI.getSkin());
        title.setFontScale(1.5f);
        root.add(title).colspan(2).pad(20).row();

        // Filters
        Table filters = new Table();
        filters.add(createFilterButton("ALL", null)).pad(5);
        filters.add(createFilterButton("SWORDS", Item.ItemType.SWORD)).pad(5);
        filters.add(createFilterButton("SHIELDS", Item.ItemType.SHIELD)).pad(5);
        filters.add(createFilterButton("HELMETS", Item.ItemType.HELMET)).pad(5);
        filters.add(createFilterButton("ARMORS", Item.ItemType.ARMOR)).pad(5);
        filters.add(createFilterButton("RINGS", Item.ItemType.RING)).pad(5);
        filters.add(createFilterButton("NECKLACES", Item.ItemType.NECKLACE)).pad(5);
        root.add(filters).colspan(2).row();

        // Main Layout
        Table main = new Table();
        listTable = new Table();
        ScrollPane scroll = new ScrollPane(listTable, VisUI.getSkin());
        scroll.setFadeScrollBars(false);
        
        detailArea = new Table();
        detailArea.setBackground(VisUI.getSkin().getDrawable("window"));
        
        // Rebalanced widths: more for list, less for detail to avoid horizontal scroll
        main.add(scroll).width(550).growY().pad(10);
        main.add(detailArea).grow().pad(10);
        
        root.add(main).grow().row();

        ImageTextButton back = createStyledButton("BACK");
        back.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new HubScreen(game));
            }
        });
        root.add(back).width(200).pad(20).colspan(2);

        refreshList();
    }

    private ImageTextButton createFilterButton(String text, final Item.ItemType type) {
        ImageTextButton btn = createStyledButton(text);
        btn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                currentFilter = type;
                refreshList();
            }
        });
        return btn;
    }

    private void refreshList() {
        listTable.clear();
        Array<Item> items = new Array<>(player.getInventory());
        
        // Filter
        if (currentFilter != null) {
            for (int i = items.size - 1; i >= 0; i--) {
                if (items.get(i).getType() != currentFilter) items.removeIndex(i);
            }
        }
        
        // Sort
        items.sort(Comparator.comparing(Item::getName));

        for (final Item item : items) {
            Table row = new Table();
            row.setTouchable(com.badlogic.gdx.scenes.scene2d.Touchable.enabled);
            
            // Icon
            Texture tex = new Texture(Gdx.files.internal(item.getFullIconPath()));
            loadedTextures.add(tex);
            Image icon = new Image(tex);
            icon.setScaling(Scaling.fit);
            row.add(icon).size(64).pad(5).left();
            
            Label nameLabel = new Label(item.getName(), VisUI.getSkin());
            if (player.getEquipped(item.getType()) == item) {
                nameLabel.setText(item.getName() + " (E)");
                nameLabel.setColor(Color.GREEN);
            }
            row.add(nameLabel).width(250).left().padLeft(10);
            
            row.addListener(new com.badlogic.gdx.scenes.scene2d.InputListener() {
                @Override
                public boolean touchDown(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y, int pointer, int button) {
                    showDetails(item);
                    return true;
                }
            });
            
            listTable.add(row).pad(5).row();
        }
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

    private void showDetails(final Item item) {
        detailArea.clear();
        
        Texture tex = new Texture(Gdx.files.internal(item.getFullIconPath()));
        loadedTextures.add(tex);
        Image icon = new Image(tex);
        icon.setScaling(Scaling.fit);
        detailArea.add(icon).size(96).pad(15).row();
        
        detailArea.add(new Label(item.getName(), VisUI.getSkin())).pad(5).row();
        Label desc = new Label(item.getDescription(), VisUI.getSkin());
        desc.setWrap(true);
        desc.setAlignment(Align.center);
        detailArea.add(desc).width(240).pad(10).row();
        
        // Stats
        Table stats = new Table();
        Item eq = player.getEquipped(item.getType());
        int eqHp = eq != null ? eq.hpBonus : 0;
        int eqAtk = eq != null ? eq.attackBonus : 0;
        int eqDef = eq != null ? eq.defenseBonus : 0;
        int eqSpd = eq != null ? eq.speedBonus : 0;
        
        addStatLabel(stats, "HP", item.hpBonus, eqHp);
        addStatLabel(stats, "ATK", item.attackBonus, eqAtk);
        addStatLabel(stats, "DEF", item.defenseBonus, eqDef);
        addStatLabel(stats, "SPD", item.speedBonus, eqSpd);
        
        detailArea.add(stats).pad(10).row();

        final boolean isEquipped = player.getEquipped(item.getType()) == item;
        ImageTextButton actionBtn = createStyledButton(isEquipped ? "UNEQUIP" : "EQUIP");
        actionBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (isEquipped) {
                    player.unequipItem(item.getType());
                    AudioManager.playSound("unequip");
                } else {
                    player.equipItem(item);
                    AudioManager.playSound("equip");
                }
                refreshList();
                showDetails(item);
            }
        });
        detailArea.add(actionBtn).width(200).pad(20);
    }

    private void addStatLabel(Table table, String name, int val, int equippedVal) {
        if (val == 0 && equippedVal == 0) return;
        
        Table row = new Table();
        String valSign = val > 0 ? "+" : "";
        Label baseLabel = new Label(name + ": " + valSign + val, VisUI.getSkin());
        row.add(baseLabel).padRight(10);
        
        int diff = val - equippedVal;
        if (diff != 0) {
            String sign = diff > 0 ? "+" : "";
            Label diffLabel = new Label("(" + sign + diff + ")", VisUI.getSkin());
            diffLabel.setColor(diff > 0 ? Color.GREEN : Color.RED);
            row.add(diffLabel);
        }
        
        table.add(row).pad(5).row();
    }

    @Override
    public void dispose() {
        super.dispose();
        for (Texture t : loadedTextures) t.dispose();
    }
}
