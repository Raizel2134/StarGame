package com.mygdx.game.sprites;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.mygdx.game.base.ScaledButton;
import com.mygdx.game.exception.GameException;
import com.mygdx.game.math.Rect;
import com.mygdx.game.screen.GameScreen;

public class ButtonNewGame extends ScaledButton{
    private GameScreen game;

    public ButtonNewGame(TextureAtlas atlas, GameScreen game) throws GameException{
        super(atlas.findRegion("button_new_game"));
        this.game = game;
    }

    @Override
    public void resize(Rect worldBounds) {
        setHeightProportion(0.07f);
        setBottom(-0.1f);
    }

    @Override
    public void action() {
        game.newGame();
    }
}
