package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Star extends ApplicationAdapter {
	SpriteBatch batch;
	Texture img;
	static Sprite backSprite;


	
	@Override
	public void create () {
		batch = new SpriteBatch();
		img = new Texture("space.jpg");
		//Определяем размер приложения, для корректной отрисовки фона.
		// В app приложении, высоты определяется не совсем корректно.
		// Она в половину меньше высоты экрана.
		Gdx.app.getGraphics();
		int width = Gdx.app.getGraphics().getWidth();
		int height = Gdx.app.getGraphics().getHeight();
		backSprite = new Sprite(img);
		//Изменение высоты и ширины фона.
		backSprite.setSize(width, height);
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		batch.draw(img, 0, 0);
		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		img.dispose();
	}
}
