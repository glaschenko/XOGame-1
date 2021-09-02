package lesson7.online.MVC.controller;

import lesson7.online.MVC.model.TTGame;
import lesson7.online.MVC.model.TTSettingsWindow;

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

    public void handleClick(int cellX, int cellY) {
        System.out.println("X: " + cellX + ", Y:" + cellY); //todo use logger
        game.makeTurn(cellX, cellY);
    }
}
