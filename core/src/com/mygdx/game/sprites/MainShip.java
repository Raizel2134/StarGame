package com.mygdx.game.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.base.Ship;
import com.mygdx.game.exception.GameException;
import com.mygdx.game.math.Rect;
import com.mygdx.game.pool.BulletPool;
import com.mygdx.game.pool.ExplosionPool;

public class MainShip extends Ship {
    private static final int HP = 100;
    private static final float SHIP_HEIGHT = 0.15f;
    private static final float BOTTOM_MARGIN = 0.05f;
    private static final int INVALID_POINTER = -1;

    private boolean pressedLeft;
    private boolean pressedRight;

    private int leftPointer = INVALID_POINTER;
    private int rightPointer = INVALID_POINTER;

    //Конструктор класса.
    public MainShip(TextureAtlas atlas, BulletPool bulletPool, ExplosionPool explosionPool) throws GameException {
        super(atlas.findRegion("main_ship"), 1, 2, 2);
        this.bulletPool = bulletPool;
        this.explosionPool = explosionPool;
        shootSound = Gdx.audio.newSound(Gdx.files.internal("sounds/laser.wav"));
        bulletRegion = atlas.findRegion("bulletMainShip");
        bulletV = new Vector2(0, 0.5f);
        bulletPos = new Vector2();
        v0 = new Vector2(0.5f, 0);
        v = new Vector2();
        reloadInterval = 0.2f;
        reloadTimer = reloadInterval;
        bulletHeight = 0.01f;
        damage = 1;
        hp = HP;
    }

    //Сброс настроек, для начала новой игры.
    public void newGame() {
        this.hp = HP;
        stop();
        pressedLeft = false;
        pressedRight = false;
        leftPointer = INVALID_POINTER;
        rightPointer = INVALID_POINTER;
        this.pos.x = worldBounds.pos.x;
        flushDestroy();
    }

    //Скалирование размера, пропорционально мировым координатам.
    @Override
    public void resize(Rect worldBounds) {
        this.worldBounds = worldBounds;
        setHeightProportion(SHIP_HEIGHT);
        setBottom(worldBounds.getBottom() + BOTTOM_MARGIN);
    }

    //Обновление с проверкой выхода за границы экрана.
    //И совершение выстрела с установкой позиции пули.
    @Override
    public void update(float delta) {
        super.update(delta);
        bulletPos.set(pos.x, pos.y + getHalfHeight());
        autoShoot(delta);
        if (getLeft() < worldBounds.getLeft()) {
            setLeft(worldBounds.getLeft());
            stop();
        }
        if (getRight() > worldBounds.getRight()) {
            setRight(worldBounds.getRight());
            stop();
        }
    }

    //Перемещение при касании пальцем\нажатием мыши по игровому пространству.
    @Override
    public boolean touchDown(Vector2 touch, int pointer, int button) {
        if (touch.x < worldBounds.pos.x) {
            if (leftPointer != INVALID_POINTER) {
                return false;
            }
            leftPointer = pointer;
            moveLeft();
        } else {
            if (rightPointer != INVALID_POINTER) {
                return false;
            }
            rightPointer = pointer;
            moveRight();
        }
        return false;
    }

    //Остановка перемещения.
    @Override
    public boolean touchUp(Vector2 touch, int pointer, int button) {
         if (pointer == leftPointer) {
             leftPointer = INVALID_POINTER;
             if (rightPointer != INVALID_POINTER) {
                 moveRight();
             } else {
                 stop();
             }
         } else if (pointer == rightPointer) {
             rightPointer = INVALID_POINTER;
             if (leftPointer != INVALID_POINTER) {
                 moveLeft();
             } else {
                 stop();
             }
         }
        return false;
    }

    //Следование за пальцем\курсором.
    @Override
    public boolean touchDragged(Vector2 touch, int pointer) {
        pos.set(touch.x, pos.y);
        return false;
    }

    //Начало движения, при нажатии клавиши.
    public boolean keyDown(int keycode) {
        switch (keycode) {
            case Input.Keys.A:
            case Input.Keys.LEFT:
                pressedLeft = true;
                moveLeft();
                break;
            case Input.Keys.D:
            case Input.Keys.RIGHT:
                pressedRight = true;
                moveRight();
                break;
        }
        return false;
    }

    //Остановка движения, при отпускании клавиши.
    public boolean keyUp(int keycode) {
        switch (keycode) {
            case Input.Keys.A:
            case Input.Keys.LEFT:
                pressedLeft = false;
                if (pressedRight) {
                    moveRight();
                } else {
                    stop();
                }
                break;
            case Input.Keys.D:
            case Input.Keys.RIGHT:
                pressedRight = false;
                if (pressedLeft) {
                    moveLeft();
                } else {
                    stop();
                }
                break;
        }
        return false;
    }

    //Проверка коллизий.
    public boolean isBulletCollision(Rect bullet) {
        return !(bullet.getRight() < getLeft()
                || bullet.getLeft() > getRight()
                || bullet.getBottom() > pos.y
                || bullet.getTop() < getBottom());
    }

    //Движение вправо.
    private void moveRight() { v.set(v0); }

    //Движение влево.
    private void moveLeft() { v.set(v0).rotate(180); }

    //Остановка.
    private void stop() { v.setZero(); }
}
