package game.toweoftrials.screens;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.ComponentMapper;
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
    private final int floor;
    private final Array<Enemy.EnemyType> encounters;
    private int currentEncounterIndex = 0;
    private final Drawable white;
    
    private final Engine engine;
    private final CombatSystem combatSystem;
    private final ComponentMapper<StatsComponent> sm = ComponentMapper.getFor(StatsComponent.class);
    private final ComponentMapper<APComponent> am = ComponentMapper.getFor(APComponent.class);
    private final ComponentMapper<AbilitiesComponent> abm = ComponentMapper.getFor(AbilitiesComponent.class);
    private final ComponentMapper<LevelComponent> lm = ComponentMapper.getFor(LevelComponent.class);
    
    private Player player;
    private Enemy enemy;
    
    private VisLabel logLabel;
    private VisLabel playerHPLabel;
    private VisLabel playerAPLabel;
    private VisLabel playerLevelLabel;
    private VisLabel enemyHPLabel;
    private VisTable actionMenu;
    private VisTextButton attackBtn, skill1Btn, skill2Btn, ultimateBtn, passTurnBtn;

    // Define standard skills for MVP
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
        
        this.engine = new Engine();
        this.combatSystem = new CombatSystem(this);
        engine.addSystem(combatSystem);
        
        setupPlayer();
        setupNextEncounter();
        setupUI();
        
        combatSystem.resetCombat();
    }

    private void setupPlayer() {
        this.player = HeroManager.getPlayer();
        engine.addEntity(player.getEntity());
    }

    private void setupNextEncounter() {
        if (currentEncounterIndex >= encounters.size) return;

        Enemy.EnemyType type = encounters.get(currentEncounterIndex);
        
        if (enemy != null) {
            engine.removeEntity(enemy.getEntity());
        }

        Entity enemyEntity = new Entity();
        switch (type) {
            case BOSS:
                enemyEntity.add(new StatsComponent("Boss Floor " + floor, 500, 40, 20, 10));
                break;
            case MINI_BOSS:
                enemyEntity.add(new StatsComponent("Elite Guardian", 200, 30, 15, 12));
                break;
            case NORMAL:
            default:
                enemyEntity.add(new StatsComponent("Slime", 50, 15, 5, 12));
                break;
        }
        enemyEntity.add(new APComponent(5));
        enemyEntity.add(new BattleComponent(false));
        enemyEntity.add(new AbilitiesComponent());
        engine.addEntity(enemyEntity);
        
        this.enemy = new Enemy(enemyEntity, type);
    }

    private void setupUI() {
        root.clear();
        
        Enemy.EnemyType type = encounters.get(currentEncounterIndex);
        String title = "BATTLE - FLOOR " + floor;
        if (type == Enemy.EnemyType.BOSS) title = "BOSS BATTLE - FLOOR " + floor;
        if (type == Enemy.EnemyType.MINI_BOSS) title = "MINI-BOSS - FLOOR " + floor;
        
        if (encounters.size > 1) {
            title += " (Wave " + (currentEncounterIndex + 1) + "/" + encounters.size + ")";
        }

        root.add(new VisLabel(title)).colspan(2).pad(10).row();

        VisTable combatArea = new VisTable();
        
        VisTable playerSide = new VisTable();
        Image playerImg = new Image(white);
        playerImg.setColor(Color.BLUE);
        playerSide.add(playerImg).size(100).pad(10).row();
        playerSide.add(new VisLabel(player.getName())).row();
        playerLevelLabel = new VisLabel("");
        playerHPLabel = new VisLabel("");
        playerAPLabel = new VisLabel("");
        playerSide.add(playerLevelLabel).row();
        playerSide.add(playerHPLabel).row();
        playerSide.add(playerAPLabel).row();
        
        VisTable enemySide = new VisTable();
        Image enemyImg = new Image(white);
        Color enemyColor = Color.RED;
        if (type == Enemy.EnemyType.BOSS) enemyColor = Color.PURPLE;
        if (type == Enemy.EnemyType.MINI_BOSS) enemyColor = Color.ORANGE;
        
        enemyImg.setColor(enemyColor);
        enemySide.add(enemyImg).size(type == Enemy.EnemyType.BOSS ? 150 : (type == Enemy.EnemyType.MINI_BOSS ? 130 : 100)).pad(10).row();
        enemySide.add(new VisLabel(enemy.getName())).row();
        enemyHPLabel = new VisLabel("");
        enemySide.add(enemyHPLabel).row();

        combatArea.add(playerSide).expandX().left().padLeft(50);
        combatArea.add(enemySide).expandX().right().padRight(50);
        
        root.add(combatArea).growX().row();

        VisTable logTable = new VisTable();
        logTable.add(new VisLabel("BATTLE LOG")).row();
        logLabel = new VisLabel("A wild monster appears!");
        logTable.add(logLabel).pad(10).row();
        root.add(logTable).growX().pad(10).row();

        actionMenu = new VisTable(true);
        actionMenu.add(new VisLabel("ACTIONS")).row();
        
        attackBtn = new VisTextButton("Basic Attack (1 AP)");
        skill1Btn = new VisTextButton("Quick Strike (1 AP)");
        skill2Btn = new VisTextButton("Heavy Smash (2 AP, CD: 1)");
        ultimateBtn = new VisTextButton("ULTIMATE (4 AP, CD: 3)");
        VisTextButton defenseBtn = new VisTextButton("Guard (1 AP)");
        passTurnBtn = new VisTextButton("Pass Turn");

        actionMenu.add(attackBtn).width(200).row();
        actionMenu.add(skill1Btn).width(200).row();
        actionMenu.add(skill2Btn).width(200).row();
        actionMenu.add(ultimateBtn).width(200).row();
        actionMenu.add(defenseBtn).width(200).row();
        actionMenu.add(passTurnBtn).width(200).row();

        root.add(actionMenu).expandY().bottom().padBottom(20).colspan(2);

        updateLabels();

        attackBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                combatSystem.performSkill(player.getEntity(), enemy.getEntity(), basicAttack);
            }
        });

        skill1Btn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                combatSystem.performSkill(player.getEntity(), enemy.getEntity(), quickStrike);
            }
        });

        skill2Btn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                combatSystem.performSkill(player.getEntity(), enemy.getEntity(), heavySmash);
            }
        });

        ultimateBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                combatSystem.performSkill(player.getEntity(), enemy.getEntity(), ultimate);
            }
        });

        defenseBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                combatSystem.performSkill(player.getEntity(), enemy.getEntity(), guard);
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
        
        StatsComponent ps = player.getStats();
        APComponent pa = player.getAP();
        AbilitiesComponent pab = player.getAbilities();
        LevelComponent pl = player.getLevel();
        StatsComponent es = enemy.getStats();
        
        if (pl != null) {
            playerLevelLabel.setText("Lvl: " + pl.level + " (" + pl.currentXp + "/" + pl.xpToNextLevel + " XP)");
        }
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
        if (entity == player.getEntity()) {
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
    }

    @Override
    public void onCombatEnded(boolean playerWon) {
        logLabel.setText(playerWon ? "Victory!" : "Defeat...");
        disableActions();
        
        if (!playerWon) {
            player.healFull();
        }
        
        actionMenu.clearChildren();
        
        if (playerWon && currentEncounterIndex < encounters.size - 1) {
            actionMenu.add(new VisLabel("ENEMY DEFEATED")).pad(10).row();
            VisTextButton nextBtn = new VisTextButton("Next Battle");
            nextBtn.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    currentEncounterIndex++;
                    setupNextEncounter();
                    setupUI();
                    combatSystem.resetCombat();
                }
            });
            actionMenu.add(nextBtn).width(200).pad(10);
        } else {
            actionMenu.add(new VisLabel(playerWon ? "DUNGEON CLEARED" : "BATTLE LOST")).pad(10).row();
            VisTextButton returnBtn = new VisTextButton("Return");
            returnBtn.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    game.setScreen(new FloorMenuScreen(game, floor));
                }
            });
            actionMenu.add(returnBtn).width(200).pad(10);
        }
    }

    @Override
    public void render(float delta) {
        updateLabels();
        super.render(delta);
        engine.update(delta);
    }
}
