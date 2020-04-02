package com.mygdx.game.sprites;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.base.Sprite;
import com.mygdx.game.math.Rect;

public class Ship extends Sprite {
    private static final float SIZE = 0.15f;
    private static final float BOTTOM_FIELD = 0.05f;
    private final Vector2 directionMove = new Vector2(0.5f, 0.0f);
    private final Vector2 direction = new Vector2();
    private Rect worldBounds;


    public Ship(TextureAtlas atlas) {
        super(atlas.findRegion("main_ship"), 1, 2, 2);
        setHeightProportion(SIZE);
    }

    public static TextureRegion[] split(TextureRegion region, int rows, int cols, int frames) {
        TextureRegion[] regions = new TextureRegion[frames];
        int tileWidth = region.getRegionWidth() / cols;
        int tileHeight = region.getRegionHeight() / rows;

        int frame = 0;
        for (int i = 0; i < rows; i++) {
            for (int k = 0; k < cols; k++) {
                regions[frame] = new TextureRegion(region, tileWidth * k, tileHeight * i, tileWidth, tileHeight);
                if(frame == frames - 1) return regions;
                frame++;
            }
        }
        return regions;
    }

    @Override
    public void resize(Rect worldBounds) {
        this.worldBounds = worldBounds;
        setBottom(worldBounds.getBottom() + BOTTOM_FIELD);
    }

    @Override
    public void update(float deltaTime) {
        pos.mulAdd(direction, deltaTime);
        if (getRight() > worldBounds.getRight()) {
            setRight(worldBounds.getRight());
            stop();
        }
        if (getLeft() < worldBounds.getLeft()) {
            setLeft(worldBounds.getLeft());
            stop();
        }
    }

    public void keyDown(int keycode) {
        switch (keycode) {
            case Input.Keys.A:
            case Input.Keys.LEFT:
                moveLeft();
                break;
            case Input.Keys.D:
            case Input.Keys.RIGHT:
                moveRight();
                break;
            case Input.Keys.UP:
                frame = 1;
                break;
        }
    }

    public void keyUp(int keycode) {
        switch (keycode) {
            case Input.Keys.A:
            case Input.Keys.LEFT:
            case Input.Keys.D:
            case Input.Keys.RIGHT:
                stop();
                break;
            case Input.Keys.UP:
                frame = 0;
                break;
        }
    }

    private void moveRight() {
        direction.set(directionMove);
    }

    private void moveLeft() {
        direction.set(directionMove).rotate(180);
    }

    private void stop() {
        direction.setZero();
    }
}
