package game.toweoftrials.model;

public class MonsterData {
    public final String name;
    public final String texturePath;
    public final int hp;
    public final int attack;
    public final int defense;
    public final int speed;
    public final Enemy.EnemyType type;

    public MonsterData(String name, String texturePath, int hp, int attack, int defense, int speed, Enemy.EnemyType type) {
        this.name = name;
        this.texturePath = texturePath;
        this.hp = hp;
        this.attack = attack;
        this.defense = defense;
        this.speed = speed;
        this.type = type;
    }
}
