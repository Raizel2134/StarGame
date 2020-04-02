package com.mygdx.game.base;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.exception.GameException;

public abstract class ScaledButton extends Sprite {

    private static final float SCALE = 0.9f;

    private int pointer;
    private boolean pressed;

    public ScaledButton(TextureRegion region) throws GameException {
        super(region);
    }

    @Override
    public boolean touchDown(Vector2 touch, int pointer, int button) {
        if (pressed || !isMe(touch)) {
            return false;
        }
        this.pointer = pointer;
        scale = SCALE;
        pressed = true;
        return false;
    }

    @Override
    public boolean touchUp(Vector2 touch, int pointer, int button) {
        if (this.pointer != pointer || !pressed) {
            return false;
        }
        if (isMe(touch)) {
            action();
        }
        pressed = false;
        scale = 1f;
        return false;
    }

    public abstract void action();
}
