package game.toweoftrials.model;

public class MonsterData {
    public final String name;
    public final String[] texturePaths;
    public final int hp;
    public final int attack;
    public final int defense;
    public final int speed;
    public final Enemy.EnemyType type;

    public MonsterData(String name, String[] texturePaths, int hp, int attack, int defense, int speed, Enemy.EnemyType type) {
        this.name = name;
        this.texturePaths = texturePaths;
        this.hp = hp;
        this.attack = attack;
        this.defense = defense;
        this.speed = speed;
        this.type = type;
    }
}
