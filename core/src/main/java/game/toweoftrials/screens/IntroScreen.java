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
    private final Array<String> introLines = new Array<>();
    private int currentLine = 0;
    private final Label textLabel;

    public IntroScreen(final Main game) {
        super(game);
        
        introLines.add("You were not born into this world...");
        introLines.add("You were 'summoned' as a Contractor.");
        introLines.add("Long ago, your soul was bartered for a wish you no longer remember.");
        introLines.add("To reclaim your mortality, you must ascend the Tower of Trials.");
        introLines.add("Each floor is a manifested nightmare of a Guardian who reached their limit.");
        introLines.add("Begin your ascent, little spark...");

        root.clear();
        root.setBackground(VisUI.getSkin().getDrawable("window"));
        
        textLabel = new Label("", VisUI.getSkin());
        textLabel.setWrap(true);
        textLabel.setAlignment(Align.center);
        root.add(textLabel).width(600).expand().center().row();
        
        root.add(new Label("(Click to continue...)", VisUI.getSkin())).pad(20).bottom();
        
        root.setTouchable(Touchable.enabled);
        root.addListener(new com.badlogic.gdx.scenes.scene2d.InputListener() {
            @Override
            public boolean touchDown(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y, int pointer, int button) {
                nextLine();
                return true;
            }
        });
        
        nextLine();
    }

    private void nextLine() {
        if (currentLine < introLines.size) {
            textLabel.setText(introLines.get(currentLine));
            currentLine++;
        } else {
            game.setScreen(new HubScreen(game));
        }
    }
}
