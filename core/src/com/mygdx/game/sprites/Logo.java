package com.mygdx.game.sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class Logo {
    private Texture logo;
    private Vector2 position;
    private Vector2 direction;

    public Logo(Texture texture){
        this.logo = texture;
        this.position = new Vector2(0,0);
    }

    public void moveTo(Vector2 touch) {
        direction = new Vector2((touch.x - position.x) / touch.x,(touch.y - position.y) / touch.y);
        position.add(direction); //По идее в direction должен расчитываться вектор направления, но он равен нулю. Поэтому перемещение мгновенное.
        setPosition(touch);
    }

    public void draw(SpriteBatch batch) {
        batch.draw(logo, position.x, position.y,0.2f,0.2f);
    }

    private void setPosition(Vector2 position) {
        this.position = position;
    }
}
