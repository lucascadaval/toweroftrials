package game.toweoftrials.model;

import com.badlogic.gdx.utils.ObjectMap;

public class MonsterRegistry {
    private static final ObjectMap<String, MonsterData> monsters = new ObjectMap<>();

    static {
        // Floor 1: Sewer
        monsters.put("slime", new MonsterData("Slime", new String[]{"monsters/slime_blue.png", "monsters/slime_orange.png", "monsters/slime_purple.png"}, 50, 15, 5, 12, Enemy.EnemyType.NORMAL));
        monsters.put("gremlin", new MonsterData("Gremlin", new String[]{"monsters/gremlin_green.png", "monsters/gremlin_brown.png", "monsters/gremlin_purple.png"}, 70, 20, 8, 14, Enemy.EnemyType.NORMAL));
        monsters.put("3head_slug", new MonsterData("3Head Slug", new String[]{"monsters/3head_slug_purple.png", "monsters/3head_slug_blue.png", "monsters/3head_slug_orange.png"}, 500, 40, 20, 10, Enemy.EnemyType.BOSS));

        // Floor 2: The unseen
        monsters.put("3eye_head", new MonsterData("3Eye Head", new String[]{"monsters/3eye_head_red.png", "monsters/3eye_head_purple.png", "monsters/3eye_head_yellow.png"}, 150, 35, 10, 15, Enemy.EnemyType.NORMAL));
        monsters.put("1eye_demonic_angel", new MonsterData("Demonic Angel", new String[]{"monsters/1eye_demonic_angel_blue.png", "monsters/1eye_demonic_angel_green.png", "monsters/1eye_demonic_angel_golden.png"}, 180, 45, 12, 18, Enemy.EnemyType.NORMAL));
        monsters.put("5eye_head", new MonsterData("5Eye Head", new String[]{"monsters/5eye_head_blue.png", "monsters/5eye_head_gray.png", "monsters/5eye_head_orange.png"}, 220, 50, 15, 12, Enemy.EnemyType.NORMAL));
        monsters.put("3eye_monster", new MonsterData("3Eye Monster", new String[]{"monsters/3eye_monster_purple.png", "monsters/3eye_monster_green.png", "monsters/3eye_monster_orange.png"}, 1200, 80, 30, 15, Enemy.EnemyType.BOSS));

        // Floor 3: Dead Sea
        monsters.put("lobster", new MonsterData("Lobster", new String[]{"monsters/lobster_yellow.png", "monsters/lobster_blue.png", "monsters/lobster_orange.png"}, 300, 65, 20, 12, Enemy.EnemyType.NORMAL));
        monsters.put("crab", new MonsterData("Crab", new String[]{"monsters/crab_yellow.png", "monsters/crab_blue.png", "monsters/crab_gray.png"}, 350, 55, 35, 8, Enemy.EnemyType.NORMAL));
        monsters.put("tentacle_warrior", new MonsterData("Tentacle Warrior", new String[]{"monsters/tentacle_warrior_green.png", "monsters/tentacle_warrior_purple.png", "monsters/tentacle_warrior_golden.png"}, 380, 75, 18, 16, Enemy.EnemyType.NORMAL));
        monsters.put("octopus_summoner", new MonsterData("Octopus Summoner", new String[]{"monsters/octopus_summoner_purple.png", "monsters/octopus_summoner_green.png", "monsters/octopus_summoner_orange.png"}, 2000, 120, 40, 20, Enemy.EnemyType.BOSS));

        // Floor 4: Forgotten library
        monsters.put("fiery_book", new MonsterData("Fiery Book", new String[]{"monsters/fiery_book_red.png", "monsters/fiery_book_blue.png", "monsters/fiery_book_orange.png"}, 450, 100, 15, 22, Enemy.EnemyType.NORMAL));
        monsters.put("frozen_book", new MonsterData("Frozen Book", new String[]{"monsters/frozen_book_ice.png", "monsters/frozen_book_white.png", "monsters/fronzen_book_blue.png"}, 450, 90, 25, 14, Enemy.EnemyType.NORMAL));
        monsters.put("flower", new MonsterData("Evil Flower", new String[]{"monsters/1eye_flower_yellow.png", "monsters/1eye_flower_blue.png", "monsters/1eye_flower_gray.png"}, 500, 85, 20, 10, Enemy.EnemyType.NORMAL));
        monsters.put("plague_doctor", new MonsterData("Plague Doctor", new String[]{"monsters/plague_doctor_purple.png", "monsters/plague_doctor_green.png", "monsters/plague_doctor_brown.png"}, 3500, 160, 50, 18, Enemy.EnemyType.BOSS));

        // Floor 5: Jungle
        monsters.put("snake", new MonsterData("Snake", new String[]{"monsters/snake_green.png", "monsters/snake_orange.png", "monsters/snake_purple.png"}, 700, 130, 25, 25, Enemy.EnemyType.NORMAL));
        monsters.put("killer_bee", new MonsterData("Killer Bee", new String[]{"monsters/killer_bee_yellow.png", "monsters/killer_bee_blue.png", "monsters/killer_bee_white.png"}, 600, 150, 15, 35, Enemy.EnemyType.NORMAL));
        monsters.put("spider", new MonsterData("Spider", new String[]{"monsters/spider_green.png", "monsters/spider_blue.png", "monsters/spider_orange.png"}, 850, 120, 40, 12, Enemy.EnemyType.NORMAL));
        monsters.put("ogre", new MonsterData("Ogre", new String[]{"monsters/ogre_yellow.png", "monsters/ogre_green.png", "monsters/ogre_orange.png"}, 6000, 250, 80, 10, Enemy.EnemyType.BOSS));

        // Floor 6: Swamp
        monsters.put("alien", new MonsterData("Alien", new String[]{"monsters/alien_green.png", "monsters/alien_orange.png", "monsters/alien_red.png"}, 1200, 200, 40, 20, Enemy.EnemyType.NORMAL));
        monsters.put("mammoth", new MonsterData("Mammoth", new String[]{"monsters/mammoth_brown.png", "monsters/mammoth_gray.png", "monsters/mammoth_orange.png"}, 2000, 160, 100, 8, Enemy.EnemyType.NORMAL));
        monsters.put("reptile_samurai", new MonsterData("Reptile Samurai", new String[]{"monsters/reptile_samurai_orange.png", "monsters/reptile_samurai_blue.png", "monsters/reptile_samurai_brown.png"}, 1400, 240, 60, 22, Enemy.EnemyType.NORMAL));
        monsters.put("reptile_warrior", new MonsterData("Reptile Warrior", new String[]{"monsters/reptile_warrior_green.png", "monsters/reptile_warrior_blue.png", "monsters/reptile_warrior_orange.png"}, 8500, 350, 120, 15, Enemy.EnemyType.BOSS));

        // Floor 7: Abandoned Mansion
        monsters.put("lion_statue", new MonsterData("Lion Statue", new String[]{"monsters/lion_statue_golden.png", "monsters/lion_statue_gray.png", "monsters/lion_statue_purple.png"}, 2500, 260, 150, 5, Enemy.EnemyType.NORMAL));
        monsters.put("tiger", new MonsterData("Ghost Tiger", new String[]{"monsters/tiger_1.png", "monsters/tiger_2.png", "monsters/tiger_3.png"}, 2000, 350, 80, 28, Enemy.EnemyType.NORMAL));
        monsters.put("wolf", new MonsterData("Wolf", new String[]{"monsters/wolf_blue.png", "monsters/wolf_red.png", "monsters/wof_yellow.png"}, 1800, 300, 70, 32, Enemy.EnemyType.NORMAL));
        monsters.put("undead_mage", new MonsterData("Undead Mage", new String[]{"monsters/undead_mage.png"}, 12000, 500, 150, 20, Enemy.EnemyType.BOSS));

        // Floor 8: Undead cemetery
        monsters.put("undead_human", new MonsterData("Undead Human", new String[]{"monsters/undead_human_yellow.png", "monsters/undead_human_blue.png", "monsters/undead_human_orange.png"}, 3500, 420, 100, 12, Enemy.EnemyType.NORMAL));
        monsters.put("skeletal_warrior", new MonsterData("Skeletal Warrior", new String[]{"monsters/skeletal_warrior_purple.png", "monsters/skeletal_warrior_blue.png", "monsters/skeletal_warrior_green.png"}, 3200, 500, 120, 18, Enemy.EnemyType.NORMAL));
        monsters.put("skeletal_worm", new MonsterData("Skeletal Worm", new String[]{"monsters/skeletal_worm_red.png", "monsters/skeletal_worm_green.png", "monsters/skeletal_worm_yellow.png"}, 4500, 350, 200, 10, Enemy.EnemyType.NORMAL));
        monsters.put("undead_knight", new MonsterData("Undead Knight", new String[]{"monsters/undead_knight_green.png", "monsters/undead_knight_blue.png", "monsters/undead_knight_brown.png"}, 7000, 650, 250, 15, Enemy.EnemyType.MINI_BOSS));
        monsters.put("lich", new MonsterData("Lich", new String[]{"monsters/lich_purple.png", "monsters/lich_orange.png", "monsters/lich_yellow.png"}, 25000, 1000, 400, 25, Enemy.EnemyType.BOSS));
    }

    public static MonsterData get(String id) {
        return monsters.get(id);
    }
}
