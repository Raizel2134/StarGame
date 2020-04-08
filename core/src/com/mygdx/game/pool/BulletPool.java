package com.mygdx.game.pool;

import com.mygdx.game.base.SpritesPool;
import com.mygdx.game.sprites.Bullet;

public class BulletPool extends SpritesPool<Bullet> {

    @Override
    protected Bullet newObject() {
        return new Bullet();
    }
}
