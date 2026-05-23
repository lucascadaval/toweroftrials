package game.toweoftrials.screens;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import game.toweoftrials.Main;
import game.toweoftrials.ecs.CombatSystem;
import game.toweoftrials.ecs.components.APComponent;
import game.toweoftrials.ecs.components.BattleComponent;
import game.toweoftrials.ecs.components.StatsComponent;
import game.toweoftrials.ecs.components.AbilitiesComponent;

public class BattleScreen extends BaseScreen implements CombatSystem.CombatListener {
    private final int floor;
    private final boolean isBoss;
    private final Drawable white;
    
    private final Engine engine;
    private final CombatSystem combatSystem;
    private final ComponentMapper<StatsComponent> sm = ComponentMapper.getFor(StatsComponent.class);
    private final ComponentMapper<APComponent> am = ComponentMapper.getFor(APComponent.class);
    private final ComponentMapper<AbilitiesComponent> abm = ComponentMapper.getFor(AbilitiesComponent.class);
    
    private Entity player;
    private Entity enemy;
    
    private VisLabel logLabel;
    private VisLabel playerHPLabel;
    private VisLabel playerAPLabel;
    private VisLabel enemyHPLabel;
    private VisTable actionMenu;
    private VisTextButton attackBtn, skill1Btn, skill2Btn, ultimateBtn, passTurnBtn;

    public BattleScreen(Main game, int floor, boolean isBoss) {
        super(game);
        this.floor = floor;
        this.isBoss = isBoss;
        this.white = VisUI.getSkin().getDrawable("white");
        
        this.engine = new Engine();
        this.combatSystem = new CombatSystem(this);
        engine.addSystem(combatSystem);
        
        setupEntities();
        setupUI();
        
        combatSystem.startCombat();
    }

    private void setupEntities() {
        player = new Entity();
        player.add(new StatsComponent("Hero", 100, 20, 10, 15));
        player.add(new APComponent(3));
        player.add(new BattleComponent(true));
        player.add(new AbilitiesComponent());
        engine.addEntity(player);

        enemy = new Entity();
        if (isBoss) {
            enemy.add(new StatsComponent("Boss Floor " + floor, 500, 40, 20, 10));
        } else {
            enemy.add(new StatsComponent("Slime", 50, 15, 5, 12));
        }
        enemy.add(new APComponent(0));
        enemy.add(new BattleComponent(false));
        enemy.add(new AbilitiesComponent());
        engine.addEntity(enemy);
    }

    private void setupUI() {
        root.clear();
        
        // Header
        root.add(new VisLabel(isBoss ? "BOSS BATTLE - FLOOR " + floor : "BATTLE - FLOOR " + floor)).colspan(2).pad(10).row();

        // Combat Area
        VisTable combatArea = new VisTable();
        
        // Player placeholder
        VisTable playerSide = new VisTable();
        Image playerImg = new Image(white);
        playerImg.setColor(Color.BLUE);
        playerSide.add(playerImg).size(100).pad(10).row();
        playerSide.add(new VisLabel(sm.get(player).name)).row();
        playerHPLabel = new VisLabel("");
        playerAPLabel = new VisLabel("");
        playerSide.add(playerHPLabel).row();
        playerSide.add(playerAPLabel).row();
        
        // Enemy placeholder
        VisTable enemySide = new VisTable();
        Image enemyImg = new Image(white);
        enemyImg.setColor(isBoss ? Color.PURPLE : Color.RED);
        enemySide.add(enemyImg).size(isBoss ? 150 : 100).pad(10).row();
        enemySide.add(new VisLabel(sm.get(enemy).name)).row();
        enemyHPLabel = new VisLabel("");
        enemySide.add(enemyHPLabel).row();

        combatArea.add(playerSide).expandX().left().padLeft(50);
        combatArea.add(enemySide).expandX().right().padRight(50);
        
        root.add(combatArea).growX().row();

        // Battle Log
        VisTable logTable = new VisTable();
        logTable.add(new VisLabel("BATTLE LOG")).row();
        logLabel = new VisLabel("A wild monster appears!");
        logTable.add(logLabel).pad(10).row();
        root.add(logTable).growX().pad(10).row();

        // Action Menu
        actionMenu = new VisTable(true);
        actionMenu.add(new VisLabel("ACTIONS")).row();
        
        attackBtn = new VisTextButton("Basic Attack (1 AP)");
        skill1Btn = new VisTextButton("Quick Strike (1 AP)");
        skill2Btn = new VisTextButton("Heavy Smash (2 AP, CD: 1)");
        ultimateBtn = new VisTextButton("ULTIMATE (4 AP, CD: 3)");
        VisTextButton itemsBtn = new VisTextButton("Items");
        passTurnBtn = new VisTextButton("Pass Turn");

        actionMenu.add(attackBtn).width(200).row();
        actionMenu.add(skill1Btn).width(200).row();
        actionMenu.add(skill2Btn).width(200).row();
        actionMenu.add(ultimateBtn).width(200).row();
        actionMenu.add(itemsBtn).width(200).row();
        actionMenu.add(passTurnBtn).width(200).row();

        root.add(actionMenu).expandY().bottom().padBottom(20).colspan(2);

        updateLabels();

        attackBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                combatSystem.performAttack(player, enemy, "Basic Attack", 1, 0, 0, 1.0f);
            }
        });

        skill1Btn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                combatSystem.performAttack(player, enemy, "Quick Strike", 1, 0, 0, 1.2f);
            }
        });

        skill2Btn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                combatSystem.performAttack(player, enemy, "Heavy Smash", 2, 0, 1, 1.8f);
            }
        });

        ultimateBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                combatSystem.performAttack(player, enemy, "ULTIMATE", 4, 0, 3, 3.5f);
            }
        });

        passTurnBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                combatSystem.nextTurn();
            }
        });
    }

    private void updateLabels() {
        if (player == null || enemy == null) return;
        
        StatsComponent ps = sm.get(player);
        APComponent pa = am.get(player);
        AbilitiesComponent pab = abm.get(player);
        StatsComponent es = sm.get(enemy);
        
        playerHPLabel.setText("HP: " + ps.hp + "/" + ps.maxHp);
        playerAPLabel.setText("AP: " + pa.currentAP + "/" + pa.maxAP);
        enemyHPLabel.setText("HP: " + es.hp + "/" + es.maxHp);
        
        // Update skill names with cooldown info
        updateButtonText(skill1Btn, "Quick Strike (1 AP)", pab, "Quick Strike");
        updateButtonText(skill2Btn, "Heavy Smash (2 AP, CD: 1)", pab, "Heavy Smash");
        updateButtonText(ultimateBtn, "ULTIMATE (4 AP, CD: 3)", pab, "ULTIMATE");

        // Disable buttons based on AP and Cooldown
        if (combatSystem.getActiveEntity() == player && !combatSystem.isCombatEnded()) {
            attackBtn.setDisabled(pa.currentAP < 1);
            skill1Btn.setDisabled(pa.currentAP < 1 || !pab.isReady("Quick Strike"));
            skill2Btn.setDisabled(pa.currentAP < 2 || !pab.isReady("Heavy Smash"));
            ultimateBtn.setDisabled(pa.currentAP < 4 || !pab.isReady("ULTIMATE"));
            passTurnBtn.setDisabled(false);
        } else {
            disableActions();
        }
    }

    private void updateButtonText(VisTextButton button, String baseText, AbilitiesComponent abilities, String skillName) {
        if (!abilities.isReady(skillName)) {
            button.setText(baseText + " (CD: " + abilities.getRemainingCooldown(skillName) + ")");
        } else {
            button.setText(baseText);
        }
    }

    private void disableActions() {
        attackBtn.setDisabled(true);
        skill1Btn.setDisabled(true);
        skill2Btn.setDisabled(true);
        ultimateBtn.setDisabled(true);
        passTurnBtn.setDisabled(true);
    }

    private void enableActions() {
        attackBtn.setDisabled(false);
        skill1Btn.setDisabled(false);
        skill2Btn.setDisabled(false);
        ultimateBtn.setDisabled(false);
        passTurnBtn.setDisabled(false);
    }

    @Override
    public void onTurnStarted(Entity entity) {
        StatsComponent s = sm.get(entity);
        logLabel.setText("It's " + s.name + "'s turn!");
        if (entity == player) {
            enableActions();
        } else {
            disableActions();
        }
    }

    @Override
    public void onActionResolved(String message) {
        logLabel.setText(message);
    }

    @Override
    public void onUpdateUI() {
        // Handled by render loop
    }

    @Override
    public void onCombatEnded(boolean playerWon) {
        logLabel.setText(playerWon ? "Victory!" : "Defeat...");
        disableActions();
        
        actionMenu.clearChildren();
        actionMenu.add(new VisLabel(playerWon ? "BATTLE WON" : "BATTLE LOST")).pad(10).row();
        
        VisTextButton returnBtn = new VisTextButton("Return");
        returnBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new FloorMenuScreen(game, floor));
            }
        });
        actionMenu.add(returnBtn).width(200).pad(10);
    }

    @Override
    public void render(float delta) {
        // Update labels based on latest ECS state BEFORE drawing
        updateLabels();
        
        super.render(delta); // stage.draw() is inside here
        engine.update(delta); // calls combatSystem.update()
    }
}
