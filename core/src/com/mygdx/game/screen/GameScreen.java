package com.mygdx.game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.pool.EnemyPool;
import com.mygdx.game.base.BaseScreen;
import com.mygdx.game.exception.GameException;
import com.mygdx.game.math.Rect;
import com.mygdx.game.pool.BulletPool;
import com.mygdx.game.pool.ExplosionPool;
import com.mygdx.game.sprites.Background;
import com.mygdx.game.sprites.Bullet;
import com.mygdx.game.sprites.ButtonNewGame;
import com.mygdx.game.sprites.Enemy;
import com.mygdx.game.sprites.GameOver;
import com.mygdx.game.sprites.MainShip;
import com.mygdx.game.sprites.Star;
import com.mygdx.game.utils.EnemyEmitter;
import java.util.List;


public class GameScreen extends BaseScreen {
    private enum STATE {PLAYING, PAUSE, GAME_OVER}
    private static final int STAR_COUNT = 64;
    private Texture bg;
    private Background background;

    private TextureAtlas atlas;

    private Star[] stars;
    private MainShip mainShip;
    private GameOver gameOver;
    private ButtonNewGame newGame;

    private BulletPool bulletPool;
    private EnemyPool enemyPool;
    private ExplosionPool explosionPool;
    private EnemyEmitter enemyEmitter;
    private Music music;
    private Sound explosion;
    private STATE state;

    @Override
    public void show() {
        super.show();
        bg = new Texture("textures/bg.jpg");
        atlas = new TextureAtlas(Gdx.files.internal("textures/mainAtlas.tpack"));
        explosion = Gdx.audio.newSound(Gdx.files.internal("sounds/explosion.wav"));
        bulletPool = new BulletPool();
        explosionPool = new ExplosionPool(atlas, explosion);
        enemyPool = new EnemyPool(bulletPool, explosionPool, worldBounds);
        enemyEmitter = new EnemyEmitter(atlas, enemyPool, worldBounds);
        music = Gdx.audio.newMusic(Gdx.files.internal("sounds/fight.mp3"));
        music.setLooping(true);
        music.play();
        initSprites();
        state = STATE.PLAYING;
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        update(delta);
        checkCollisions();
        freeAllDestroyed();
        draw();
    }

    @Override
    public void resize(Rect worldBounds) {
        super.resize(worldBounds);
        background.resize(worldBounds);
        for (Star star : stars) {
            star.resize(worldBounds);
        }
        mainShip.resize(worldBounds);
        gameOver.resize(worldBounds);
        newGame.resize(worldBounds);
    }

    @Override
    public void dispose() {
        bg.dispose();
        atlas.dispose();
        bulletPool.dispose();
        enemyPool.dispose();
        music.dispose();
        super.dispose();
    }

    @Override
    public boolean keyDown(int keycode) {
        if (state == STATE.PLAYING) {
            mainShip.keyDown(keycode);
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (state == STATE.PLAYING) {
            mainShip.keyUp(keycode);
        }
        return false;
    }
    @Override
    public boolean touchDown(Vector2 touch, int pointer, int button) {
        if (state == STATE.PLAYING) {
            mainShip.touchDown(touch, pointer, button);
        } else if(state == STATE.GAME_OVER){
            newGame.touchDown(touch, pointer, button);
        }
        return false;
    }

    @Override
    public boolean touchUp(Vector2 touch, int pointer, int button) {
        if (state == STATE.PLAYING) {
            mainShip.touchUp(touch, pointer, button);
        } else if(state == STATE.GAME_OVER){
            newGame.touchUp(touch, pointer, button);
        }
        return false;
    }

    private void initSprites() {
        try {
            background = new Background(bg);
            stars = new Star[STAR_COUNT];
            for (int i = 0; i < STAR_COUNT; i++) {
                stars[i] =  new Star(atlas);
            }
            mainShip = new MainShip(atlas, bulletPool, explosionPool);
            gameOver = new GameOver(atlas);
            newGame = new ButtonNewGame(atlas,this);
        } catch (GameException e) {
            throw new RuntimeException(e);
        }
    }

    private void update(float delta) {
        for (Star star : stars) {
            star.update(delta);
        }
        explosionPool.updateActiveSprites(delta);
        if (state == STATE.PLAYING) {
            mainShip.update(delta);
            bulletPool.updateActiveSprites(delta);
            enemyPool.updateActiveSprites(delta);
            enemyEmitter.generate(delta);
        }
    }
    private void checkCollisions() {
        if (state != STATE.PLAYING) {
            return;
        }
        List<Enemy> enemyList = enemyPool.getActiveObjects();
        List<Bullet> bulletList = bulletPool.getActiveObjects();
        for (Enemy enemy : enemyList) {
            if (enemy.isDestroyed()) {
                continue;
            }
            float minDist = enemy.getHalfWidth() + mainShip.getHalfWidth();
            if (mainShip.pos.dst(enemy.pos) < minDist) {
                enemy.destroy();
                mainShip.damage(enemy.getDamage());
            }
            for (Bullet bullet : bulletList) {
                if (bullet.getOwner() != mainShip || bullet.isDestroyed()) {
                    continue;
                }
                if (enemy.isBulletCollision(bullet)) {
                    enemy.damage(bullet.getDamage());
                    bullet.destroy();
                }
            }
        }
        for (Bullet bullet : bulletList) {
            if (bullet.getOwner() == mainShip || bullet.isDestroyed()) {
                continue;
            }
            if (mainShip.isBulletCollision(bullet)) {
                mainShip.damage(bullet.getDamage());
                bullet.destroy();
            }
        }
        if (mainShip.isDestroyed()) {
            state = STATE.GAME_OVER;
        }
    }

    private void freeAllDestroyed() {
        bulletPool.freeAllDestroyedActiveObjects();
        enemyPool.freeAllDestroyedActiveObjects();
        explosionPool.freeAllDestroyedActiveObjects();
    }
    //Вызов методов сбрасывающих настройки.
    public void newGame() {
        state = STATE.PLAYING;
        mainShip.newGame();
        bulletPool.freeAllActiveObjects();
        enemyPool.freeAllActiveObjects();
        explosionPool.freeAllActiveObjects();
    }

    private void draw() {
        Gdx.gl.glClearColor(0.5f, 0.7f, 0.8f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        background.draw(batch);
        for (Star star : stars) {
            star.draw(batch);
        }
        switch (state) {
            case PLAYING:
                mainShip.draw(batch);
                enemyPool.drawActiveSprites(batch);
                bulletPool.drawActiveSprites(batch);
                break;
            case GAME_OVER:
                gameOver.draw(batch);
                newGame.draw(batch);
                break;
        }
        explosionPool.drawActiveSprites(batch);
        batch.end();
    }
}
