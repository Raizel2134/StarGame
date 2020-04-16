package com.mygdx.game.sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.mygdx.game.base.ScaledButton;
import com.mygdx.game.exception.GameException;
import com.mygdx.game.math.Rect;
import com.mygdx.game.screen.GameScreen;

public class ButtonResume extends ScaledButton {
    private GameScreen game;

    public ButtonResume(TextureAtlas atlas, GameScreen game) throws GameException {
        super(atlas.findRegion("play-button"));
        this.game = game;
    }

    @Override
    public void resize(Rect worldBounds) {
        setHeightProportion(0.05f);
        setTop(worldBounds.getTop() - 0.01f);
        setRight(worldBounds.getRight() - 0.01f);
    }

    @Override
    public void action() {
        game.resumeGame();
    }
}
