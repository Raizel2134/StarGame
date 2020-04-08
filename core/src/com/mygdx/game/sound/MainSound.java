package com.mygdx.game.sound;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

public class MainSound {

    //Реализовал все через Music, необходими ли для воспроизведения звуков использовать Sound?
    private Music music;

    public MainSound(String path){
        this.music = Gdx.audio.newMusic(Gdx.files.internal(path));
        playMusic(this.music);
    }

    private void playMusic(Music music){
        music.setVolume(0.1f);
        music.play();
    }

    public void dispose(){
        this.music.stop();
    }
}