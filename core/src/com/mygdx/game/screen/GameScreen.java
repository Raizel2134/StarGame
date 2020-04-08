package com.mygdx.game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.pool.EnemyPool;
import com.mygdx.game.base.BaseScreen;
import com.mygdx.game.exception.GameException;
import com.mygdx.game.math.Rect;
import com.mygdx.game.pool.BulletPool;
import com.mygdx.game.sprites.Background;
import com.mygdx.game.sprites.Bullet;
import com.mygdx.game.sprites.Enemy;
import com.mygdx.game.sprites.MainShip;
import com.mygdx.game.sprites.Star;
import com.mygdx.game.utils.EnemyEmitter;
import java.util.List;


public class GameScreen extends BaseScreen {
    private static final int STAR_COUNT = 64;
    private Texture bg;
    private Background background;

    private TextureAtlas atlas;

    private Star[] stars;

    private MainShip mainShip;

    private BulletPool bulletPool;
    private EnemyPool enemyPool;

    private EnemyEmitter enemyEmitter;
    private Music fightMusic;

    @Override
    public void show() {
        super.show();
        bg = new Texture("textures/bg.jpg");
        atlas = new TextureAtlas(Gdx.files.internal("textures/mainAtlas.tpack"));
        bulletPool = new BulletPool();
        enemyPool = new EnemyPool(bulletPool, worldBounds);
        enemyEmitter = new EnemyEmitter(atlas, enemyPool, worldBounds);
        fightMusic = Gdx.audio.newMusic(Gdx.files.internal("sounds/fight.mp3"));
        fightMusic.setLooping(true);
        fightMusic.play();
        initSprites();
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        update(delta);
        checkCollisions();
        freeAllDestroyed();
        checkDamageMainShip();
        checkDamageEnemyShip();
        if(!checkStatusGame()){
            System.out.println("You lose!");
        }
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
    }

    @Override
    public void dispose() {
        bg.dispose();
        atlas.dispose();
        bulletPool.dispose();
        enemyPool.dispose();
        fightMusic.dispose();
        super.dispose();
    }

    @Override
    public boolean keyDown(int keycode) {
        mainShip.keyDown(keycode);
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        mainShip.keyUp(keycode);
        return false;
    }
    @Override
    public boolean touchDown(Vector2 touch, int pointer, int button) {
        mainShip.touchDown(touch, pointer, button);
        return false;
    }

    @Override
    public boolean touchUp(Vector2 touch, int pointer, int button) {
        mainShip.touchUp(touch, pointer, button);
        return false;
    }

    private void initSprites() {
        try {
            background = new Background(bg);
            stars = new Star[STAR_COUNT];
            for (int i = 0; i < STAR_COUNT; i++) {
                stars[i] =  new Star(atlas);
            }
            mainShip = new MainShip(atlas, bulletPool);
        } catch (GameException e) {
            throw new RuntimeException(e);
        }
    }

    private void update(float delta) {
        for (Star star : stars) {
            star.update(delta);
        }
        mainShip.update(delta);
        bulletPool.updateActiveSprites(delta);
        enemyPool.updateActiveSprites(delta);
        enemyEmitter.generate(delta);
    }

    //Добавил нанесение урона при столкновении с нашим кораблем.
    private void checkCollisions() {
        List<Enemy> enemyList = enemyPool.getActiveObjects();
        for (Enemy enemy : enemyList) {
            if (enemy.isDestroyed()) {
                continue;
            }
            float minDist = enemy.getHalfWidth() + mainShip.getHalfWidth();
            if (mainShip.pos.dst(enemy.pos) < minDist) {
                mainShip.damageHP(enemy.getDamage());
                enemy.destroy();
            }
        }
    }

    //По аналогу метода выше, сделал проверку на попадании вражеской пули в наш корабль.
    //До того как переработал отрисовку пули из носа корабля, урон проходил от своих же пуль.
    //Флаг принадлежности пули добавил позже.
    private void checkDamageMainShip() {
        List<Bullet> bulletEnemy = bulletPool.getActiveObjects();
        for (Bullet bullet : bulletEnemy) {
            if (bullet.isDestroyed()) {
                continue;
            }
            if(bullet.getFlagShip() == 1){
                float minDist = bullet.getHalfWidth() + mainShip.getHalfWidth();
                if (mainShip.pos.dst(bullet.pos) < minDist) {
                    mainShip.damageHP(bullet.getDamage());
                    bullet.destroy();
                }
            }
        }
    }

    //Аналогично сделал проверку на нанесение урона вражескому кораблю.
    //Здесь была похожая ситуация. Пули уже отрисовывались корректно, но враг наносил сам себе урон.
    //С этого момента решил добавить флаг.
    private void checkDamageEnemyShip() {
        List<Bullet> bulletEnemy = bulletPool.getActiveObjects();
        for (Bullet bullet : bulletEnemy) {
            if (bullet.isDestroyed()) {
                continue;
            } else {
                List<Enemy> enemyList = enemyPool.getActiveObjects();
                for (Enemy enemy : enemyList) {
                    if(bullet.getFlagShip() == 0){
                        float minDist = enemy.getHalfWidth() - bullet.getHalfWidth();
                        if (enemy.pos.dst(bullet.pos) < minDist) {
                            enemy.damageHP(bullet.getDamage());
                            if(enemy.getHP() <= 0){
                                enemy.destroy();
                            }
                            bullet.destroy();
                        }
                    }
                }
            }
        }
    }

    //Метод на проверку статуса игры, на наш корабль нельзя применить метод dispose().
    //Поэтому игра продалжается дальше, но в консоль выводится сообщение.
    private boolean checkStatusGame(){
        if(mainShip.getHP() <= 0){
            System.out.println("You lose");
            return false;
        }
        return true;
    }

    private void freeAllDestroyed() {
        bulletPool.freeAllDestroyedActiveObjects();
        enemyPool.freeAllDestroyedActiveObjects();
    }

    private void draw() {
        Gdx.gl.glClearColor(0.5f, 0.7f, 0.8f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        background.draw(batch);
        for (Star star : stars) {
            star.draw(batch);
        }
        mainShip.draw(batch);
        bulletPool.drawActiveSprites(batch);
        enemyPool.drawActiveSprites(batch);
        batch.end();
    }
}
