package game.toweoftrials.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;
import com.kotcrab.vis.ui.VisUI;
import game.toweoftrials.Main;
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
        
        main.add(scroll).width(400).growY().pad(10);
        main.add(detailArea).grow().pad(10);
        
        root.add(main).grow().row();

        TextButton back = createStyledButton("BACK");
        back.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new HubScreen(game));
            }
        });
        root.add(back).width(200).pad(20).colspan(2);

        refreshList();
    }

    private TextButton createFilterButton(String text, final Item.ItemType type) {
        TextButton btn = createStyledButton(text);
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
            Texture tex = new Texture(Gdx.files.internal("items/" + getFolder(item.getType()) + "/" + item.getIconPath()));
            loadedTextures.add(tex);
            Image icon = new Image(tex);
            icon.setScaling(Scaling.fit);
            row.add(icon).size(32).pad(5);
            
            Label nameLabel = new Label(item.getName(), VisUI.getSkin());
            if (player.getEquipped(item.getType()) == item) {
                nameLabel.setText(item.getName() + " (E)");
                nameLabel.setColor(Color.GREEN);
            }
            row.add(nameLabel).left().expandX();
            
            row.addListener(new com.badlogic.gdx.scenes.scene2d.InputListener() {
                @Override
                public boolean touchDown(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y, int pointer, int button) {
                    showDetails(item);
                    return true;
                }
            });
            
            listTable.add(row).growX().pad(2).row();
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
        
        Texture tex = new Texture(Gdx.files.internal("items/" + getFolder(item.getType()) + "/" + item.getIconPath()));
        loadedTextures.add(tex);
        Image icon = new Image(tex);
        icon.setScaling(Scaling.fit);
        detailArea.add(icon).size(128).pad(20).row();
        
        detailArea.add(new Label(item.getName(), VisUI.getSkin())).pad(5).row();
        Label desc = new Label(item.getDescription(), VisUI.getSkin());
        desc.setWrap(true);
        desc.setAlignment(Align.center);
        detailArea.add(desc).width(300).pad(10).row();
        
        // Stats
        Table stats = new Table();
        if (item.hpBonus != 0) stats.add(new Label("HP: +" + item.hpBonus, VisUI.getSkin())).pad(5).row();
        if (item.attackBonus != 0) stats.add(new Label("ATK: +" + item.attackBonus, VisUI.getSkin())).pad(5).row();
        if (item.defenseBonus != 0) stats.add(new Label("DEF: +" + item.defenseBonus, VisUI.getSkin())).pad(5).row();
        if (item.speedBonus != 0) stats.add(new Label("SPD: +" + item.speedBonus, VisUI.getSkin())).pad(5).row();
        detailArea.add(stats).pad(10).row();

        final boolean isEquipped = player.getEquipped(item.getType()) == item;
        TextButton actionBtn = createStyledButton(isEquipped ? "UNEQUIP" : "EQUIP");
        actionBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (isEquipped) player.unequipItem(item.getType());
                else player.equipItem(item);
                refreshList();
                showDetails(item);
            }
        });
        detailArea.add(actionBtn).width(200).pad(20);
    }

    private TextButton createStyledButton(String text) {
        TextButton button = new TextButton(text, VisUI.getSkin());
        button.setColor(Color.WHITE); // Ensure background is bright

        // Initial text color setup
        updateButtonFontColor(button);

        button.addListener(new com.badlogic.gdx.scenes.scene2d.InputListener() {
            @Override
            public void enter(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (!button.isDisabled()) button.getLabel().setColor(Color.WHITE);
            }
            @Override
            public void exit(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y, int pointer, Actor toActor) {
                updateButtonFontColor(button);
            }
        });
        return button;
    }

    private void updateButtonFontColor(TextButton button) {
        if (button.isDisabled()) button.getLabel().setColor(Color.GRAY);
        else button.getLabel().setColor(Color.LIGHT_GRAY);
    }

    @Override
    public void dispose() {
        super.dispose();
        for (Texture t : loadedTextures) t.dispose();
    }
}
