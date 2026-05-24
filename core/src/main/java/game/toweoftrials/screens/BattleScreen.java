package game.toweoftrials.screens;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import game.toweoftrials.Main;
import game.toweoftrials.ecs.CombatSystem;
import game.toweoftrials.ecs.HeroManager;
import game.toweoftrials.ecs.components.*;
import game.toweoftrials.model.Enemy;
import game.toweoftrials.model.Player;
import game.toweoftrials.model.Skill;

public class BattleScreen extends BaseScreen implements CombatSystem.CombatListener {
    public enum EncounterType {
        NORMAL, MINI_BOSS, BOSS
    }

    private final int floor;
    private final Array<Enemy.EnemyType> encounters;
    private int currentEncounterIndex;
    private final Drawable white;
    private final Texture slimeTexture;
    
    private final Engine engine;
    private final CombatSystem combatSystem;
    private final ComponentMapper<StatsComponent> sm = ComponentMapper.getFor(StatsComponent.class);
    
    private Player player;
    private Enemy enemy;
    
    private VisLabel logLabel;
    private VisLabel playerHPLabel;
    private VisLabel playerAPLabel;
    private VisLabel playerLevelLabel;
    private VisLabel enemyHPLabel;
    
    private VisTable actionArea;
    private VisTable inventoryArea;
    private VisTable equipmentArea;
    private VisTable combatArea;
    
    private VisTextButton attackBtn, skill1Btn, skill2Btn, ultimateBtn, passTurnBtn, guardBtn;

    private final Skill basicAttack = new Skill("Basic Attack", Skill.SkillType.DAMAGE, 1, 0, 1.0f);
    private final Skill quickStrike = new Skill("Quick Strike", Skill.SkillType.DAMAGE, 1, 0, 1.2f);
    private final Skill heavySmash = new Skill("Heavy Smash", Skill.SkillType.DAMAGE, 2, 1, 1.8f);
    private final Skill ultimate = new Skill("ULTIMATE", Skill.SkillType.DAMAGE, 4, 3, 3.5f);
    private final Skill guard = new Skill("Guard", Skill.SkillType.DEFENSE, 1, 0, 1.5f);

    public BattleScreen(Main game, int floor, Enemy.EnemyType type) {
        this(game, floor, new Array<>(new Enemy.EnemyType[]{type}));
    }

    public BattleScreen(Main game, int floor, Array<Enemy.EnemyType> encounters) {
        super(game);
        this.floor = floor;
        this.encounters = encounters;
        this.currentEncounterIndex = 0;
        this.white = VisUI.getSkin().getDrawable("white");
        this.slimeTexture = new Texture(Gdx.files.internal("monsters/slime_blue.png"));
        
        this.engine = new Engine();
        this.combatSystem = new CombatSystem(this);
        engine.addSystem(combatSystem);
        
        setupPlayer();
        setupNextEncounter();
        setupUI();
        
        // Force engine update to process entity additions before starting combat
        engine.update(0);
        combatSystem.resetCombat();
    }

    private void setupPlayer() {
        this.player = HeroManager.getPlayer();
        engine.addEntity(player.getEntity());
    }

    private void setupNextEncounter() {
        if (currentEncounterIndex >= encounters.size) return;
        Enemy.EnemyType type = encounters.get(currentEncounterIndex);
        if (enemy != null) engine.removeEntity(enemy.getEntity());

        Entity enemyEntity = new Entity();
        switch (type) {
            case BOSS: enemyEntity.add(new StatsComponent("Boss Floor " + floor, 500, 40, 20, 10)); break;
            case MINI_BOSS: enemyEntity.add(new StatsComponent("Elite Guardian", 200, 30, 15, 12)); break;
            default: enemyEntity.add(new StatsComponent("Slime", 50, 15, 5, 12)); break;
        }
        enemyEntity.add(new APComponent(5));
        enemyEntity.add(new BattleComponent(false));
        enemyEntity.add(new AbilitiesComponent());
        engine.addEntity(enemyEntity);
        this.enemy = new Enemy(enemyEntity, type);
    }

    private void setupUI() {
        root.clear();
        
        // Define percentages
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();
        
        VisTable topRow = new VisTable();
        VisTable bottomRow = new VisTable();
        
        equipmentArea = new VisTable(true);
        combatArea = new VisTable(true);
        actionArea = new VisTable(true);
        inventoryArea = new VisTable(true);

        // Styling (borders/background placeholders)
        equipmentArea.setBackground(white); equipmentArea.setColor(0.1f, 0.1f, 0.1f, 1f);
        combatArea.setBackground(white); combatArea.setColor(0.15f, 0.15f, 0.15f, 1f);
        actionArea.setBackground(white); actionArea.setColor(0.12f, 0.12f, 0.12f, 1f);
        inventoryArea.setBackground(white); inventoryArea.setColor(0.1f, 0.1f, 0.1f, 1f);

        topRow.add(combatArea).width(w * 0.7f).growY();
        topRow.add(equipmentArea).width(w * 0.3f).growY();
        
        bottomRow.add(actionArea).width(w * 0.7f).growY();
        bottomRow.add(inventoryArea).width(w * 0.3f).growY();

        root.add(topRow).height(h * 0.6f).growX().row();
        root.add(bottomRow).height(h * 0.4f).growX();

        setupEquipmentUI();
        setupCombatUI();
        setupActionUI();
        setupInventoryUI();
        
        updateLabels();
    }

    private void setupEquipmentUI() {
        equipmentArea.clear();
        equipmentArea.add(new VisLabel("EQUIPMENT")).pad(5).row();
        equipmentArea.add(createEquipSlot("Weapon")).pad(2).row();
        equipmentArea.add(createEquipSlot("Armor")).pad(2).row();
        equipmentArea.add(createEquipSlot("Accessory")).pad(2).row();
        equipmentArea.add().grow();
    }

    private VisTable createEquipSlot(String name) {
        VisTable slot = new VisTable();
        Image icon = new Image(white);
        icon.setColor(Color.DARK_GRAY);
        slot.add(icon).size(32).pad(5);
        slot.add(new VisLabel(name)).left();
        return slot;
    }

    private void setupCombatUI() {
        combatArea.clear();
        
        Enemy.EnemyType type = encounters.get(currentEncounterIndex);
        String titleStr = "FLOOR " + floor + (encounters.size > 1 ? " (" + (currentEncounterIndex + 1) + "/" + encounters.size + ")" : "");
        combatArea.add(new VisLabel(titleStr)).colspan(2).pad(5).row();

        VisTable battleLayout = new VisTable();
        
        // Player
        VisTable playerSide = new VisTable();
        Image playerImg = new Image(white); playerImg.setColor(Color.BLUE);
        playerSide.add(playerImg).size(80).pad(5).row();
        playerSide.add(new VisLabel(player.getName())).row();
        playerLevelLabel = new VisLabel(""); playerSide.add(playerLevelLabel).row();
        playerHPLabel = new VisLabel(""); playerSide.add(playerHPLabel).row();
        playerAPLabel = new VisLabel(""); playerSide.add(playerAPLabel).row();
        
        // Enemy
        VisTable enemySide = new VisTable();
        Image enemyImg;
        if (type == Enemy.EnemyType.NORMAL) {
            enemyImg = new Image(slimeTexture);
        } else {
            enemyImg = new Image(white);
            if (type == Enemy.EnemyType.BOSS) enemyImg.setColor(Color.PURPLE);
            else if (type == Enemy.EnemyType.MINI_BOSS) enemyImg.setColor(Color.ORANGE);
            else enemyImg.setColor(Color.RED);
        }
        
        float eSize = type == Enemy.EnemyType.BOSS ? 120 : (type == Enemy.EnemyType.MINI_BOSS ? 100 : 80);
        enemySide.add(enemyImg).size(eSize).pad(5).row();
        enemySide.add(new VisLabel(enemy.getName())).row();
        enemyHPLabel = new VisLabel(""); enemySide.add(enemyHPLabel).row();

        battleLayout.add(enemySide).expandX().center();
        battleLayout.add(playerSide).expandX().center();
        
        combatArea.add(battleLayout).growX().row();
        
        VisTable logTable = new VisTable();
        logTable.add(new VisLabel("--- BATTLE LOG ---")).row();
        logLabel = new VisLabel("A wild monster appears!");
        logLabel.setWrap(true);
        logTable.add(logLabel).width(300).pad(10);
        combatArea.add(logTable).grow();
    }

    private void setupActionUI() {
        actionArea.clear();
        actionArea.add(new VisLabel("ACTIONS")).pad(5).row();
        
        VisTable buttons = new VisTable(true);
        attackBtn = new VisTextButton("Basic Attack (1 AP)");
        skill1Btn = new VisTextButton("Quick Strike (1 AP)");
        skill2Btn = new VisTextButton("Heavy Smash (2 AP)");
        ultimateBtn = new VisTextButton("ULTIMATE (4 AP)");
        guardBtn = new VisTextButton("Guard (1 AP)");
        passTurnBtn = new VisTextButton("Pass Turn");

        buttons.add(attackBtn).width(180);
        buttons.add(skill1Btn).width(180).row();
        buttons.add(skill2Btn).width(180);
        buttons.add(ultimateBtn).width(180).row();
        buttons.add(guardBtn).width(180);
        buttons.add(passTurnBtn).width(180).row();

        actionArea.add(buttons).expand().center();

        attackBtn.addListener(new ChangeListener() { @Override public void changed(ChangeEvent event, Actor actor) { combatSystem.performSkill(player.getEntity(), enemy.getEntity(), basicAttack); }});
        skill1Btn.addListener(new ChangeListener() { @Override public void changed(ChangeEvent event, Actor actor) { combatSystem.performSkill(player.getEntity(), enemy.getEntity(), quickStrike); }});
        skill2Btn.addListener(new ChangeListener() { @Override public void changed(ChangeEvent event, Actor actor) { combatSystem.performSkill(player.getEntity(), enemy.getEntity(), heavySmash); }});
        ultimateBtn.addListener(new ChangeListener() { @Override public void changed(ChangeEvent event, Actor actor) { combatSystem.performSkill(player.getEntity(), enemy.getEntity(), ultimate); }});
        guardBtn.addListener(new ChangeListener() { @Override public void changed(ChangeEvent event, Actor actor) { combatSystem.performDefensiveSkill(player.getEntity(), "Guard", 1, 0, 1.5f); }});
        passTurnBtn.addListener(new ChangeListener() { @Override public void changed(ChangeEvent event, Actor actor) { combatSystem.nextTurn(); }});
    }

    private void setupInventoryUI() {
        inventoryArea.clear();
        inventoryArea.add(new VisLabel("INVENTORY")).pad(5).row();
        VisTable grid = new VisTable(true);
        for(int i=0; i<12; i++) {
            Image item = new Image(white);
            item.setColor(Color.GRAY);
            grid.add(item).size(32).pad(2);
            if((i+1)%3 == 0) grid.row();
        }
        inventoryArea.add(grid).grow();
    }

    private void updateLabels() {
        if (player == null || enemy == null || playerHPLabel == null) return;
        
        StatsComponent ps = player.getStats();
        APComponent pa = player.getAP();
        AbilitiesComponent pab = player.getAbilities();
        LevelComponent pl = player.getLevel();
        StatsComponent es = enemy.getStats();
        
        playerLevelLabel.setText("Lvl: " + pl.level + " (" + pl.currentXp + "/" + pl.xpToNextLevel + " XP)");
        String hpText = "HP: " + ps.hp + "/" + ps.maxHp;
        if (ps.shield > 0) hpText += " [+" + ps.shield + "]";
        playerHPLabel.setText(hpText);
        playerAPLabel.setText("AP: " + pa.currentAP + "/" + pa.maxAP);
        enemyHPLabel.setText("HP: " + es.hp + "/" + es.maxHp);
        
        updateButtonText(skill1Btn, "Quick Strike (1 AP)", pab, "Quick Strike");
        updateButtonText(skill2Btn, "Heavy Smash (2 AP, CD: 1)", pab, "Heavy Smash");
        updateButtonText(ultimateBtn, "ULTIMATE (4 AP, CD: 3)", pab, "ULTIMATE");

        if (combatSystem.getActiveEntity() == player.getEntity() && !combatSystem.isCombatEnded()) {
            attackBtn.setDisabled(pa.currentAP < 1);
            skill1Btn.setDisabled(pa.currentAP < 1 || !pab.isReady("Quick Strike"));
            skill2Btn.setDisabled(pa.currentAP < 2 || !pab.isReady("Heavy Smash"));
            ultimateBtn.setDisabled(pa.currentAP < 4 || !pab.isReady("ULTIMATE"));
            guardBtn.setDisabled(pa.currentAP < 1);
            passTurnBtn.setDisabled(false);
        } else {
            disableActions();
        }
    }

    private void updateButtonText(VisTextButton button, String baseText, AbilitiesComponent abilities, String skillName) {
        if (!abilities.isReady(skillName)) button.setText(baseText + " (CD: " + abilities.getRemainingCooldown(skillName) + ")");
        else button.setText(baseText);
    }

    private void disableActions() {
        if (attackBtn == null) return;
        attackBtn.setDisabled(true); skill1Btn.setDisabled(true); skill2Btn.setDisabled(true);
        ultimateBtn.setDisabled(true); guardBtn.setDisabled(true); passTurnBtn.setDisabled(true);
    }

    private void enableActions() {
        if (attackBtn == null) return;
        attackBtn.setDisabled(false); skill1Btn.setDisabled(false); skill2Btn.setDisabled(false);
        ultimateBtn.setDisabled(false); guardBtn.setDisabled(false); passTurnBtn.setDisabled(false);
    }

    @Override public void onTurnStarted(Entity entity) {
        if(logLabel != null) logLabel.setText("It's " + sm.get(entity).name + "'s turn!");
        if (entity == player.getEntity()) enableActions(); else disableActions();
    }

    @Override public void onActionResolved(String message) { if(logLabel != null) logLabel.setText(message); }
    @Override public void onUpdateUI() {}

    @Override
    public void onCombatEnded(boolean playerWon) {
        logLabel.setText(playerWon ? "Victory!" : "Defeat...");
        disableActions();
        
        player.healFull(); // Always heal and reset cooldowns after combat
        
        actionArea.clearChildren();
        actionArea.add(new VisLabel(playerWon ? "BATTLE WON" : "BATTLE LOST")).pad(10).row();
        
        if (playerWon && currentEncounterIndex < encounters.size - 1) {
            VisTextButton nextBtn = new VisTextButton("Next Battle");
            nextBtn.addListener(new ChangeListener() {
                @Override public void changed(ChangeEvent event, Actor actor) {
                    currentEncounterIndex++;
                    setupNextEncounter();
                    setupUI();
                    combatSystem.resetCombat();
                }
            });
            actionArea.add(nextBtn).width(200);
        } else {
            VisTextButton returnBtn = new VisTextButton("Return");
            returnBtn.addListener(new ChangeListener() {
                @Override public void changed(ChangeEvent event, Actor actor) {
                    game.setScreen(new FloorMenuScreen(game, floor));
                }
            });
            actionArea.add(returnBtn).width(200);
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        if (slimeTexture != null) {
            slimeTexture.dispose();
        }
    }

    @Override
    public void hide() {
        super.hide();
        // Crucial: Remove all entities from engine so they can be re-added to a new engine later
        if (engine != null) {
            engine.removeAllEntities();
        }
    }

    @Override
    public void render(float delta) {
        updateLabels();
        super.render(delta);
        engine.update(delta);
    }
}
