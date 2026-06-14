package game.toweoftrials.model;

import com.badlogic.ashley.core.Entity;
import game.toweoftrials.ecs.components.*;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public class Player extends GameEntity {
    private final Array<Item> inventory = new Array<>();
    private final ObjectMap<Item.ItemType, Item> equippedItems = new ObjectMap<>();
    
    public Player(Entity entity) {
        super(entity);
    }

    public Array<Item> getInventory() {
        return inventory;
    }

    public void addItem(Item item) {
        inventory.add(item);
    }

    public void equipItem(Item item) {
        // Unequip current if exists
        Item current = equippedItems.get(item.getType());
        if (current != null) unequipItem(current.getType());
        
        equippedItems.put(item.getType(), item);
        applyStats(item, 1);
    }

    public void unequipItem(Item.ItemType type) {
        Item item = equippedItems.remove(type);
        if (item != null) applyStats(item, -1);
    }

    public Item getEquipped(Item.ItemType type) {
        return equippedItems.get(type);
    }

    private void applyStats(Item item, int sign) {
        StatsComponent stats = getStats();
        stats.maxHp += item.hpBonus * sign;
        stats.attack += item.attackBonus * sign;
        stats.defense += item.defenseBonus * sign;
        stats.speed += item.speedBonus * sign;
        if (stats.hp > stats.maxHp) stats.hp = stats.maxHp;
    }
    public LevelComponent getLevel() {
        return entity.getComponent(LevelComponent.class);
    }

    public void healFull() {
        StatsComponent stats = getStats();
        stats.hp = stats.maxHp;
        stats.shield = 0;
        entity.getComponent(BattleComponent.class).isDead = false;
        getAbilities().resetCooldowns();
    }
}
