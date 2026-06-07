package game.toweoftrials.model;

import com.badlogic.gdx.utils.ObjectMap;

public class MonsterRegistry {
    private static final ObjectMap<String, MonsterData> monsters = new ObjectMap<>();

    static {
        // Floor 1: Sewer
        monsters.put("slime", new MonsterData("Slime", "monsters/slime_blue.png", 50, 15, 5, 12, Enemy.EnemyType.NORMAL));
        monsters.put("gremlin", new MonsterData("Gremlin", "monsters/gremlin_green.png", 70, 20, 8, 14, Enemy.EnemyType.NORMAL));
        monsters.put("3head_slug", new MonsterData("3Head Slug", "monsters/3head_slug_purple.png", 500, 40, 20, 10, Enemy.EnemyType.BOSS));

        // Floor 2: The unseen
        monsters.put("3eye_head", new MonsterData("3Eye Head", "monsters/3eye_head_red.png", 100, 25, 10, 15, Enemy.EnemyType.NORMAL));
        monsters.put("1eye_demonic_angel", new MonsterData("Demonic Angel", "monsters/1eye_demonic_angel_blue.png", 120, 30, 12, 18, Enemy.EnemyType.NORMAL));
        monsters.put("5eye_head", new MonsterData("5Eye Head", "monsters/5eye_head_blue.png", 150, 35, 15, 12, Enemy.EnemyType.NORMAL));
        monsters.put("3eye_monster", new MonsterData("3Eye Monster", "monsters/3eye_monster_purple.png", 800, 60, 30, 15, Enemy.EnemyType.BOSS));

        // Floor 3: Dead Sea
        monsters.put("lobster", new MonsterData("Lobster", "monsters/lobster_yellow.png", 180, 40, 20, 12, Enemy.EnemyType.NORMAL));
        monsters.put("crab", new MonsterData("Crab", "monsters/crab_yellow.png", 200, 35, 35, 8, Enemy.EnemyType.NORMAL));
        monsters.put("tentacle_warrior", new MonsterData("Tentacle Warrior", "monsters/tentacle_warrior_green.png", 220, 45, 18, 16, Enemy.EnemyType.NORMAL));
        monsters.put("octopus_summoner", new MonsterData("Octopus Summoner", "monsters/octopus_summoner_purple.png", 1200, 80, 40, 20, Enemy.EnemyType.BOSS));

        // Floor 4: Forgotten library
        monsters.put("fiery_book", new MonsterData("Fiery Book", "monsters/fiery_book_red.png", 250, 60, 15, 22, Enemy.EnemyType.NORMAL));
        monsters.put("frozen_book", new MonsterData("Frozen Book", "monsters/frozen_book_ice.png", 250, 55, 25, 14, Enemy.EnemyType.NORMAL));
        monsters.put("flower", new MonsterData("Evil Flower", "monsters/1eye_flower_yellow.png", 300, 50, 20, 10, Enemy.EnemyType.NORMAL));
        monsters.put("plague_doctor", new MonsterData("Plague Doctor", "monsters/plague_doctor_purple.png", 2000, 100, 50, 18, Enemy.EnemyType.BOSS));

        // Floor 5: Jungle
        monsters.put("snake", new MonsterData("Snake", "monsters/snake_green.png", 400, 75, 25, 25, Enemy.EnemyType.NORMAL));
        monsters.put("killer_bee", new MonsterData("Killer Bee", "monsters/killer_bee_yellow.png", 350, 85, 15, 35, Enemy.EnemyType.NORMAL));
        monsters.put("spider", new MonsterData("Spider", "monsters/spider_green.png", 500, 70, 40, 12, Enemy.EnemyType.NORMAL));
        monsters.put("ogre", new MonsterData("Ogre", "monsters/ogre_yellow.png", 3500, 150, 80, 10, Enemy.EnemyType.BOSS));

        // Floor 6: Swamp
        monsters.put("alien", new MonsterData("Alien", "monsters/alien_green.png", 700, 120, 40, 20, Enemy.EnemyType.NORMAL));
        monsters.put("mammoth", new MonsterData("Mammoth", "monsters/mammoth_brown.png", 1200, 100, 100, 8, Enemy.EnemyType.NORMAL));
        monsters.put("reptile_samurai", new MonsterData("Reptile Samurai", "monsters/reptile_samurai_orange.png", 800, 140, 60, 22, Enemy.EnemyType.NORMAL));
        monsters.put("reptile_warrior", new MonsterData("Reptile Warrior", "monsters/reptile_warrior_green.png", 5000, 200, 120, 15, Enemy.EnemyType.BOSS));

        // Floor 7: Abandoned Mansion
        monsters.put("lion_statue", new MonsterData("Lion Statue", "monsters/lion_statue_golden.png", 1500, 150, 150, 5, Enemy.EnemyType.NORMAL));
        monsters.put("tiger", new MonsterData("Ghost Tiger", "monsters/tiger_1.png", 1200, 220, 80, 28, Enemy.EnemyType.NORMAL));
        monsters.put("wolf", new MonsterData("Wolf", "monsters/wolf_blue.png", 1000, 180, 70, 32, Enemy.EnemyType.NORMAL));
        monsters.put("undead_mage", new MonsterData("Undead Mage", "monsters/undead_mage.png", 7000, 300, 150, 20, Enemy.EnemyType.BOSS));

        // Floor 8: Undead cemetery
        monsters.put("undead_human", new MonsterData("Undead Human", "monsters/undead_human_yellow.png", 2000, 250, 100, 12, Enemy.EnemyType.NORMAL));
        monsters.put("skeletal_warrior", new MonsterData("Skeletal Warrior", "monsters/skeletal_warrior_purple.png", 1800, 300, 120, 18, Enemy.EnemyType.NORMAL));
        monsters.put("skeletal_worm", new MonsterData("Skeletal Worm", "monsters/skeletal_worm_red.png", 2500, 200, 200, 10, Enemy.EnemyType.NORMAL));
        monsters.put("undead_knight", new MonsterData("Undead Knight", "monsters/undead_knight_green.png", 4000, 400, 250, 15, Enemy.EnemyType.MINI_BOSS));
        monsters.put("lich", new MonsterData("Lich", "monsters/lich_purple.png", 15000, 600, 400, 25, Enemy.EnemyType.BOSS));
    }

    public static MonsterData get(String id) {
        return monsters.get(id);
    }
}
