package com.mygdx.game.base;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

public class Font extends BitmapFont {
    public Font(String fontFile, String imageFile){
        super(Gdx.files.internal(fontFile), Gdx.files.internal(imageFile), false, false);
    }

    //Скалирование размера, пропорционально мировым координатам.
    public void setSize(float size){
        getData().setScale(size/getCapHeight());
    }

    public GlyphLayout draw(Batch batch, CharSequence str, float x, float y, float targetWidth, int halign, boolean wrap) {
        return super.draw(batch, str, x, y, 0, halign, false);
    }
}
