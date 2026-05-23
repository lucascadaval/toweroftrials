package game.toweoftrials.ecs;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.utils.Array;
import game.toweoftrials.ecs.components.APComponent;
import game.toweoftrials.ecs.components.BattleComponent;
import game.toweoftrials.ecs.components.StatsComponent;
import game.toweoftrials.ecs.components.AbilitiesComponent;

import java.util.Comparator;

public class CombatSystem extends EntitySystem {
    private final ComponentMapper<StatsComponent> sm = ComponentMapper.getFor(StatsComponent.class);
    private final ComponentMapper<APComponent> am = ComponentMapper.getFor(APComponent.class);
    private final ComponentMapper<BattleComponent> bm = ComponentMapper.getFor(BattleComponent.class);
    private final ComponentMapper<AbilitiesComponent> abm = ComponentMapper.getFor(AbilitiesComponent.class);

    private ImmutableArray<Entity> combatants;
    private final Array<Entity> turnQueue = new Array<>();
    private Entity activeEntity;
    private final CombatListener listener;
    private boolean combatEnded = false;

    private float actionDelay = 1.0f;
    private float timeSinceLastAction = 0;
    private boolean waitingForAI = false;

    public interface CombatListener {
        void onTurnStarted(Entity entity);
        void onActionResolved(String message);
        void onCombatEnded(boolean playerWon);
        void onUpdateUI();
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
        if (combatEnded) return;

        if (waitingForAI && activeEntity != null && !bm.get(activeEntity).isPlayer) {
            timeSinceLastAction += deltaTime;
            if (timeSinceLastAction >= actionDelay) {
                timeSinceLastAction = 0;
                executeOneAIAction();
            }
        }
    }

    public void startCombat() {
        if (combatEnded) return;
        turnQueue.clear();
        for (Entity e : combatants) {
            turnQueue.add(e);
        }
        sortQueue();
        nextTurn();
    }

    private void sortQueue() {
        turnQueue.sort(new Comparator<Entity>() {
            @Override
            public int compare(Entity o1, Entity o2) {
                return Integer.compare(sm.get(o2).speed, sm.get(o1).speed);
            }
        });
    }

    public void nextTurn() {
        if (combatEnded) return;
        if (checkCombatEnd()) return;

        if (turnQueue.size == 0) {
            startCombat(); // New round
            return;
        }

        activeEntity = turnQueue.removeIndex(0);
        
        // AP Regeneration to MAX at the start of the turn
        APComponent ap = am.get(activeEntity);
        ap.currentAP = ap.maxAP;

        // Update Cooldowns ONLY at the start of entity's turn
        AbilitiesComponent abilities = abm.get(activeEntity);
        abilities.updateCooldowns();

        listener.onTurnStarted(activeEntity);
        
        if (!bm.get(activeEntity).isPlayer) {
            waitingForAI = true;
            timeSinceLastAction = 0.5f; // Initial delay before AI acts
        } else {
            waitingForAI = false;
        }
    }

    private void executeOneAIAction() {
        if (combatEnded) return;
        
        APComponent ap = am.get(activeEntity);
        AbilitiesComponent abilities = abm.get(activeEntity);
        Entity player = getPlayer();
        
        if (player == null || bm.get(player).isDead) {
            waitingForAI = false;
            nextTurn();
            return;
        }

        boolean acted = false;
        // AI logic: Use as much AP as possible, respecting cooldowns
        if (ap.currentAP >= 4 && abilities.isReady("ULTIMATE")) {
            acted = performAttack(activeEntity, player, "ULTIMATE", 4, 0, 3, 3.5f);
        } else if (ap.currentAP >= 2 && abilities.isReady("Heavy Smash")) {
            acted = performAttack(activeEntity, player, "Heavy Smash", 2, 0, 1, 1.8f);
        } else if (ap.currentAP >= 1 && abilities.isReady("Quick Strike")) {
            acted = performAttack(activeEntity, player, "Quick Strike", 1, 0, 0, 1.2f);
        } else if (ap.currentAP >= 1) { 
            acted = performAttack(activeEntity, player, "Basic Attack", 1, 0, 0, 1.0f);
        }

        if (!acted || combatEnded) {
            waitingForAI = false;
            if (!combatEnded) nextTurn();
        }
    }

    public boolean performAttack(Entity attacker, Entity target, String actionName, int apCost, int apGain, int cooldown, float multiplier) {
        if (combatEnded) return false;
        
        APComponent ap = am.get(attacker);
        AbilitiesComponent abilities = abm.get(attacker);

        // 1. Check AP cost
        if (ap.currentAP < apCost) {
            if (bm.get(attacker).isPlayer) listener.onActionResolved("Not enough AP for " + actionName);
            return false;
        }

        // 2. Check Cooldown
        if (!abilities.isReady(actionName)) {
            if (bm.get(attacker).isPlayer) listener.onActionResolved(actionName + " is on cooldown! (" + abilities.getRemainingCooldown(actionName) + " turns left)");
            return false;
        }

        // 3. Execute Action
        ap.currentAP -= apCost;
        ap.currentAP = Math.min(ap.maxAP, ap.currentAP + apGain);
        
        if (cooldown > 0) {
            abilities.startCooldown(actionName, cooldown);
        }

        StatsComponent aStats = sm.get(attacker);
        StatsComponent tStats = sm.get(target);

        int damage = (int) (Math.max(1, aStats.attack - tStats.defense) * multiplier);
        tStats.hp = Math.max(0, tStats.hp - damage);

        String message = aStats.name + " used " + actionName + "!";
        if (apCost > 0) message += " (-" + apCost + " AP)";
        if (apGain > 0) message += " (+" + apGain + " AP)";
        message += "\n" + tStats.name + " took " + damage + " damage!";
        
        listener.onActionResolved(message);

        if (tStats.hp <= 0) {
            bm.get(target).isDead = true;
            turnQueue.removeValue(target, true);
            listener.onActionResolved(tStats.name + " was defeated!");
        }

        listener.onUpdateUI();
        
        checkCombatEnd();
        
        return true;
    }

    public boolean checkCombatEnd() {
        if (combatEnded) return true;

        boolean playerAlive = false;
        boolean enemiesAlive = false;

        for (Entity e : combatants) {
            BattleComponent bc = bm.get(e);
            if (!bc.isDead) {
                if (bc.isPlayer) playerAlive = true;
                else enemiesAlive = true;
            }
        }

        if (!playerAlive) {
            combatEnded = true;
            listener.onCombatEnded(false);
            return true;
        }
        if (!enemiesAlive) {
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
}
