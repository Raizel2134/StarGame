package com.mygdx.game.sprites;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.base.Sprite;
import com.mygdx.game.math.Rect;

public class Bullet extends Sprite {
    private static final float DELTA_COEFF = 1.1f;
    private Rect worldBounds;
    private final Vector2 v = new Vector2();
    private int damage;
    private Object owner;

    private float deltaSave;

    public Bullet() {
        regions = new TextureRegion[1];
    }

    public void set(
            Object owner,
            TextureRegion region,
            Vector2 posBullet,
            Vector2 v0,
            float height,
            Rect worldBounds,
            int damage
    ) {
       this.owner = owner;
       this.regions[0] = region;
       this.pos.set(posBullet);
       this.v.set(v0);
       setHeightProportion(height);
       this.worldBounds = worldBounds;
       this.damage = damage;
    }

    @Override
    public void update(float delta) {
        if(deltaSave == 0f){
            deltaSave = delta;
        }
        if(delta > deltaSave * DELTA_COEFF){
            delta = deltaSave;
        }
        pos.mulAdd(v, delta);
        if (isOutside(worldBounds)) {
            destroy();
        }
    }

    public int getDamage() {
        return damage;
    }

    public Object getOwner() {
        return owner;
    }

}
