package game.toweoftrials.ecs;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import game.toweoftrials.ecs.components.*;
import game.toweoftrials.model.Skill;

import java.util.Comparator;

public class CombatSystem extends EntitySystem {
    private final ComponentMapper<StatsComponent> sm = ComponentMapper.getFor(StatsComponent.class);
    private final ComponentMapper<APComponent> am = ComponentMapper.getFor(APComponent.class);
    private final ComponentMapper<BattleComponent> bm = ComponentMapper.getFor(BattleComponent.class);
    private final ComponentMapper<AbilitiesComponent> abm = ComponentMapper.getFor(AbilitiesComponent.class);
    private final ComponentMapper<LevelComponent> lm = ComponentMapper.getFor(LevelComponent.class);
    private final ComponentMapper<StatusComponent> stm = ComponentMapper.getFor(StatusComponent.class);

    private ImmutableArray<Entity> combatants;
    private final Array<Entity> turnQueue = new Array<>();
    private Entity activeEntity;
    private final CombatListener listener;
    private boolean combatEnded = false;

    public interface CombatListener {
        void onTurnStarted(Entity entity);
        void onActionResolved(String message);
        void onCombatEnded(boolean playerWon);
        void onUpdateUI();
        void onAnimationRequested(Entity attacker, Entity target, String animationName);
        void onFloatingTextRequested(Entity target, String text, Color color);
    }

    public CombatSystem(CombatListener listener) {
        this.listener = listener;
    }

    @Override
    public void addedToEngine(com.badlogic.ashley.core.Engine engine) {
        combatants = engine.getEntitiesFor(Family.all(StatsComponent.class, BattleComponent.class, AbilitiesComponent.class).get());
    }

    public void startCombat() {
        if (combatEnded) return;
        for (Entity e : combatants) {
            bm.get(e).isDead = false; 
            sm.get(e).shield = 0;
            if (e.getComponent(StatusComponent.class) == null) e.add(new StatusComponent());
            else stm.get(e).activeEffects.clear();
        }
        startNewRound();
    }

    private void startNewRound() {
        if (combatEnded) return;
        turnQueue.clear();
        for (Entity e : combatants) {
            if (!bm.get(e).isDead) {
                turnQueue.add(e);
            }
        }
        
        if (turnQueue.size == 0) {
            checkCombatEnd();
            return;
        }

        sortQueue();
        nextTurn();
    }

    private void sortQueue() {
        turnQueue.sort((o1, o2) -> {
            int s1 = getEffectiveSpeed(o1);
            int s2 = getEffectiveSpeed(o2);
            return Integer.compare(s2, s1);
        });
    }

    private int getEffectiveSpeed(Entity e) {
        StatsComponent s = sm.get(e);
        StatusComponent st = stm.get(e);
        float mod = 1.0f;
        if (st != null) mod -= st.getEffectValue(StatusComponent.EffectType.SPD_DEBUFF);
        return (int) (s.speed * mod);
    }

    public void nextTurn() {
        if (combatEnded) return;
        if (checkCombatEnd()) return;

        if (turnQueue.size == 0) {
            startNewRound(); 
            return;
        }

        activeEntity = turnQueue.removeIndex(0);
        
        BattleComponent bc = bm.get(activeEntity);
        if (bc == null || bc.isDead) {
            nextTurn();
            return;
        }

        tickDoTs(activeEntity);
        if (combatEnded || bc.isDead) {
            nextTurn();
            return;
        }
        
        StatusComponent st = stm.get(activeEntity);
        if (st != null) {
            if (st.hasEffect(StatusComponent.EffectType.STUN)) {
                listener.onActionResolved(sm.get(activeEntity).name + " is stunned and skips turn!");
                endTurn(activeEntity);
                return;
            }
        }
        
        if (bc.isPlayer) {
            APComponent ap = am.get(activeEntity);
            if (ap != null) ap.currentAP = ap.maxAP;
        }

        sm.get(activeEntity).shield = 0;
        abm.get(activeEntity).updateCooldowns();

        listener.onTurnStarted(activeEntity);
    }

    public void endTurn(Entity entity) {
        decrementStatusDurations(entity);
        nextTurn();
    }

    private void tickDoTs(Entity entity) {
        StatusComponent st = stm.get(entity);
        if (st == null) return;
        StatsComponent stats = sm.get(entity);

        for (StatusComponent.StatusEffect effect : st.activeEffects) {
            if (effect.type == StatusComponent.EffectType.POISON || effect.type == StatusComponent.EffectType.BURN) {
                int damage = (int) (stats.maxHp * effect.value);
                stats.hp = Math.max(0, stats.hp - damage);
                listener.onFloatingTextRequested(entity, "-" + damage, effect.type == StatusComponent.EffectType.POISON ? Color.LIME : Color.ORANGE);
                if (stats.hp <= 0) {
                    bm.get(entity).isDead = true;
                    listener.onActionResolved(stats.name + " succumbed to " + effect.type + "!");
                    checkCombatEnd();
                    return;
                }
            }
        }
        listener.onUpdateUI();
    }

    private void decrementStatusDurations(Entity entity) {
        StatusComponent st = stm.get(entity);
        if (st == null) return;
        for (int i = st.activeEffects.size - 1; i >= 0; i--) {
            st.activeEffects.get(i).duration--;
            if (st.activeEffects.get(i).duration <= 0) {
                st.activeEffects.removeIndex(i);
            }
        }
    }

    public void executeOneAIAction() {
        if (combatEnded || activeEntity == null) return;
        BattleComponent activeBc = bm.get(activeEntity);
        if (activeBc == null || activeBc.isDead || activeBc.isPlayer) return;
        
        Entity player = getPlayer();
        if (player == null || bm.get(player).isDead) {
            endTurn(activeEntity);
            return;
        }

        performAttack(activeEntity, player, "Attack", 0, 0, 0, 1.0f);
        listener.onAnimationRequested(activeEntity, player, null);

        if (!combatEnded) {
            endTurn(activeEntity); 
        }
    }

    public boolean performSkill(Entity attacker, Entity target, Skill skill) {
        if (combatEnded) return false;

        boolean success = false;
        if (skill.isAoE) {
            success = executeAoESkill(attacker, skill);
        } else {
            success = executeSingleTargetSkill(attacker, target, skill);
        }
        return success;
    }

    private boolean executeSingleTargetSkill(Entity attacker, Entity target, Skill skill) {
        if (!checkResources(attacker, skill)) return false;
        
        applySkillAction(attacker, target, skill);
        listener.onUpdateUI();
        checkCombatEnd();
        return true;
    }

    private boolean executeAoESkill(Entity attacker, Skill skill) {
        if (!checkResources(attacker, skill)) return false;

        listener.onActionResolved(sm.get(attacker).name + " used " + skill.getName() + " on all targets!");
        
        for (Entity e : combatants) {
            BattleComponent ebc = bm.get(e);
            if (ebc != null && !ebc.isDead && ebc.isPlayer != bm.get(attacker).isPlayer) {
                applySkillAction(attacker, e, skill);
            }
        }
        
        listener.onUpdateUI();
        checkCombatEnd();
        return true;
    }

    private boolean checkResources(Entity attacker, Skill skill) {
        BattleComponent abc = bm.get(attacker);
        if (abc.isPlayer) {
            APComponent ap = am.get(attacker);
            if (ap.currentAP < skill.getApCost()) {
                listener.onActionResolved("Not enough AP!");
                return false;
            }
            ap.currentAP -= skill.getApCost();
        }

        AbilitiesComponent abilities = abm.get(attacker);
        if (!abilities.isReady(skill.getName())) {
            if (abc.isPlayer) listener.onActionResolved(skill.getName() + " is on cooldown!");
            return false;
        }

        if (skill.getCooldown() > 0) abilities.startCooldown(skill.getName(), skill.getCooldown());
        return true;
    }

    private void applySkillAction(Entity attacker, Entity target, Skill skill) {
        StatsComponent a = sm.get(attacker);
        StatsComponent t = sm.get(target);
        StatusComponent aStatus = stm.get(attacker);
        StatusComponent tStatus = stm.get(target);

        if (skill.getType() == Skill.SkillType.HEAL) {
            int heal = 0;
            if (skill.percentHeal > 0) {
                heal = (int) (t.maxHp * skill.percentHeal);
            } else {
                heal = (int) (a.attack * skill.getMultiplier());
            }
            t.hp = Math.min(t.maxHp, t.hp + heal);
            listener.onFloatingTextRequested(target, "+" + heal, Color.GREEN);
            game.toweoftrials.utils.AudioManager.playSound("heal");
        } else if (skill.getType() == Skill.SkillType.DEFENSIVE) {
            int shield = (int) (a.defense * skill.getMultiplier());
            a.shield += shield;
            listener.onFloatingTextRequested(attacker, "GUARD +" + shield, Color.CYAN);
            game.toweoftrials.utils.AudioManager.playSound("buff");
        } else {
            // OFFENSIVE
            game.toweoftrials.utils.AudioManager.playSound("attack");
            float multiplier = skill.getMultiplier();
            boolean isCrit = false;
            float critChance = 0.05f + (aStatus != null ? aStatus.getEffectValue(StatusComponent.EffectType.CRIT_BUFF) : 0);
            if (MathUtils.random() < critChance) {
                multiplier *= 1.5f;
                isCrit = true;
            }

            int damage = (int) (a.attack * multiplier);
            if (skill.detonatePoison && tStatus != null && tStatus.hasEffect(StatusComponent.EffectType.POISON)) {
                damage *= 2;
                listener.onFloatingTextRequested(target, "DETONATE!", Color.YELLOW);
            }
            
            int finalDmg = skill.ignoreDef ? damage : Math.max(1, (int) (damage * (100f / (100f + t.defense))));
            
            if (t.shield > 0) {
                int abs = Math.min(t.shield, finalDmg);
                t.shield -= abs;
                finalDmg -= abs;
                listener.onFloatingTextRequested(target, "ABSORBED!", Color.GRAY);
            }
            
            t.hp = Math.max(0, t.hp - finalDmg);
            listener.onFloatingTextRequested(target, (isCrit ? "CRIT! " : "") + "-" + finalDmg, isCrit ? Color.GOLD : Color.RED);

            if (skill.lifesteal) {
                a.hp = Math.min(a.maxHp, a.hp + finalDmg);
                listener.onFloatingTextRequested(attacker, "+" + finalDmg, Color.GREEN);
            }

            if (t.hp <= 0) {
                bm.get(target).isDead = true;
                turnQueue.removeValue(target, true);
                listener.onActionResolved(t.name + " was defeated!");
                game.toweoftrials.utils.AudioManager.playSound("death");
                if (bm.get(attacker).isPlayer) awardXp(attacker, target);
            }
        }

        // Apply Status Effect (Target)
        if (skill.statusType != null) {
            StatusComponent targetSt = (skill.getType() == Skill.SkillType.DEFENSIVE || skill.getType() == Skill.SkillType.HEAL) ? stm.get(attacker) : stm.get(target);
            if (targetSt != null) {
                targetSt.addEffect(skill.statusType, skill.statusDuration, skill.statusValue, skill.getName());
                listener.onFloatingTextRequested(targetSt == stm.get(attacker) ? attacker : target, skill.statusType.name(), Color.GOLD);
                if (skill.statusType == StatusComponent.EffectType.SPD_DEBUFF || skill.statusType == StatusComponent.EffectType.SPD_BUFF) {
                    sortQueue();
                }
            }
        }

        // Apply Status Effect (Self)
        if (skill.selfStatusType != null) {
            StatusComponent selfSt = stm.get(attacker);
            if (selfSt != null) {
                selfSt.addEffect(skill.selfStatusType, skill.selfStatusDuration, skill.selfStatusValue, skill.getName());
                listener.onFloatingTextRequested(attacker, skill.selfStatusType.name(), Color.GOLD);
            }
        }
        
        listener.onAnimationRequested(attacker, target, skill.getAnimationName());
    }

    public boolean performHealSkill(Entity attacker, Entity target, String actionName, int apCost, int cooldown, float multiplier) {
        return false;
    }

    public boolean performAttack(Entity attacker, Entity target, String actionName, int apCost, int apGain, int cooldown, float multiplier) {
        Skill s = new Skill(actionName, "", Skill.SkillType.OFFENSIVE, apCost, cooldown, multiplier, "impactvfx");
        return executeSingleTargetSkill(attacker, target, s);
    }

    public boolean performDefensiveSkill(Entity entity, String actionName, int apCost, int cooldown, float defenseMultiplier) {
        Skill s = new Skill(actionName, "", Skill.SkillType.DEFENSIVE, apCost, cooldown, defenseMultiplier, "impactvfx");
        return executeSingleTargetSkill(entity, entity, s);
    }

    private void awardXp(Entity playerEntity, Entity defeatedEnemy) {
        StatsComponent enemyStats = sm.get(defeatedEnemy);
        LevelComponent pl = lm.get(playerEntity);
        if (pl == null) return;
        
        int xpGained = enemyStats.attack + enemyStats.defense + (enemyStats.maxHp / 5);
        pl.addXp(xpGained);
        listener.onFloatingTextRequested(playerEntity, "+" + xpGained + " XP", Color.GOLD);
        listener.onActionResolved("Gained " + xpGained + " XP!");

        if (pl.canLevelUp()) {
            pl.levelUp();
            StatsComponent ps = sm.get(playerEntity);
            ps.maxHp += 15;
            ps.hp = ps.maxHp; 
            ps.attack += 3;
            ps.defense += 2;
            ps.speed += 2;
            listener.onActionResolved("LEVEL UP! Reached Level " + pl.level + "!");
        }
    }

    public boolean checkCombatEnd() {
        if (combatEnded) return true;
        if (combatants == null || combatants.size() == 0) return false;

        int playersAlive = 0;
        int enemiesAlive = 0;
        int enemiesFound = 0;

        for (Entity e : combatants) {
            BattleComponent bc = bm.get(e);
            if (bc == null) continue;
            
            if (bc.isPlayer) {
                if (!bc.isDead) playersAlive++;
            } else {
                enemiesFound++;
                if (!bc.isDead) enemiesAlive++;
            }
        }

        if (playersAlive == 0) {
            combatEnded = true;
            listener.onCombatEnded(false);
            return true;
        }

        if (enemiesFound > 0 && enemiesAlive == 0) {
            combatEnded = true;
            listener.onCombatEnded(true);
            return true;
        }

        return false;
    }

    public boolean isCombatEnded() {
        return combatEnded;
    }

    public Entity getActiveEntity() {
        return activeEntity;
    }

    public Entity getPlayer() {
        for (Entity e : combatants) {
            if (bm.get(e).isPlayer) return e;
        }
        return null;
    }

    public void resetCombat() {
        combatEnded = false;
        turnQueue.clear();
        startCombat();
    }
}
