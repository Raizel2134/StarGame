package com.mygdx.game.sprites;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.sound.MainSound;
import com.mygdx.game.base.Sprite;
import com.mygdx.game.math.Rect;

public class Bullet extends Sprite {

    private Rect worldBounds;
    private final Vector2 v = new Vector2();
    private int damage;
    private Object owner;
    private String shootMusic;
    private MainSound shootMainSound;

    public Bullet() {
        regions = new TextureRegion[1];
    }

    public void set(
            Object owner,
            TextureRegion region,
            Vector2 pos0,
            Vector2 v0,
            float height,
            Rect worldBounds,
            int damage
    ) {
       this.owner = owner;
       this.regions[0] = region;
       this.pos.set(pos0);
       this.v.set(v0);
       setHeightProportion(height);
       this.worldBounds = worldBounds;
       this.damage = damage;
       this.shootMusic = "sounds/shoot.wav";
       shootMainSound = new MainSound(shootMusic);
    }

    @Override
    public void update(float delta) {
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
