package game.toweoftrials.screens;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.kotcrab.vis.ui.VisUI;
import game.toweoftrials.Main;
import game.toweoftrials.ecs.CombatSystem;
import game.toweoftrials.ecs.HeroManager;
import game.toweoftrials.ecs.components.*;
import game.toweoftrials.model.*;

public class BattleScreen extends BaseScreen implements CombatSystem.CombatListener {
    
    private final int floor;
    private final Array<Array<String>> waveMonsterIds;
    private final boolean isBossBattle;
    private int currentWaveIndex;
    private final Drawable white;
    private final Texture backgroundTexture;
    
    private final Engine engine;
    private final CombatSystem combatSystem;
    private final ComponentMapper<StatsComponent> sm = ComponentMapper.getFor(StatsComponent.class);
    private final ComponentMapper<APComponent> am = ComponentMapper.getFor(APComponent.class);
    
    private Player player;
    private final Array<Enemy> enemies = new Array<>();
    
    private Label playerHPLabel, playerAPLabel, playerLevelLabel;
    private ProgressBar playerHealthBar;
    private final Array<ProgressBar> enemyHealthBars = new Array<>();
    
    private Table actionArea, combatArea, headerArea;
    private Table playerSide, enemySide;
    
    private TextButton attackBtn, passTurnBtn, fleeBtn;
    private final Array<String> logMessageQueue = new Array<>();
    private boolean isShowingLog = false;
    private boolean combatJustEnded = false;
    private boolean playerWonLast = false;
    private boolean escaped = false;
    
    private Skill selectedSkill = null;
    private boolean isTargeting = false;
    private boolean isSelectingSkill = false;
    private boolean introFinished = false;

    private final ObjectMap<String, Array<Texture>> animationFrames = new ObjectMap<>();
    private final Array<ActiveAnimation> activeAnimations = new Array<>();
    private final ObjectMap<String, Texture> entityTextures = new ObjectMap<>();

    private static class ActiveAnimation {
        AnimationEffect effect;
        Actor anchor;
        ActiveAnimation(AnimationEffect effect, Actor anchor) { this.effect = effect; this.anchor = anchor; }
    }

    public BattleScreen(Main game, int floor, Array<Array<String>> waveMonsterIds) {
        super(game);
        this.floor = floor;
        this.waveMonsterIds = waveMonsterIds;
        this.currentWaveIndex = 0;
        this.isBossBattle = waveMonsterIds.size == 1 && MonsterRegistry.get(waveMonsterIds.get(0).get(0)).type == Enemy.EnemyType.BOSS;
        this.white = VisUI.getSkin().getDrawable("white");
        this.backgroundTexture = new Texture(Gdx.files.internal("background/Dungeon_Poison.png"));
        
        loadSkillAnimations();
        
        this.engine = new Engine();
        this.combatSystem = new CombatSystem(this);
        engine.addSystem(combatSystem);
        
        setupPlayer();
        setupNextWave();
        setupUI();
        
        engine.update(0);
        
        if (isBossBattle) {
            triggerBossDialogue();
        }
        
        introFinished = true;
        combatSystem.resetCombat();
    }

    private void triggerBossDialogue() {
        boolean firstTime = !game.hasMetBoss(floor);
        String dialogue = getBossDialogue(floor, firstTime);
        addLogMessage(dialogue);
        game.markBossMet(floor);
    }

    private String getBossDialogue(int floor, boolean firstTime) {
        if (firstTime) {
            switch (floor) {
                case 1: return "3HEAD SLUG: Fresh meat falls from the ceiling... My heads haven't shared a meal in centuries!";
                case 2: return "3EYE MONSTER: I have seen your birth, your sins, and now... I see your end.";
                case 3: return "OCTOPUS SUMMONER: The sea is so cold... Stay with me, hero. Let the waves take your breath away.";
                case 4: return "PLAGUE DOCTOR: Your pulse is... irregular. Don't worry, my scalpel is precise. I'll make you immortal on these pages.";
                case 5: return "OGRE: NO TALK. ONLY CRUSH. THE TOWER WANTS YOUR BONES!";
                case 6: return "REPTILE WARRIOR: There is no honor in this swamp, only the strong and the dead. Draw your steel.";
                case 7: return "UNDEAD MAGE: Can you hear them? The whispers of everyone you've failed? Join them in my halls.";
                case 8: return "THE LICH: Welcome to the end of the world. Let’s see if you can burn bright enough to end this cycle.";
                default: return "GUARDIAN: You shall not pass.";
            }
        } else {
            switch (floor) {
                case 1: return "3HEAD SLUG: Back for more? My stomachs are still growling from the last time I crushed you!";
                case 2: return "3EYE MONSTER: Persistence is a predictable trait. I've already seen how this battle ends.";
                case 3: return "OCTOPUS SUMMONER: You escaped my embrace once. I won't be so lonely after I drown you this time.";
                case 4: return "PLAGUE DOCTOR: A patient that refuses to stay down? Let's try a more... radical surgery.";
                case 5: return "OGRE: LITTLE THING STILL BREATHES? OGRE BREAK BREATH NOW!";
                case 6: return "REPTILE WARRIOR: You learned nothing from our last duel. A warrior who repeats mistakes is already dead.";
                case 7: return "UNDEAD MAGE: The mansion has many rooms for souls like yours. Why do you struggle against the inevitable?";
                case 8: return "THE LICH: Eternity is a long time to wait for a worthy foe. Did the previous death teach you anything?";
                default: return "GUARDIAN: Again? Your soul is stubborn.";
            }
        }
    }

    private void loadSkillAnimations() {
        Array<Texture> frames = new Array<>();
        for (int i = 1; i <= 5; i++) {
            frames.add(new Texture(Gdx.files.internal("skills/impactvfx/VFXimpact3_frame" + i + ".png")));
        }
        animationFrames.put("impactvfx", frames);
    }

    private void setupPlayer() {
        this.player = HeroManager.getPlayer();
        if (player.getEntity().getComponent(VisualComponent.class) == null) {
            player.getEntity().add(new VisualComponent("player/img.png")); 
        }
        if (player.getEntity().getComponent(StatusComponent.class) == null) {
            player.getEntity().add(new StatusComponent());
        }
        engine.addEntity(player.getEntity());
    }

    private void setupNextWave() {
        if (currentWaveIndex >= waveMonsterIds.size) return;
        Array<String> ids = waveMonsterIds.get(currentWaveIndex);
        
        for (Enemy e : enemies) engine.removeEntity(e.getEntity());
        enemies.clear();
        enemyHealthBars.clear();

        for (String id : ids) {
            MonsterData data = MonsterRegistry.get(id);
            if (data == null) continue;

            Entity enemyEntity = new Entity();
            enemyEntity.add(new StatsComponent(data.name, data.hp, data.attack, data.defense, data.speed));
            enemyEntity.add(new APComponent(0));
            enemyEntity.add(new BattleComponent(false));
            enemyEntity.add(new AbilitiesComponent());
            enemyEntity.add(new VisualComponent(data.texturePath));
            enemyEntity.add(new StatusComponent());
            engine.addEntity(enemyEntity);
            enemies.add(new Enemy(enemyEntity, data.type));
        }
    }

    private void setupUI() {
        root.clear();
        Image background = new Image(backgroundTexture);
        background.setFillParent(true);
        root.addActor(background);

        Table uiTable = new Table();
        uiTable.setFillParent(true);
        root.addActor(uiTable);

        headerArea = new Table();
        headerArea.setBackground(VisUI.getSkin().getDrawable("window"));
        uiTable.add(headerArea).top().pad(10).row();
        
        combatArea = new Table();
        uiTable.add(combatArea).grow().row();
        
        actionArea = new Table();
        actionArea.setBackground(VisUI.getSkin().getDrawable("window"));
        uiTable.add(actionArea).height(Gdx.graphics.getHeight() * 0.45f).growX();

        setupHeaderUI();
        setupCombatUI();
        setupActionUI();
        
        updateLabels();
    }

    private void setupHeaderUI() {
        headerArea.clear();
        String titleStr = "FLOOR " + floor + (waveMonsterIds.size > 1 ? " - WAVE " + (currentWaveIndex + 1) + "/" + waveMonsterIds.size : "");
        headerArea.add(new Label(titleStr, VisUI.getSkin())).pad(10);
    }

    private void setupCombatUI() {
        combatArea.clear();
        Table battleLayout = new Table();
        
        enemySide = new Table();
        if (enemies.size == 1) {
            enemySide.add(createEntityTable(enemies.get(0), Color.RED)).center();
        } else if (enemies.size == 2) {
            enemySide.add(createEntityTable(enemies.get(0), Color.RED)).pad(10).row();
            enemySide.add(createEntityTable(enemies.get(1), Color.RED)).pad(10);
        } else {
            Table backCol = new Table();
            backRow(backCol, 0, 1);
            enemySide.add(backCol).padRight(40);
            
            Table frontCol = new Table();
            frontRow(frontCol, 2, enemies.size);
            enemySide.add(frontCol).padLeft(10);
        }
        
        playerSide = createEntityTable(player, Color.BLUE);
        playerLevelLabel = (Label) playerSide.getChildren().get(2);
        playerHPLabel = (Label) playerSide.getChildren().get(3);
        playerHealthBar = (ProgressBar) playerSide.getChildren().get(4);
        playerAPLabel = (Label) playerSide.getChildren().get(5);

        battleLayout.add(enemySide).expand().center();
        battleLayout.add(playerSide).expand().center();
        combatArea.add(battleLayout).grow();
    }

    private void backRow(Table t, int start, int end) {
        for (int i = start; i <= end && i < enemies.size; i++) {
            t.add(createEntityTable(enemies.get(i), Color.RED)).pad(10).row();
        }
    }

    private void frontRow(Table t, int start, int end) {
        for (int i = start; i < end; i++) {
            t.add(createEntityTable(enemies.get(i), Color.RED)).pad(10).row();
        }
    }

    private Table createEntityTable(final GameEntity ge, Color fallback) {
        Table t = new Table();
        VisualComponent vis = ge.getEntity().getComponent(VisualComponent.class);
        Image img = createEntityImage(vis, fallback);
        
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

        boolean isPlayer = ge instanceof Player;
        t.add(img).size(isPlayer ? 80 : 110).pad(5).row();
        
        if (isPlayer) {
            t.add(new Label(ge.getName(), VisUI.getSkin())).row(); 
            t.add(new Label("", VisUI.getSkin())).row(); 
            t.add(new Label("", VisUI.getSkin())).row(); 
        }

        ProgressBar hpBar = new ProgressBar(0, ge.getStats().maxHp, 1, false, VisUI.getSkin());
        hpBar.setValue(ge.getStats().hp);
        t.add(hpBar).width(120).pad(2).row(); 
        
        if (isPlayer) {
            t.add(new Label("", VisUI.getSkin())).row(); 
        } else {
            enemyHealthBars.add(hpBar);
        }
        
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
        else if (isSelectingSkill) showSkillsUI();
        else showButtonsUI();
    }

    private void showLogUI() {
        actionArea.clear();
        String message = logMessageQueue.first();
        Label msgLabel = new Label(message, VisUI.getSkin());
        msgLabel.setWrap(true); msgLabel.setAlignment(Align.center);
        actionArea.add(msgLabel).width(600).growY().center().row();
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
            isShowingLog = false;
            if (escaped) {
                player.healFull();
                game.setScreen(new HubScreen(game));
                return;
            }

            setupActionUI();
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
        
        attackBtn = createStyledButton("Attack");
        fleeBtn = createStyledButton("Flee (2 AP)");
        passTurnBtn = createStyledButton("Pass Turn");

        float btnWidth = 350;
        float pad = 10;
        buttons.add(attackBtn).width(btnWidth).pad(pad);
        buttons.add(fleeBtn).width(btnWidth).pad(pad);
        buttons.add(passTurnBtn).width(btnWidth).pad(pad);
        actionArea.add(buttons).grow().center();

        attackBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) { isSelectingSkill = true; setupActionUI(); }
        });
        fleeBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                APComponent pa = player.getAP();
                if (pa.currentAP >= 2) {
                    pa.currentAP -= 2;
                    escaped = true;
                    addLogMessage("Escaped from battle!");
                } else {
                    addLogMessage("Not enough AP to flee!");
                }
            }
        });
        passTurnBtn.addListener(new ChangeListener() { @Override public void changed(ChangeEvent event, Actor actor) { combatSystem.nextTurn(); }});
        updateLabels();
    }

    private void showSkillsUI() {
        actionArea.clear();
        actionArea.add(new Label("SELECT A SKILL AND A TARGET", VisUI.getSkin())).pad(5).row();
        
        Table main = new Table();
        Table listTable = new Table();
        ScrollPane scroll = new ScrollPane(listTable, VisUI.getSkin());
        scroll.setFadeScrollBars(false);

        final Table detailArea = new Table();
        detailArea.setBackground(VisUI.getSkin().getDrawable("window"));

        AbilitiesComponent ac = player.getEntity().getComponent(AbilitiesComponent.class);
        for (final Skill skill : ac.skills) {
            TextButton btn = createStyledButton(skill.getName());
            boolean canUse = am.get(player.getEntity()).currentAP >= skill.getApCost() && ac.isReady(skill.getName());
            if (!canUse) {
                btn.setDisabled(true);
                updateButtonFontColor(btn);
            }
            btn.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    selectedSkill = skill;
                    if (skill.getType() == Skill.SkillType.DEFENSIVE || skill.getType() == Skill.SkillType.HEAL) {
                        isTargeting = false;
                        executeSkill(player.getEntity()); // Auto-target self
                    } else {
                        isTargeting = true;
                        showSkillDetail(detailArea, skill);
                    }
                }
            });
            listTable.add(btn).width(250).pad(2).row();
        }

        TextButton backBtn = createStyledButton("Back");
        backBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                isSelectingSkill = false;
                isTargeting = false;
                selectedSkill = null;
                setupActionUI();
            }
        });
        listTable.add(backBtn).width(250).padTop(10);

        main.add(scroll).width(300).growY().pad(10);
        main.add(detailArea).grow().pad(10);
        actionArea.add(main).grow();
    }

    private void showSkillDetail(Table area, final Skill skill) {
        area.clear();
        area.add(new Label(skill.getName().toUpperCase(), VisUI.getSkin())).pad(10).row();
        Label desc = new Label(skill.getDescription(), VisUI.getSkin());
        desc.setWrap(true); desc.setAlignment(Align.center);
        area.add(desc).width(300).pad(10).row();
        
        Table stats = new Table();
        stats.add(new Label("Cost: " + skill.getApCost() + " AP", VisUI.getSkin())).pad(5).row();
        stats.add(new Label("Cooldown: " + skill.getCooldown() + " turns", VisUI.getSkin())).pad(5).row();
        area.add(stats).pad(10).row();
    }

    private void executeSkill(Entity target) {
        if (selectedSkill != null) {
            combatSystem.performSkill(player.getEntity(), target, selectedSkill);
            isTargeting = false;
            selectedSkill = null;
            isSelectingSkill = false; // Close skill menu after use
            setupActionUI();
        }
    }

    private void showEndCombatUI() {
        actionArea.clear();
        actionArea.add(new Label(playerWonLast ? "BATTLE WON" : "BATTLE LOST", VisUI.getSkin())).pad(10).row();
        
        if (playerWonLast) {
            processRewards();
        }

        if (playerWonLast && currentWaveIndex < waveMonsterIds.size - 1) {
            TextButton nextBtn = createStyledButton("Next Battle");
            nextBtn.addListener(new ChangeListener() {
                @Override public void changed(ChangeEvent event, Actor actor) {
                    currentWaveIndex++; combatJustEnded = false; setupNextWave(); 
                    setupHeaderUI(); setupCombatUI(); combatSystem.resetCombat();
                }
            });
            actionArea.add(nextBtn).width(200);
        } else {
            TextButton returnBtn = createStyledButton("Return");
            returnBtn.addListener(new ChangeListener() {
                @Override public void changed(ChangeEvent event, Actor actor) { game.setScreen(new HubScreen(game)); }
            });
            actionArea.add(returnBtn).width(200);
        }
    }

    private void processRewards() {
        int roll = MathUtils.random(1, 100);
        int threshold = 15;
        if (isBossBattle) threshold = 100;
        else if (currentWaveIndex == 1) threshold = 30;
        else if (currentWaveIndex == 2) threshold = 50;

        if (roll <= threshold) {
            String itemStr = getFloorItem();
            Item item = ItemRegistry.get(itemStr);
            if (item != null) {
                player.addItem(item);
                addLogMessage("LOOT: Found " + item.getName() + "!");
            }
        }

        AbilitiesComponent ac = player.getEntity().getComponent(AbilitiesComponent.class);
        if (isBossBattle && !game.isBossCleared(floor)) {
            String sName = getBossSkillName();
            Skill s = SkillRegistry.get(sName);
            if (s != null && !hasSkill(ac, sName)) {
                ac.skills.add(s);
                game.markBossCleared(floor);
                addLogMessage("UNLOCKED: New Skill '" + sName + "'!");
            }
        } else if (!isBossBattle && currentWaveIndex == waveMonsterIds.size - 1 && !game.isDungeonCleared(floor)) {
            String sName = getDungeonSkillName();
            Skill s = SkillRegistry.get(sName);
            if (s != null && !hasSkill(ac, sName)) {
                ac.skills.add(s);
                game.markDungeonCleared(floor);
                addLogMessage("UNLOCKED: New Skill '" + sName + "'!");
            }
        }
        game.saveGame();
    }

    private boolean hasSkill(AbilitiesComponent ac, String name) {
        for (Skill s : ac.skills) if (s.getName().equals(name)) return true;
        return false;
    }

    private String getFloorItem() {
        switch (floor) {
            case 1: return MathUtils.randomBoolean() ? "Thick Slime Robe" : "Rusted Gremlin Blade";
            case 2: return "Piercing Gaze Rapier";
            case 3: return "Sharp Chitin Scimitar";
            case 4: return "Plague Doctor's Scalpel";
            case 5: return "Giant Ogre Fang";
            case 6: return "Swamp Light Katana";
            case 7: return "Black Marble Sword";
            case 8: return "Knight's Soul Blade";
            default: return "Oxidized Copper Ring";
        }
    }

    private String getDungeonSkillName() {
        switch (floor) {
            case 1: return "Slime Skin";
            case 2: return "Hidden Vision";
            case 3: return "Abyssal Shell";
            case 4: return "Incandescent Pages";
            case 5: return "Lethal Stinger";
            case 6: return "Cold-Blooded Stance";
            case 7: return "Phantasmal Howl";
            case 8: return "Bone Prison";
            default: return "";
        }
    }

    private String getBossSkillName() {
        switch (floor) {
            case 1: return "Acid Spit";
            case 2: return "Abyssal Gaze";
            case 3: return "Crushing Tentacles";
            case 4: return "Plague Miasma";
            case 5: return "Primal Fury";
            case 6: return "Shogun's Shadow Slash";
            case 7: return "Ectoplasmic Drain";
            case 8: return "Soul Reaper";
            default: return "";
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
        playerHealthBar.setRange(0, ps.maxHp);
        playerHealthBar.setValue(ps.hp);
        playerAPLabel.setText("AP: " + pa.currentAP + "/" + pa.maxAP);

        for (int i = 0; i < enemies.size; i++) {
            Enemy e = enemies.get(i);
            if (i < enemyHealthBars.size) {
                ProgressBar hpBar = enemyHealthBars.get(i);
                StatsComponent es = e.getStats();
                hpBar.setRange(0, es.maxHp);
                hpBar.setValue(es.hp);
                
                Actor eTable = findActorForEntity(e.getEntity());
                if (eTable != null) eTable.getColor().a = es.hp <= 0 ? 0.5f : 1.0f;
            }
        }

        if (attackBtn != null) {
            if (combatSystem.getActiveEntity() == player.getEntity() && !combatSystem.isCombatEnded() && !isShowingLog) {
                AbilitiesComponent ac = player.getEntity().getComponent(AbilitiesComponent.class);
                boolean canAny = false;
                for (Skill s : ac.skills) if (pa.currentAP >= s.getApCost() && ac.isReady(s.getName())) canAny = true;
                
                attackBtn.setDisabled(!canAny);
                fleeBtn.setDisabled(pa.currentAP < 2);
                passTurnBtn.setDisabled(false);
                updateButtonFontColor(attackBtn);
                updateButtonFontColor(fleeBtn);
                updateButtonFontColor(passTurnBtn);
            } else {
                disableActions();
            }
        }
    }

    private void disableActions() {
        if (attackBtn == null) return;
        attackBtn.setDisabled(true); fleeBtn.setDisabled(true); passTurnBtn.setDisabled(true);
        updateButtonFontColor(attackBtn); updateButtonFontColor(fleeBtn); updateButtonFontColor(passTurnBtn);
    }

    private TextButton createStyledButton(String text) {
        TextButton button = new TextButton(text, VisUI.getSkin());
        button.setColor(Color.WHITE);
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
            if (enemies.get(i).getEntity() == e) {
                if (enemies.size > 2) {
                    if (i < 2) return ((Table)enemySide.getChildren().get(0)).getChildren().get(i);
                    else return enemySide.getChildren().get(1);
                }
                return enemySide.getChildren().get(i);
            }
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
        if (backgroundTexture != null) backgroundTexture.dispose();
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
