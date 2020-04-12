package com.mygdx.game.sprites;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.base.Sprite;
import com.mygdx.game.math.Rect;

public class Bullet extends Sprite {
    private Rect worldBounds;
    private final Vector2 v = new Vector2();
    private int damage;
    private Object owner;

    //Добавил флаг, дающий понять чей это выстрел
    private int flagShip;

    public Bullet() {
        regions = new TextureRegion[1];
    }

    //Изменил сеттер, теперь передается позиция posX и posY в качестве начальных для пули.
    //Раньше передавался вектор, но из-за этого пуля летела из центра корабля.
    //Теперь на вход поступают координаты posX и posY, которые записываются в вектор пули
    //Звук выстрела происходил при инициализации объекта корабля.
    //Поэтому перенес метод shoot() в класс Enemy и MainShip.
    public void set(
            Object owner,
            TextureRegion region,
            float posX,
            float posY,
            Vector2 v0,
            float height,
            Rect worldBounds,
            int damage,
            int flagShip
    ) {
       this.owner = owner;
       this.regions[0] = region;
       this.pos.set(posX, posY);
       this.v.set(v0);
       setHeightProportion(height);
       this.worldBounds = worldBounds;
       this.damage = damage;
       this.flagShip = flagShip;
    }

    @Override
    public void update(float delta) {
        pos.mulAdd(v, delta);
        if (isOutside(worldBounds)) {
            destroy();
        }
    }

    public void setPosition(Vector2 size){
        this.pos.set(pos.x, pos.y + size.y);
    }

    public int getDamage() {
        return damage;
    }

    public Object getOwner() {
        return owner;
    }

    public int getFlagShip(){return this.flagShip; }
}
