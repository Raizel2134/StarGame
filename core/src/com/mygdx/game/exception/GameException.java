package com.mygdx.game.exception;

public class GameException extends Exception{

    public GameException() {
        super();
    }

    public GameException(String s) {
        super(s);
    }

    public GameException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public GameException(Throwable throwable) {
        super(throwable);
    }

}
