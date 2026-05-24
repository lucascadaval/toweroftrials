package game.toweoftrials.screens;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
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
    private final ComponentMapper<APComponent> am = ComponentMapper.getFor(APComponent.class);
    private final ComponentMapper<AbilitiesComponent> abm = ComponentMapper.getFor(AbilitiesComponent.class);
    private final ComponentMapper<LevelComponent> lm = ComponentMapper.getFor(LevelComponent.class);
    
    private Player player;
    private Enemy enemy;
    
    private Label playerHPLabel;
    private Label playerAPLabel;
    private Label playerLevelLabel;
    private Label enemyHPLabel;
    
    private Table actionArea;
    private Table inventoryArea;
    private Table equipmentArea;
    private Table combatArea;
    
    private Table playerSide;
    private Table enemySide;
    
    private TextButton attackBtn, skill1Btn, skill2Btn, ultimateBtn, passTurnBtn, guardBtn;
    
    private final ObjectMap<String, Array<Texture>> animationFrames = new ObjectMap<>();
    private final Array<ActiveAnimation> activeAnimations = new Array<>();
    
    private static class ActiveAnimation {
        AnimationEffect effect;
        Actor anchor;
        
        ActiveAnimation(AnimationEffect effect, Actor anchor) {
            this.effect = effect;
            this.anchor = anchor;
        }
    }
    
    private final Array<String> logMessageQueue = new Array<>();
    private boolean isShowingLog = false;
    private boolean combatJustEnded = false;
    private boolean playerWonLast = false;

    private final Skill basicAttack = new Skill("Basic Attack", Skill.SkillType.DAMAGE, 1, 0, 1.0f, "double_slash");
    private final Skill quickStrike = new Skill("Quick Strike", Skill.SkillType.DAMAGE, 1, 0, 1.2f, null);
    private final Skill heavySmash = new Skill("Heavy Smash", Skill.SkillType.DAMAGE, 2, 1, 1.8f, null);
    private final Skill ultimate = new Skill("ULTIMATE", Skill.SkillType.DAMAGE, 4, 3, 3.5f, null);
    private final Skill guard = new Skill("Guard", Skill.SkillType.DEFENSE, 1, 0, 1.5f, null);
    private final Skill doubleSlash = new Skill("Double Slash", Skill.SkillType.DAMAGE, 2, 1, 2.2f, "double_slash");

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
        
        loadSkillAnimations();
        
        this.engine = new Engine();
        this.combatSystem = new CombatSystem(this);
        engine.addSystem(combatSystem);
        
        setupPlayer();
        setupNextEncounter();
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
            player.getEntity().add(new VisualComponent(null)); 
        }
        engine.addEntity(player.getEntity());
    }

    private void setupNextEncounter() {
        if (currentEncounterIndex >= encounters.size) return;
        Enemy.EnemyType type = encounters.get(currentEncounterIndex);
        if (enemy != null) engine.removeEntity(enemy.getEntity());

        Entity enemyEntity = new Entity();
        String texPath = null;
        switch (type) {
            case BOSS: enemyEntity.add(new StatsComponent("Boss Floor " + floor, 500, 40, 20, 10)); break;
            case MINI_BOSS: enemyEntity.add(new StatsComponent("Elite Guardian", 200, 30, 15, 12)); break;
            default: 
                enemyEntity.add(new StatsComponent("Slime", 50, 15, 5, 12));
                texPath = "monsters/slime_blue.png";
                break;
        }
        enemyEntity.add(new APComponent(5));
        enemyEntity.add(new BattleComponent(false));
        enemyEntity.add(new AbilitiesComponent());
        enemyEntity.add(new VisualComponent(texPath));
        engine.addEntity(enemyEntity);
        this.enemy = new Enemy(enemyEntity, type);
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
        equipmentArea.add().grow();
    }

    private Table createEquipSlot(String name) {
        Table slot = new Table();
        Image icon = new Image(white);
        icon.setColor(Color.DARK_GRAY);
        slot.add(icon).size(32).pad(5);
        slot.add(new Label(name, VisUI.getSkin())).left();
        return slot;
    }

    private final ObjectMap<String, Texture> entityTextures = new ObjectMap<>();

    private void setupCombatUI() {
        combatArea.clear();
        
        Enemy.EnemyType type = encounters.get(currentEncounterIndex);
        String titleStr = "FLOOR " + floor + (encounters.size > 1 ? " (" + (currentEncounterIndex + 1) + "/" + encounters.size + ")" : "");
        combatArea.add(new Label(titleStr, VisUI.getSkin())).colspan(2).pad(5).row();

        Table battleLayout = new Table();
        
        playerSide = new Table();
        VisualComponent pVis = player.getEntity().getComponent(VisualComponent.class);
        Image playerImg = createEntityImage(pVis, Color.BLUE);
        playerSide.add(playerImg).size(120).pad(10).row();
        playerSide.add(new Label(player.getName(), VisUI.getSkin())).row();
        playerLevelLabel = new Label("", VisUI.getSkin()); playerSide.add(playerLevelLabel).row();
        playerHPLabel = new Label("", VisUI.getSkin()); playerSide.add(playerHPLabel).row();
        playerAPLabel = new Label("", VisUI.getSkin()); playerSide.add(playerAPLabel).row();
        
        enemySide = new Table();
        VisualComponent eVis = enemy.getEntity().getComponent(VisualComponent.class);
        Color enemyDefColor = Color.RED;
        if (type == Enemy.EnemyType.BOSS) enemyDefColor = Color.PURPLE;
        else if (type == Enemy.EnemyType.MINI_BOSS) enemyDefColor = Color.ORANGE;
        
        Image enemyImg = createEntityImage(eVis, enemyDefColor);
        float eSize = type == Enemy.EnemyType.BOSS ? 200 : (type == Enemy.EnemyType.MINI_BOSS ? 160 : 120);
        enemySide.add(enemyImg).size(eSize).pad(10).row();
        enemySide.add(new Label(enemy.getName(), VisUI.getSkin())).row();
        enemyHPLabel = new Label("", VisUI.getSkin()); enemySide.add(enemyHPLabel).row();

        battleLayout.add(enemySide).expandX().center();
        battleLayout.add(playerSide).expandX().center();
        
        combatArea.add(battleLayout).grow().center();
    }

    private Image createEntityImage(VisualComponent vis, Color fallbackColor) {
        if (vis != null && vis.texturePath != null) {
            if (!entityTextures.containsKey(vis.texturePath)) {
                entityTextures.put(vis.texturePath, new Texture(Gdx.files.internal(vis.texturePath)));
            }
            return new Image(entityTextures.get(vis.texturePath));
        } else {
            Image img = new Image(white);
            img.setColor(fallbackColor);
            return img;
        }
    }

    private void setupActionUI() {
        actionArea.clear();
        actionArea.clearListeners();

        if (isShowingLog && logMessageQueue.size > 0) {
            showLogUI();
        } else if (combatJustEnded) {
            showEndCombatUI();
        } else {
            showButtonsUI();
        }
    }

    private void showLogUI() {
        actionArea.clear();
        String message = logMessageQueue.first();
        
        Label msgLabel = new Label(message, VisUI.getSkin());
        msgLabel.setWrap(true);
        msgLabel.setAlignment(Align.center);
        
        actionArea.add(msgLabel).width(500).growY().center().row();
        actionArea.add(new Label("(Click here to continue...)", VisUI.getSkin())).pad(10).bottom();

        actionArea.setTouchable(Touchable.enabled);
        actionArea.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                proceedLog();
                return true;
            }
        });
    }

    private void proceedLog() {
        if (logMessageQueue.size > 0) {
            logMessageQueue.removeIndex(0);
        }
        
        if (logMessageQueue.size == 0) {
            isShowingLog = false;
            setupActionUI();

            // Check if we need to trigger the next AI action
            Entity active = combatSystem.getActiveEntity();
            if (active != null) {
                 BattleComponent bc = active.getComponent(BattleComponent.class);
                 if (bc != null && !bc.isDead && !bc.isPlayer && !combatSystem.isCombatEnded()) {
                     combatSystem.executeOneAIAction();
                 }
            }
        }
 else {
            showLogUI();
        }
    }

    private void showButtonsUI() {
        actionArea.clear();
        actionArea.add(new Label("ACTIONS", VisUI.getSkin())).pad(10).colspan(2).row();

        Table buttons = new Table();
        attackBtn = createStyledButton("Basic Attack (1 AP)");
        skill1Btn = createStyledButton("Quick Strike (1 AP)");
        skill2Btn = createStyledButton("Double Slash (2 AP)");
        ultimateBtn = createStyledButton("ULTIMATE (4 AP)");
        guardBtn = createStyledButton("Guard (1 AP)");
        passTurnBtn = createStyledButton("Pass Turn");

        float btnWidth = 320;
        float pad = 10;

        buttons.add(attackBtn).width(btnWidth).pad(pad).expandX();
        buttons.add(skill1Btn).width(btnWidth).pad(pad).expandX().row();
        buttons.add(skill2Btn).width(btnWidth).pad(pad).expandX();
        buttons.add(ultimateBtn).width(btnWidth).pad(pad).expandX().row();
        buttons.add(guardBtn).width(btnWidth).pad(pad).expandX();
        buttons.add(passTurnBtn).width(btnWidth).pad(pad).expandX().row();

        actionArea.add(buttons).grow();

        attackBtn.addListener(new ChangeListener() { @Override public void changed(ChangeEvent event, Actor actor) { combatSystem.performSkill(player.getEntity(), enemy.getEntity(), basicAttack); }});
        skill1Btn.addListener(new ChangeListener() { @Override public void changed(ChangeEvent event, Actor actor) { combatSystem.performSkill(player.getEntity(), enemy.getEntity(), quickStrike); }});
        skill2Btn.addListener(new ChangeListener() { @Override public void changed(ChangeEvent event, Actor actor) { combatSystem.performSkill(player.getEntity(), enemy.getEntity(), doubleSlash); }});
        ultimateBtn.addListener(new ChangeListener() { @Override public void changed(ChangeEvent event, Actor actor) { combatSystem.performSkill(player.getEntity(), enemy.getEntity(), ultimate); }});
        guardBtn.addListener(new ChangeListener() { @Override public void changed(ChangeEvent event, Actor actor) { combatSystem.performSkill(player.getEntity(), null, guard); }});
        passTurnBtn.addListener(new ChangeListener() { @Override public void changed(ChangeEvent event, Actor actor) { combatSystem.nextTurn(); }});
        
        updateLabels();
    }

    private void showEndCombatUI() {
        actionArea.clear();
        actionArea.add(new Label(playerWonLast ? "BATTLE WON" : "BATTLE LOST", VisUI.getSkin())).pad(10).row();
        
        if (playerWonLast && currentEncounterIndex < encounters.size - 1) {
            TextButton nextBtn = createStyledButton("Next Battle");
            nextBtn.addListener(new ChangeListener() {
                @Override public void changed(ChangeEvent event, Actor actor) {
                    currentEncounterIndex++;
                    combatJustEnded = false;
                    setupNextEncounter();
                    setupUI();
                    combatSystem.resetCombat();
                }
            });
            actionArea.add(nextBtn).width(200);
        } else {
            TextButton returnBtn = createStyledButton("Return");
            returnBtn.addListener(new ChangeListener() {
                @Override public void changed(ChangeEvent event, Actor actor) {
                    game.setScreen(new FloorMenuScreen(game, floor));
                }
            });
            actionArea.add(returnBtn).width(200);
        }
    }

    private void addLogMessage(String msg) {
        logMessageQueue.add(msg);
        if (!isShowingLog) {
            isShowingLog = true;
            setupActionUI();
        }
    }

    private void updateLabels() {
        if (player == null || enemy == null || playerHPLabel == null) return;
        
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
        
        if (attackBtn != null) {
            updateButtonText(skill1Btn, "Quick Strike (1 AP)", pab, "Quick Strike");
            updateButtonText(skill2Btn, "Double Slash (2 AP, CD: 1)", pab, "Double Slash");
            updateButtonText(ultimateBtn, "ULTIMATE (4 AP, CD: 3)", pab, "ULTIMATE");

            if (combatSystem.getActiveEntity() == player.getEntity() && !combatSystem.isCombatEnded() && !isShowingLog) {
                attackBtn.setDisabled(pa.currentAP < 1);
                skill1Btn.setDisabled(pa.currentAP < 1 || !pab.isReady("Quick Strike"));
                skill2Btn.setDisabled(pa.currentAP < 2 || !pab.isReady("Double Slash"));
                ultimateBtn.setDisabled(pa.currentAP < 4 || !pab.isReady("ULTIMATE"));
                guardBtn.setDisabled(pa.currentAP < 1);
                passTurnBtn.setDisabled(false);
            } else {
                disableActions();
            }
        }
    }

    private void updateButtonText(TextButton button, String baseText, AbilitiesComponent abilities, String skillName) {
        if (button == null) return;
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
            Image item = new Image(white);
            item.setColor(Color.GRAY);
            grid.add(item).size(32).pad(2);
            if((i+1)%3 == 0) grid.row();
        }
        inventoryArea.add(grid).grow();
    }

    @Override public void onTurnStarted(Entity entity) {
        String msg = "It's " + sm.get(entity).name + "'s turn!";
        addLogMessage(msg);
    }

    @Override public void onActionResolved(String message) {
        addLogMessage(message);
    }

    @Override public void onUpdateUI() {
    }

    @Override
    public void onAnimationRequested(Entity attacker, Entity target, String animationName) {
        Actor targetActor = (target == player.getEntity()) ? playerSide : enemySide;
        Actor attackerActor = (attacker == player.getEntity()) ? playerSide : enemySide;
        
        // Dash and Shake Effects for all attacks
        if (attacker != target) {
            // Attacker dashes towards target
            float dashDist = 40f;
            if (attackerActor == playerSide) dashDist = -40f; // Player dashes left
            attackerActor.addAction(Actions.sequence(
                Actions.moveBy(dashDist, 0, 0.1f),
                Actions.moveBy(-dashDist, 0, 0.1f)
            ));
            
            // Target shakes upon impact
            targetActor.addAction(Actions.sequence(
                Actions.moveBy(10, 0, 0.05f),
                Actions.moveBy(-20, 0, 0.05f),
                Actions.moveBy(10, 0, 0.05f)
            ));
        }

        if (animationName != null && animationFrames.containsKey(animationName)) {
            Array<Texture> frames = animationFrames.get(animationName);
            AnimationEffect effect = new AnimationEffect(frames, 0.05f);
            activeAnimations.add(new ActiveAnimation(effect, targetActor));
        }
    }

    @Override
    public void onCombatEnded(boolean playerWon) {
        this.playerWonLast = playerWon;
        this.combatJustEnded = true;
        addLogMessage(playerWon ? "Victory! All enemies defeated." : "Defeat... You were overwhelmed.");
        player.healFull();
    }

    @Override
    public void dispose() {
        super.dispose();
        if (slimeTexture != null) slimeTexture.dispose();
        for (Array<Texture> frames : animationFrames.values()) {
            for (Texture t : frames) t.dispose();
        }
        for (Texture t : entityTextures.values()) {
            t.dispose();
        }
    }

    @Override
    public void hide() {
        super.hide();
        if (engine != null) engine.removeAllEntities();
    }

    @Override
    public void render(float delta) {
        updateLabels();
        super.render(delta);
        
        stage.getBatch().begin();
        for (int i = activeAnimations.size - 1; i >= 0; i--) {
            ActiveAnimation aa = activeAnimations.get(i);
            aa.effect.update(delta);
            if (aa.effect.isFinished()) {
                activeAnimations.removeIndex(i);
            } else {
                TextureRegion frame = aa.effect.getKeyFrame();
                Vector2 pos = new Vector2(aa.anchor.getWidth() / 2, aa.anchor.getHeight() / 2);
                aa.anchor.localToStageCoordinates(pos);
                float drawW = frame.getRegionWidth() * 2;
                float drawH = frame.getRegionHeight() * 2;
                stage.getBatch().draw(frame, pos.x - drawW/2, pos.y - drawH/2, drawW, drawH);
            }
        }
        stage.getBatch().end();
        
        engine.update(delta);
    }
}
