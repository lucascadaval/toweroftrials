package game.toweoftrials.ecs.components;

import com.badlogic.ashley.core.Component;

public class StatsComponent implements Component {
    public int hp;
    public int maxHp;
    public int attack;
    public int defense;
    public int speed;
    public int level = 1;
    public int xp = 0;
    public String name;

    public StatsComponent(String name, int maxHp, int attack, int defense, int speed) {
        this.name = name;
        this.hp = maxHp;
        this.maxHp = maxHp;
        this.attack = attack;
        this.defense = defense;
        this.speed = speed;
    }
}
