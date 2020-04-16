package com.mygdx.game.screen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.base.Font;
import com.mygdx.game.pool.EnemyPool;
import com.mygdx.game.base.BaseScreen;
import com.mygdx.game.exception.GameException;
import com.mygdx.game.math.Rect;
import com.mygdx.game.pool.BulletPool;
import com.mygdx.game.pool.ExplosionPool;
import com.mygdx.game.sprites.Background;
import com.mygdx.game.sprites.Bullet;
import com.mygdx.game.sprites.ButtonBack;
import com.mygdx.game.sprites.ButtonNewGame;
import com.mygdx.game.sprites.ButtonPause;
import com.mygdx.game.sprites.ButtonResume;
import com.mygdx.game.sprites.Enemy;
import com.mygdx.game.sprites.GameOver;
import com.mygdx.game.sprites.MainShip;
import com.mygdx.game.sprites.Star;
import com.mygdx.game.utils.EnemyEmitter;
import java.util.List;


public class GameScreen extends BaseScreen {
    private final Game game;
    public GameScreen(Game game) {
        this.game = game;
    }

    private enum STATE {PLAYING, PAUSE, GAME_OVER}
    private static final int STAR_COUNT = 64;
    private static final float FONT_SIZE = 0.02f;
    private static final float FONT_MARGIN = 0.01f;
    private static final String FRAGS = "Frags = ";
    private static final String HP = "HP = ";
    private static final String LEVEL = "Level = ";
    private Texture bg;
    private Background background;

    private TextureAtlas atlas;
    private TextureAtlas atlasGUI;

    private Star[] stars;
    private MainShip mainShip;
    private GameOver gameOver;
    private ButtonNewGame newGame;
    private ButtonPause pause;
    private ButtonResume resume;
    private ButtonBack back;
    private MenuScreen menu;

    private BulletPool bulletPool;
    private EnemyPool enemyPool;
    private ExplosionPool explosionPool;
    private EnemyEmitter enemyEmitter;
    private Music music;
    private STATE state;
    private STATE statePrev;
    private int frags;

    private Font font;
    private StringBuilder sbFrags;
    private StringBuilder sbHp;
    private StringBuilder sbLevel;

    @Override
    public void show() {
        super.show();
        bg = new Texture("textures/bg.jpg");
        atlas = new TextureAtlas(Gdx.files.internal("textures/mainAtlas.tpack"));
        atlasGUI = new TextureAtlas(Gdx.files.internal("textures/gui/gui.pack"));
        Sound explosion = Gdx.audio.newSound(Gdx.files.internal("sounds/explosion.wav"));
        bulletPool = new BulletPool();
        explosionPool = new ExplosionPool(atlas, explosion);
        enemyPool = new EnemyPool(bulletPool, explosionPool, worldBounds);
        enemyEmitter = new EnemyEmitter(atlas, enemyPool, worldBounds);
        font = new Font("font.fnt","font.png");
        font.setSize(FONT_SIZE);
        sbFrags = new StringBuilder();
        sbLevel = new StringBuilder();
        sbHp = new StringBuilder();
        music = Gdx.audio.newMusic(Gdx.files.internal("sounds/fight.mp3"));
        music.setLooping(true);
        music.play();
        initSprites();
        state = STATE.PLAYING;
        statePrev = STATE.PLAYING;
        frags = 0;
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        update(delta);
        checkCollisions();
        freeAllDestroyed();
        draw();
    }

    //Скалирование размера, пропорционально мировым координатам.
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
        resume.resize(worldBounds);
        back.resize(worldBounds);
        pause.resize(worldBounds);
    }

    @Override
    public void dispose() {
        bg.dispose();
        atlas.dispose();
        bulletPool.dispose();
        enemyPool.dispose();
        music.dispose();
        font.dispose();
        super.dispose();
    }

    //Пауза при сворачивании.
    public void pause(){
        statePrev = state;
        state = STATE.PAUSE;
        music.pause();
    }

    //Пауза при нажатии кнопки.
    public void pauseGame(){
        statePrev = state;
        state = STATE.PAUSE;
    }

    //Воспроизведение при разворачивании.
    public void resume(){
        state = statePrev;
        music.play();
    }

    //Воспроизведение при нажатии кнопки.
    public void resumeGame(){
        state = statePrev;
    }

    //Движение по нажатию клавиши клавиатуры.
    @Override
    public boolean keyDown(int keycode) {
        if (state == STATE.PLAYING) {
            mainShip.keyDown(keycode);
        }
        return false;
    }

    //Остановка при отпускании клавиши клавиатуры.
    @Override
    public boolean keyUp(int keycode) {
        if (state == STATE.PLAYING) {
            mainShip.keyUp(keycode);
        }
        return false;
    }

    //Перемещение по нажатию мышью\касанием пальцем по игровому полю.
    @Override
    public boolean touchDown(Vector2 touch, int pointer, int button) {
        switch (state){
            case PLAYING:
                pause.touchDown(touch, pointer, button);
                //mainShip.touchDown(touch, pointer, button);
                break;
            case PAUSE:
                resume.touchDown(touch, pointer, button);
                back.touchDown(touch, pointer, button);
                break;
            case GAME_OVER:
                newGame.touchDown(touch, pointer, button);
                break;
        }
        return false;
    }

    //Остановка перемещения.
    @Override
    public boolean touchUp(Vector2 touch, int pointer, int button) {
        switch (state){
            case PLAYING:
                pause.touchUp(touch, pointer, button);
                //mainShip.touchDown(touch, pointer, button);
                break;
            case PAUSE:
                resume.touchUp(touch, pointer, button);
                back.touchUp(touch, pointer, button);
                break;
            case GAME_OVER:
                newGame.touchUp(touch, pointer, button);
                break;
        }
        return false;
    }

    //Пермещение по касанию.
    @Override
    public boolean touchDragged(Vector2 touch, int pointer) {
        if (state == STATE.PLAYING) {
            mainShip.touchDragged(touch, pointer);
        }
        return false;
    }

    //Инициализация игровых объектов.
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
            resume = new ButtonResume(atlasGUI, this);
            pause = new ButtonPause(atlasGUI, this);
            back = new ButtonBack(atlasGUI,game);
        } catch (GameException e) {
            throw new RuntimeException(e);
        }
    }

    //Обновление состояний игровых объектов.
    private void update(float delta) {
        for (Star star : stars) {
            star.update(delta);
        }
        explosionPool.updateActiveSprites(delta);
        if (state == STATE.PLAYING) {
            mainShip.update(delta);
            bulletPool.updateActiveSprites(delta);
            enemyPool.updateActiveSprites(delta);
            enemyEmitter.generate(delta, frags);
        }
    }

    //Проверка колизиий.
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
                frags++;
                mainShip.damage(enemy.getDamage());
            }
            for (Bullet bullet : bulletList) {
                if (bullet.getOwner() != mainShip || bullet.isDestroyed()) {
                    continue;
                }
                if (enemy.isBulletCollision(bullet)) {
                    enemy.damage(bullet.getDamage());
                    bullet.destroy();
                    if(enemy.isDestroyed()){
                        frags++;}
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

    //Очистка всех игровых объектов, перед началом новой игры.
    private void freeAllDestroyed() {
        bulletPool.freeAllDestroyedActiveObjects();
        enemyPool.freeAllDestroyedActiveObjects();
        explosionPool.freeAllDestroyedActiveObjects();
    }

    //Вызов методов сбрасывающих настройки, при начале новой игры.
    public void newGame() {
        state = STATE.PLAYING;
        mainShip.newGame();
        bulletPool.freeAllActiveObjects();
        enemyPool.freeAllActiveObjects();
        explosionPool.freeAllActiveObjects();
        frags = 0;
    }

    //Отрисовка объектов.
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
                pause.draw(batch);
                break;
            case GAME_OVER:
                gameOver.draw(batch);
                newGame.draw(batch);
                break;
            case PAUSE:
                mainShip.draw(batch);
                enemyPool.drawActiveSprites(batch);
                bulletPool.drawActiveSprites(batch);
                resume.draw(batch);
                back.draw(batch);
                break;
        }
        explosionPool.drawActiveSprites(batch);
        printInfo();
        batch.end();
    }

    private void printInfo(){
        sbFrags.setLength(0);
        sbHp.setLength(0);
        sbLevel.setLength(0);
        font.draw(batch, sbLevel.append(LEVEL).append(enemyEmitter.getLevel()), worldBounds.getLeft() + FONT_MARGIN, worldBounds.getTop() - FONT_MARGIN);
        font.draw(batch, sbHp.append(HP).append(mainShip.getHp()), worldBounds.getLeft() + FONT_MARGIN, worldBounds.getTop() - (FONT_MARGIN * 2 + FONT_SIZE));
        font.draw(batch, sbFrags.append(FRAGS).append(frags), worldBounds.getLeft() + FONT_MARGIN, worldBounds.getTop() - (FONT_MARGIN * 3 + FONT_SIZE * 2));
    }
}
