package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.mygdx.game.screen.MenuScreen;

public class StartGame extends Game {

	@Override
	public void create() {
		setScreen(new MenuScreen(this));
	}
}
