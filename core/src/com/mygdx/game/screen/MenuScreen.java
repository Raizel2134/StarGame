package com.mygdx.game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.base.BaseScreen;

public class MenuScreen extends BaseScreen {

    private Texture img;
    //Начальная позиция
    private Vector2 pos1;
    //Конечная позиция
    private Vector2 pos2;
    //Флаг начала
    private boolean start = true;
    //Вектор направления
    private Vector2 direction;

    @Override
    public void show() {
        super.show();
        img = new Texture("badlogic.jpg");
        pos1 = new Vector2();
        pos1.set( 0, 0);
        pos2 = new Vector2();
    }

    @Override
    public void render(float delta) {
       update(delta);
       draw();
    }

    @Override
    public void dispose() {
        batch.dispose();
        img.dispose();

        super.dispose();
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        pos2.set(screenX, Gdx.graphics.getHeight() - screenY);
        start = false;
        return false;
    }

    private void update(float delta) {
    }

    private void draw() {
        Gdx.gl.glClearColor(0.5f, 0.7f, 0.8f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        //Проверка на то первый запуск это или нет
        if(start&& pos1.x == 0 && pos2.y == 0){
            batch.draw(img, pos1.x, pos1.y);
        }   else {
            //Двигается постепенно замедляя скорость
            direction = new Vector2((pos2.x - pos1.x) / pos2.x,(pos2.y - pos1.y) / pos2.y);
            pos1.add(direction);
            //Двигается к месту нажатия
            batch.draw(img,pos1.x,pos1.y);
        }
        batch.end();
    }

}
