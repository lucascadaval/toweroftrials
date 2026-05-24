package game.toweoftrials.screens;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.kotcrab.vis.ui.VisUI;
import game.toweoftrials.Main;
import game.toweoftrials.ecs.CombatSystem;
import game.toweoftrials.ecs.HeroManager;
import game.toweoftrials.ecs.components.*;
import game.toweoftrials.model.AnimationEffect;
import game.toweoftrials.model.Enemy;
import game.toweoftrials.model.GameEntity;
import game.toweoftrials.model.Player;
import game.toweoftrials.model.Skill;

public class BattleScreen extends BaseScreen implements CombatSystem.CombatListener {
    
    private final int floor;
    private final Array<Array<Enemy.EnemyType>> waves;
    private int currentWaveIndex;
    private final Drawable white;
    
    private final Engine engine;
    private final CombatSystem combatSystem;
    private final ComponentMapper<StatsComponent> sm = ComponentMapper.getFor(StatsComponent.class);
    private final ComponentMapper<APComponent> am = ComponentMapper.getFor(APComponent.class);
    private final ComponentMapper<VisualComponent> vm = ComponentMapper.getFor(VisualComponent.class);
    
    private Player player;
    private final Array<Enemy> enemies = new Array<>();
    
    private Label playerHPLabel, playerAPLabel, playerLevelLabel;
    private Table actionArea, inventoryArea, equipmentArea, combatArea;
    private Table playerSide, enemySide;
    
    private TextButton attackBtn, passTurnBtn;
    private final Array<String> logMessageQueue = new Array<>();
    private boolean isShowingLog = false;
    private boolean combatJustEnded = false;
    private boolean playerWonLast = false;
    
    private Skill selectedSkill = null;
    private boolean isTargeting = false;

    private final ObjectMap<String, Array<Texture>> animationFrames = new ObjectMap<>();
    private final Array<ActiveAnimation> activeAnimations = new Array<>();
    private final ObjectMap<String, Texture> entityTextures = new ObjectMap<>();

    private static class ActiveAnimation {
        AnimationEffect effect;
        Actor anchor;
        ActiveAnimation(AnimationEffect effect, Actor anchor) { this.effect = effect; this.anchor = anchor; }
    }

    private final Skill basicAttack = new Skill("Attack", Skill.SkillType.OFFENSIVE, 1, 0, 1.0f, "double_slash");

    public BattleScreen(Main game, int floor, Array<Array<Enemy.EnemyType>> waves) {
        super(game);
        this.floor = floor;
        this.waves = waves;
        this.currentWaveIndex = 0;
        this.white = VisUI.getSkin().getDrawable("white");
        
        loadSkillAnimations();
        
        this.engine = new Engine();
        this.combatSystem = new CombatSystem(this);
        engine.addSystem(combatSystem);
        
        setupPlayer();
        setupNextWave();
        setupUI();
        
        engine.update(0);
        combatSystem.resetCombat();
    }

    private void loadSkillAnimations() {
        Array<Texture> frames = new Array<>();
        for (int i = 1; i <= 10; i++) {
            frames.add(new Texture(Gdx.files.internal("skills/double_slash/warrior_skill1_frame" + i + ".png")));
        }
        animationFrames.put("double_slash", frames);
    }

    private void setupPlayer() {
        this.player = HeroManager.getPlayer();
        if (player.getEntity().getComponent(VisualComponent.class) == null) {
            player.getEntity().add(new VisualComponent("player/hero_player.png")); 
        }
        engine.addEntity(player.getEntity());
    }

    private void setupNextWave() {
        if (currentWaveIndex >= waves.size) return;
        Array<Enemy.EnemyType> types = waves.get(currentWaveIndex);
        
        for (Enemy e : enemies) engine.removeEntity(e.getEntity());
        enemies.clear();

        for (Enemy.EnemyType type : types) {
            Entity enemyEntity = new Entity();
            String texPath = "monsters/slime_blue.png";
            switch (type) {
                case BOSS: enemyEntity.add(new StatsComponent("Boss Floor " + floor, 500, 40, 20, 10)); texPath = "monsters/3head_slug_purple.png"; break;
                case MINI_BOSS: enemyEntity.add(new StatsComponent("Elite Guardian", 200, 30, 15, 12)); break;
                default: enemyEntity.add(new StatsComponent("Slime", 50, 15, 5, 12)); break;
            }
            enemyEntity.add(new APComponent(5));
            enemyEntity.add(new BattleComponent(false));
            enemyEntity.add(new AbilitiesComponent());
            enemyEntity.add(new VisualComponent(texPath));
            engine.addEntity(enemyEntity);
            enemies.add(new Enemy(enemyEntity, type));
        }
    }

    private void setupUI() {
        root.clear();
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();
        
        Table topRow = new Table();
        Table bottomRow = new Table();
        
        equipmentArea = new Table();
        combatArea = new Table();
        actionArea = new Table();
        inventoryArea = new Table();

        equipmentArea.setBackground(VisUI.getSkin().getDrawable("window"));
        combatArea.setBackground(VisUI.getSkin().getDrawable("window"));
        actionArea.setBackground(VisUI.getSkin().getDrawable("window"));
        inventoryArea.setBackground(VisUI.getSkin().getDrawable("window"));

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
        equipmentArea.add(new Label("EQUIPMENT", VisUI.getSkin())).pad(5).row();
        equipmentArea.add(createEquipSlot("Weapon")).pad(2).row();
        equipmentArea.add(createEquipSlot("Armor")).pad(2).row();
        equipmentArea.add(createEquipSlot("Accessory")).pad(2).row();
    }

    private Table createEquipSlot(String name) {
        Table slot = new Table();
        Image icon = new Image(white); icon.setColor(Color.DARK_GRAY);
        slot.add(icon).size(32).pad(5);
        slot.add(new Label(name, VisUI.getSkin())).left();
        return slot;
    }

    private void setupCombatUI() {
        combatArea.clear();
        String titleStr = "FLOOR " + floor + (waves.size > 1 ? " (Wave " + (currentWaveIndex + 1) + "/" + waves.size + ")" : "");
        combatArea.add(new Label(titleStr, VisUI.getSkin())).colspan(2).pad(5).row();

        Table battleLayout = new Table();
        
        // Enemies Side (Left)
        enemySide = new Table();
        for (Enemy e : enemies) {
            Table eTable = createEntityTable(e, Color.RED);
            enemySide.add(eTable).pad(10);
        }
        
        // Player Side (Right)
        playerSide = createEntityTable(player, Color.BLUE);
        playerLevelLabel = (Label) playerSide.getChildren().get(2);
        playerHPLabel = (Label) playerSide.getChildren().get(3);
        playerAPLabel = (Label) playerSide.getChildren().get(4);

        battleLayout.add(enemySide).expandX().center();
        battleLayout.add(playerSide).expandX().center();
        combatArea.add(battleLayout).grow().center();
    }

    private Table createEntityTable(final GameEntity ge, Color fallback) {
        Table t = new Table();
        VisualComponent vis = ge.getEntity().getComponent(VisualComponent.class);
        Image img = createEntityImage(vis, fallback);
        
        // Add click listener for targeting
        img.setTouchable(Touchable.enabled);
        img.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (isTargeting && selectedSkill != null) {
                    executeSkill(ge.getEntity());
                    return true;
                }
                return false;
            }
        });

        t.add(img).size(80).pad(5).row();
        t.add(new Label(ge.getName(), VisUI.getSkin())).row();
        t.add(new Label("", VisUI.getSkin())).row(); // Level or placeholder
        t.add(new Label("", VisUI.getSkin())).row(); // HP
        if (ge instanceof Player) t.add(new Label("", VisUI.getSkin())).row(); // AP
        
        return t;
    }

    private Image createEntityImage(VisualComponent vis, Color fallbackColor) {
        if (vis != null && vis.texturePath != null) {
            if (!entityTextures.containsKey(vis.texturePath)) {
                entityTextures.put(vis.texturePath, new Texture(Gdx.files.internal(vis.texturePath)));
            }
            return new Image(entityTextures.get(vis.texturePath));
        } else {
            Image img = new Image(white); img.setColor(fallbackColor); return img;
        }
    }

    private void setupActionUI() {
        actionArea.clear();
        actionArea.clearListeners();
        if (isShowingLog && logMessageQueue.size > 0) showLogUI();
        else if (combatJustEnded) showEndCombatUI();
        else showButtonsUI();
    }

    private void showLogUI() {
        actionArea.clear();
        String message = logMessageQueue.first();
        Label msgLabel = new Label(message, VisUI.getSkin());
        msgLabel.setWrap(true); msgLabel.setAlignment(Align.center);
        actionArea.add(msgLabel).width(500).growY().center().row();
        actionArea.add(new Label("(Click here to continue...)", VisUI.getSkin())).pad(10).bottom();
        actionArea.setTouchable(Touchable.enabled);
        actionArea.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                proceedLog(); return true;
            }
        });
    }

    private void proceedLog() {
        if (logMessageQueue.size > 0) logMessageQueue.removeIndex(0);
        if (logMessageQueue.size == 0) {
            isShowingLog = false; setupActionUI();
            Entity active = combatSystem.getActiveEntity();
            if (active != null) {
                 BattleComponent bc = active.getComponent(BattleComponent.class);
                 if (bc != null && !bc.isDead && !bc.isPlayer && !combatSystem.isCombatEnded()) {
                     combatSystem.executeOneAIAction();
                 }
            }
        } else showLogUI();
    }

    private void showButtonsUI() {
        actionArea.clear();
        actionArea.add(new Label(isTargeting ? "SELECT A TARGET" : "ACTIONS", VisUI.getSkin())).pad(10).row();
        Table buttons = new Table();
        
        attackBtn = createStyledButton("Attack (1 AP)");
        passTurnBtn = createStyledButton("Pass Turn");

        float btnWidth = 320;
        buttons.add(attackBtn).width(btnWidth).pad(10);
        buttons.add(passTurnBtn).width(btnWidth).pad(10).row();
        actionArea.add(buttons).grow();

        attackBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                selectedSkill = basicAttack;
                isTargeting = true;
                setupActionUI();
            }
        });
        
        passTurnBtn.addListener(new ChangeListener() { @Override public void changed(ChangeEvent event, Actor actor) { combatSystem.nextTurn(); }});
        updateLabels();
    }

    private void executeSkill(Entity target) {
        if (selectedSkill != null) {
            combatSystem.performSkill(player.getEntity(), target, selectedSkill);
            isTargeting = false;
            selectedSkill = null;
            setupActionUI();
        }
    }

    private void showEndCombatUI() {
        actionArea.clear();
        actionArea.add(new Label(playerWonLast ? "BATTLE WON" : "BATTLE LOST", VisUI.getSkin())).pad(10).row();
        if (playerWonLast && currentWaveIndex < waves.size - 1) {
            TextButton nextBtn = createStyledButton("Next Battle");
            nextBtn.addListener(new ChangeListener() {
                @Override public void changed(ChangeEvent event, Actor actor) {
                    currentWaveIndex++; combatJustEnded = false; setupNextWave(); setupUI(); combatSystem.resetCombat();
                }
            });
            actionArea.add(nextBtn).width(200);
        } else {
            TextButton returnBtn = createStyledButton("Return");
            returnBtn.addListener(new ChangeListener() {
                @Override public void changed(ChangeEvent event, Actor actor) { game.setScreen(new FloorMenuScreen(game, floor)); }
            });
            actionArea.add(returnBtn).width(200);
        }
    }

    private void updateLabels() {
        if (player == null) return;
        StatsComponent ps = player.getStats();
        APComponent pa = player.getAP();
        LevelComponent pl = player.getLevel();
        
        playerLevelLabel.setText("Lvl: " + pl.level + " (" + pl.currentXp + "/" + pl.xpToNextLevel + " XP)");
        String hpText = "HP: " + ps.hp + "/" + ps.maxHp;
        if (ps.shield > 0) hpText += " [+" + ps.shield + "]";
        playerHPLabel.setText(hpText);
        playerAPLabel.setText("AP: " + pa.currentAP + "/" + pa.maxAP);

        // Update enemies HP labels
        for (int i = 0; i < enemies.size; i++) {
            Enemy e = enemies.get(i);
            Table eTable = (Table) enemySide.getChildren().get(i);
            Label hpLab = (Label) eTable.getChildren().get(3);
            StatsComponent es = e.getStats();
            hpLab.setText("HP: " + es.hp + "/" + es.maxHp);
            eTable.getColor().a = es.hp <= 0 ? 0.5f : 1.0f;
        }

        if (attackBtn != null) {
            if (combatSystem.getActiveEntity() == player.getEntity() && !combatSystem.isCombatEnded() && !isShowingLog) {
                attackBtn.setDisabled(pa.currentAP < 1);
                passTurnBtn.setDisabled(false);
            } else {
                disableActions();
            }
        }
    }

    private void disableActions() {
        if (attackBtn == null) return;
        attackBtn.setDisabled(true); passTurnBtn.setDisabled(true);
    }

    private TextButton createStyledButton(String text) {
        TextButton button = new TextButton(text, VisUI.getSkin());
        button.addListener(new InputListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (!button.isDisabled()) button.setColor(Color.LIGHT_GRAY);
            }
            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                button.setColor(Color.WHITE);
            }
        });
        return button;
    }

    private void setupInventoryUI() {
        inventoryArea.clear();
        inventoryArea.add(new Label("INVENTORY", VisUI.getSkin())).pad(5).row();
        Table grid = new Table();
        for(int i=0; i<12; i++) {
            Image item = new Image(white); item.setColor(Color.GRAY);
            grid.add(item).size(32).pad(2);
            if((i+1)%3 == 0) grid.row();
        }
        inventoryArea.add(grid).grow();
    }

    @Override public void onTurnStarted(Entity entity) { addLogMessage("It's " + sm.get(entity).name + "'s turn!"); }
    @Override public void onActionResolved(String message) { addLogMessage(message); }
    @Override public void onUpdateUI() {}

    @Override
    public void onAnimationRequested(Entity attacker, Entity target, String animationName) {
        Actor targetActor = findActorForEntity(target);
        Actor attackerActor = findActorForEntity(attacker);
        if (targetActor == null || attackerActor == null) return;

        if (attacker != target) {
            float dashDist = 40f;
            if (attackerActor == playerSide) dashDist = -40f;
            attackerActor.addAction(Actions.sequence(Actions.moveBy(dashDist, 0, 0.1f), Actions.moveBy(-dashDist, 0, 0.1f)));
            targetActor.addAction(Actions.sequence(Actions.moveBy(10, 0, 0.05f), Actions.moveBy(-20, 0, 0.05f), Actions.moveBy(10, 0, 0.05f)));
        }

        if (animationName != null && animationFrames.containsKey(animationName)) {
            activeAnimations.add(new ActiveAnimation(new AnimationEffect(animationFrames.get(animationName), 0.05f), targetActor));
        }
    }

    private Actor findActorForEntity(Entity e) {
        if (e == player.getEntity()) return playerSide;
        for (int i=0; i<enemies.size; i++) {
            if (enemies.get(i).getEntity() == e) return enemySide.getChildren().get(i);
        }
        return null;
    }

    private void addLogMessage(String msg) {
        logMessageQueue.add(msg);
        if (!isShowingLog) { isShowingLog = true; setupActionUI(); }
    }

    @Override public void onCombatEnded(boolean playerWon) {
        this.playerWonLast = playerWon; this.combatJustEnded = true;
        addLogMessage(playerWon ? "Victory! All enemies defeated." : "Defeat... You were overwhelmed.");
        player.healFull();
    }

    @Override public void dispose() {
        super.dispose();
        for (Array<Texture> frames : animationFrames.values()) for (Texture t : frames) t.dispose();
        for (Texture t : entityTextures.values()) t.dispose();
    }

    @Override public void hide() { super.hide(); if (engine != null) engine.removeAllEntities(); }

    @Override
    public void render(float delta) {
        updateLabels();
        super.render(delta);
        stage.getBatch().begin();
        for (int i = activeAnimations.size - 1; i >= 0; i--) {
            ActiveAnimation aa = activeAnimations.get(i);
            aa.effect.update(delta);
            if (aa.effect.isFinished()) activeAnimations.removeIndex(i);
            else {
                TextureRegion frame = aa.effect.getKeyFrame();
                Vector2 pos = new Vector2(aa.anchor.getWidth() / 2, aa.anchor.getHeight() / 2);
                aa.anchor.localToStageCoordinates(pos);
                float drawW = frame.getRegionWidth() * 2; float drawH = frame.getRegionHeight() * 2;
                stage.getBatch().draw(frame, pos.x - drawW/2, pos.y - drawH/2, drawW, drawH);
            }
        }
        stage.getBatch().end();
        engine.update(delta);
    }
}
