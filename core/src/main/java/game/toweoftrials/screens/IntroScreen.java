package game.toweoftrials.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.VisUI;
import game.toweoftrials.Main;

public class IntroScreen extends BaseScreen {
    private final String fullText;
    private float timeElapsed = 0;
    private final float charsPerSecond = 40f; // Speed of the typewriter effect
    private final Label textLabel;
    private final Label continueLabel;
    private boolean isFinished = false;

    public IntroScreen(final Main game) {
        super(game);
        
        fullText = "You were not born into this world...\n\n" +
                   "You were 'summoned' as a Contractor.\n\n" +
                   "Long ago, your soul was bartered for a wish you no longer remember.\n\n" +
                   "To reclaim your mortality, you must ascend the Tower of Trials.\n\n" +
                   "Each floor is a manifested nightmare of a Guardian who reached their limit.\n\n" +
                   "Begin your ascent, little spark...";

        root.clear();
        root.setBackground(VisUI.getSkin().getDrawable("window"));
        
        textLabel = new Label("", VisUI.getSkin());
        textLabel.setWrap(true);
        textLabel.setAlignment(Align.center);
        root.add(textLabel).width(600).expand().center().row();
        
        continueLabel = new Label("(Click to begin your journey...)", VisUI.getSkin());
        continueLabel.setVisible(false);
        root.add(continueLabel).pad(20).bottom();
        
        root.setTouchable(Touchable.enabled);
        root.addListener(new com.badlogic.gdx.scenes.scene2d.InputListener() {
            @Override
            public boolean touchDown(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y, int pointer, int button) {
                if (isFinished) {
                    game.setScreen(new HubScreen(game));
                } else {
                    // Skip animation
                    timeElapsed = fullText.length() / charsPerSecond;
                }
                return true;
            }
        });
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        if (!isFinished) {
            timeElapsed += delta;
            int charsToShow = (int) (timeElapsed * charsPerSecond);
            if (charsToShow >= fullText.length()) {
                charsToShow = fullText.length();
                isFinished = true;
                continueLabel.setVisible(true);
            }
            textLabel.setText(fullText.substring(0, charsToShow));
        }
    }
}
