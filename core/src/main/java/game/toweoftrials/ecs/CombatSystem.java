package game.toweoftrials.ecs;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
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
    }

    public CombatSystem(CombatListener listener) {
        this.listener = listener;
    }

    @Override
    public void addedToEngine(com.badlogic.ashley.core.Engine engine) {
        combatants = engine.getEntitiesFor(Family.all(StatsComponent.class, APComponent.class, BattleComponent.class, AbilitiesComponent.class).get());
    }

    @Override
    public void update(float deltaTime) {
        // AI execution is now triggered by the UI to ensure the player can read the log
    }

    public void startCombat() {
        if (combatEnded) return;
        turnQueue.clear();
        for (Entity e : combatants) {
            bm.get(e).isDead = false; 
            sm.get(e).shield = 0;    
            turnQueue.add(e);
        }
        sortQueue();
        nextTurn();
    }

    private void sortQueue() {
        turnQueue.sort((o1, o2) -> Integer.compare(sm.get(o2).speed, sm.get(o1).speed));
    }

    public void nextTurn() {
        if (combatEnded) return;
        if (checkCombatEnd()) return;

        if (turnQueue.size == 0) {
            startCombat(); 
            return;
        }

        activeEntity = turnQueue.removeIndex(0);
        APComponent ap = am.get(activeEntity);
        ap.currentAP = ap.maxAP;
        sm.get(activeEntity).shield = 0;
        abm.get(activeEntity).updateCooldowns();

        listener.onTurnStarted(activeEntity);
    }

    public void executeOneAIAction() {
        if (combatEnded || activeEntity == null || bm.get(activeEntity).isPlayer) return;
        
        APComponent ap = am.get(activeEntity);
        AbilitiesComponent abilities = abm.get(activeEntity);
        Entity player = getPlayer();
        
        if (player == null || bm.get(player).isDead) {
            nextTurn();
            return;
        }

        boolean acted = false;
        if (ap.currentAP >= 4 && abilities.isReady("ULTIMATE")) {
            acted = performAttack(activeEntity, player, "ULTIMATE", 4, 0, 3, 3.5f);
            if (acted) listener.onAnimationRequested(activeEntity, player, null);
        } else if (ap.currentAP >= 2 && abilities.isReady("Heavy Smash")) {
            acted = performAttack(activeEntity, player, "Heavy Smash", 2, 0, 1, 1.8f);
            if (acted) listener.onAnimationRequested(activeEntity, player, null);
        } else if (ap.currentAP >= 1 && abilities.isReady("Quick Strike")) {
            acted = performAttack(activeEntity, player, "Quick Strike", 1, 0, 0, 1.2f);
            if (acted) listener.onAnimationRequested(activeEntity, player, null);
        } else if (ap.currentAP >= 1) { 
            acted = performAttack(activeEntity, player, "Basic Attack", 1, 0, 0, 1.0f);
            if (acted) listener.onAnimationRequested(activeEntity, player, "double_slash");
        }

        if (!acted || combatEnded) {
            if (!combatEnded) nextTurn();
        }
    }

    public boolean performSkill(Entity attacker, Entity target, Skill skill) {
        if (combatEnded) return false;

        boolean success;
        if (skill.getType() == Skill.SkillType.DEFENSE) {
            success = performDefensiveSkill(attacker, skill.getName(), skill.getApCost(), skill.getCooldown(), skill.getMultiplier());
            if (success && skill.getAnimationName() != null) {
                listener.onAnimationRequested(attacker, attacker, skill.getAnimationName());
            }
        } else {
            success = performAttack(attacker, target, skill.getName(), skill.getApCost(), 0, skill.getCooldown(), skill.getMultiplier());
            if (success && skill.getAnimationName() != null) {
                listener.onAnimationRequested(attacker, target, skill.getAnimationName());
            }
        }
        return success;
    }

    @SuppressWarnings("UnusedReturnValue")
    public boolean performAttack(Entity attacker, Entity target, String actionName, int apCost, int apGain, int cooldown, float multiplier) {
        if (combatEnded) return false;
        
        APComponent ap = am.get(attacker);
        AbilitiesComponent abilities = abm.get(attacker);

        if (ap.currentAP < apCost) {
            if (bm.get(attacker).isPlayer) listener.onActionResolved("Not enough AP for " + actionName);
            return false;
        }
        if (!abilities.isReady(actionName)) {
            if (bm.get(attacker).isPlayer) listener.onActionResolved(actionName + " is on cooldown! (" + abilities.getRemainingCooldown(actionName) + " turns left)");
            return false;
        }

        ap.currentAP -= apCost;
        ap.currentAP = Math.min(ap.maxAP, ap.currentAP + apGain);
        if (cooldown > 0) abilities.startCooldown(actionName, cooldown);

        StatsComponent aStats = sm.get(attacker);
        StatsComponent tStats = sm.get(target);

        int rawDamage = (int) (aStats.attack * multiplier);
        int finalDamage = Math.max(1, rawDamage - tStats.defense);
        
        if (tStats.shield > 0) {
            int absorbed = Math.min(tStats.shield, finalDamage);
            tStats.shield -= absorbed;
            finalDamage -= absorbed;
            listener.onActionResolved("Shield absorbed " + absorbed + " damage!");
        }

        tStats.hp = Math.max(0, tStats.hp - finalDamage);

        String message = aStats.name + " used " + actionName + "! (-" + apCost + " AP)";
        message += "\n" + tStats.name + " took " + finalDamage + " damage!";
        
        listener.onActionResolved(message);

        if (tStats.hp <= 0) {
            bm.get(target).isDead = true;
            turnQueue.removeValue(target, true);
            listener.onActionResolved(tStats.name + " was defeated!");
            if (bm.get(attacker).isPlayer) awardXp(attacker, target);
        }

        listener.onUpdateUI();
        checkCombatEnd();
        return true;
    }

    public boolean performDefensiveSkill(Entity entity, String actionName, int apCost, int cooldown, float defenseMultiplier) {
        if (combatEnded) return false;
        
        APComponent ap = am.get(entity);
        AbilitiesComponent abilities = abm.get(entity);

        if (ap.currentAP < apCost) {
            if (bm.get(entity).isPlayer) listener.onActionResolved("Not enough AP for " + actionName);
            return false;
        }
        if (!abilities.isReady(actionName)) {
            if (bm.get(entity).isPlayer) listener.onActionResolved(actionName + " is on cooldown!");
            return false;
        }

        ap.currentAP -= apCost;
        if (cooldown > 0) abilities.startCooldown(actionName, cooldown);

        StatsComponent stats = sm.get(entity);
        int shieldAmount = (int) (stats.defense * defenseMultiplier);
        stats.shield += shieldAmount;

        listener.onActionResolved(stats.name + " used " + actionName + "!\nGained " + shieldAmount + " shield!");
        
        listener.onUpdateUI();
        return true;
    }

    private void awardXp(Entity playerEntity, Entity defeatedEnemy) {
        StatsComponent enemyStats = sm.get(defeatedEnemy);
        LevelComponent pl = lm.get(playerEntity);
        if (pl == null) return;
        
        int xpGained = enemyStats.attack + enemyStats.defense + (enemyStats.maxHp / 5);
        pl.addXp(xpGained);
        listener.onActionResolved("Gained " + xpGained + " XP!");

        if (pl.canLevelUp()) {
            pl.levelUp();
            StatsComponent ps = sm.get(playerEntity);
            ps.maxHp += 20;
            ps.hp = ps.maxHp; 
            ps.attack += 5;
            ps.defense += 3;
            ps.speed += 2;
            listener.onActionResolved("LEVEL UP! Reached Level " + pl.level + "!");
        }
    }

    public boolean checkCombatEnd() {
        if (combatEnded) return true;
        if (combatants == null || combatants.size() == 0) return false;

        boolean playerAlive = false;
        boolean enemiesAlive = false;
        boolean playerFound = false;

        for (Entity e : combatants) {
            BattleComponent bc = bm.get(e);
            if (bc.isPlayer) playerFound = true;
            if (!bc.isDead) {
                if (bc.isPlayer) playerAlive = true;
                else enemiesAlive = true;
            }
        }

        if (playerFound && !playerAlive) {
            combatEnded = true;
            listener.onCombatEnded(false);
            return true;
        }
        if (playerFound && !enemiesAlive) {
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
