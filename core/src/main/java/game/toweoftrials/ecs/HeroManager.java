package game.toweoftrials.ecs;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;

import game.toweoftrials.model.Player;

public class HeroManager {
    private static Entity hero;
    private static Player player;

    public static Entity getHero() {
        return hero;
    }

    public static Player getPlayer() {
        if (player == null && hero != null) {
            player = new Player(hero);
        }
        return player;
    }

    public static void setHero(Entity h) {
        hero = h;
        player = new Player(hero);
    }
}
