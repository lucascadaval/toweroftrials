package game.toweoftrials.model;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public class AnimationEffect {
    private final Animation<TextureRegion> animation;
    private float stateTime;
    private boolean finished;

    public AnimationEffect(Array<Texture> frames, float frameDuration) {
        Array<TextureRegion> regions = new Array<>();
        for (Texture t : frames) {
            regions.add(new TextureRegion(t));
        }
        this.animation = new Animation<>(frameDuration, regions, Animation.PlayMode.NORMAL);
        this.stateTime = 0;
        this.finished = false;
    }

    public void update(float delta) {
        stateTime += delta;
        if (animation.isAnimationFinished(stateTime)) {
            finished = true;
        }
    }

    public TextureRegion getKeyFrame() {
        return animation.getKeyFrame(stateTime);
    }

    public boolean isFinished() {
        return finished;
    }
}
