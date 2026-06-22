package game.toweoftrials.screens;

import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.VisUI;
import game.toweoftrials.Main;

public class OutroScreen extends BaseScreen {
    private final String fullText;
    private float timeElapsed = 0;
    private final float charsPerSecond = 40f; 
    private final Label textLabel;
    private final Label continueLabel;
    private boolean isFinished = false;

    public OutroScreen(final Main game) {
        super(game);
        
        fullText = "You have struck down the Lich King, the final Guardian of this purgatory...\n\n" +
                   "The curse of the Tower shatters like glass, revealing the mortal sky.\n\n" +
                   "Your forgotten wish has been granted. Your soul is your own once more.\n\n" +
                   "You are no longer a spark. You are human.\n\n" +
                   "Thank you for playing Tower of Trials.";

        root.clear();
        root.setBackground(VisUI.getSkin().getDrawable("window"));
        
        textLabel = new Label("", VisUI.getSkin());
        textLabel.setWrap(true);
        textLabel.setAlignment(Align.center);
        root.add(textLabel).width(600).expand().center().row();
        
        continueLabel = new Label("(Click to return to main menu...)", VisUI.getSkin());
        continueLabel.setVisible(false);
        root.add(continueLabel).pad(20).bottom();
        
        root.setTouchable(Touchable.enabled);
        root.addListener(new com.badlogic.gdx.scenes.scene2d.InputListener() {
            @Override
            public boolean touchDown(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y, int pointer, int button) {
                if (isFinished) {
                    game.setScreen(new StartMenuScreen(game));
                } else {
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
