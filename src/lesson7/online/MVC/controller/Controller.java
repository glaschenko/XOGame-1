package lesson7.online.MVC.controller;

import lesson7.online.MVC.model.TTGame;
import lesson7.online.MVC.model.TTGameListener;
import lesson7.online.MVC.model.TTSettingsWindow;

import java.util.logging.Level;

public class Controller {
    private final TTGame game;

    public Controller() {
        this.game = new TTGame();
    }

    public void handleStart(TTSettingsWindow settings){
        game.start(settings);
    }

    public TTGame getGame() {
        return game;
    }

    public void setGameListener(TTGameListener gameListener){
        game.setSettings(gameListener);
    }

    public void handleClick(int cellX, int cellY) {
        TTGame.logger.log(Level.INFO, "X: " + cellX + ", Y:" + cellY);
        game.makeTurn(cellX, cellY);
    }
}
