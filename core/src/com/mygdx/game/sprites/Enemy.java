package com.mygdx.game.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import com.mygdx.game.base.Ship;
import com.mygdx.game.math.Rect;
import com.mygdx.game.pool.BulletPool;

public class Enemy extends Ship {
    //Флаг врага
    private static final int FLAG_ENEMY = 1;

    public Enemy(BulletPool bulletPool, Rect worldBounds) {
        this.bulletPool = bulletPool;
        this.worldBounds = worldBounds;
        v = new Vector2();
        v0 = new Vector2();
        bulletV = new Vector2();
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        //Установил проверку, при которой проверяется позиция корабля по Y. И если она меньше
        //верхней границы - половины высоты корабля, то ускоряю корабль. После скорость становится
        //дефолтной, выстрел совершается при выходе на дефолтную скорость.
        if (pos.y > worldBounds.getTop() - halfHeight){
            v.set(0, -0.5f);
        } else {
            v.set(v0);
            if (getBottom() <= worldBounds.getBottom()) {
                destroy();
            }
            reloadTimer += delta;
            if (reloadTimer >= reloadInterval) {
                reloadTimer = 0f;
                shoot();
            }
        }
    }

    public void set(
            TextureRegion[] regions,
            Vector2 v0,
            TextureRegion bulletRegion,
            float bulletHeight,
            float bulletVY,
            int damage,
            float reloadInterval,
            int hp,
            float height
    ) {
        this.regions = regions;
        this.v0.set(v0);
        this.bulletRegion = bulletRegion;
        this.bulletHeight = bulletHeight;
        this.bulletV.set(0, bulletVY);
        this.damage = damage;
        this.reloadInterval = reloadInterval;
        this.reloadTimer = reloadInterval;
        this.hp = hp;
        this.v.set(v0);
        setHeightProportion(height);
    }

    private void shoot() {
        Bullet bullet = bulletPool.obtain();
        bullet.set(this, bulletRegion, pos.x, pos.y - halfHeight, bulletV, bulletHeight, worldBounds, damage, FLAG_ENEMY);
        shootSound = Gdx.audio.newSound(Gdx.files.internal("sounds/shoot.wav"));
        shootSound.play();
    }

    public int getDamage(){
        return this.damage;
    }

    //Добавил методы damageHP и getHP, что бы наносить урон и проверять текущее ХП.
    public void damageHP(int damage){
        this.hp -= damage;
        System.out.println("HP = " + this.hp);
    }

    public int getHP(){return this.hp;}
}
