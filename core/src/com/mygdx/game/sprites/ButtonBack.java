package com.mygdx.game.sprites;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.mygdx.game.base.ScaledButton;
import com.mygdx.game.exception.GameException;
import com.mygdx.game.math.Rect;
import com.mygdx.game.screen.MenuScreen;

public class ButtonBack extends ScaledButton {
    private final Game game;

    public ButtonBack(TextureAtlas atlas, Game game) throws GameException {
        super(atlas.findRegion("cancel"));
        this.game = game;
    }

    @Override
    public void resize(Rect worldBounds) {
        setHeightProportion(0.05f);
        setTop(worldBounds.getTop() - 0.07f);
        setRight(worldBounds.getRight() - 0.01f);
    }

    @Override
    public void action() {
        game.setScreen(new MenuScreen(game));
    }
}