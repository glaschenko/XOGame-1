package lesson7.online.mvc.controller;

import lesson7.online.mvc.model.*;

import java.awt.*;

public class TTGameController {
    private final TTGame game;

    public TTGameController() {
        this.game = new TTGame(new TTGameSettings(4, 4, GameMode.HUMAN_VS_HUMAN));
    }

    public void setGameListener(TTGameListener gameListener) {
        game.setListener(gameListener);
    }

    public void handleStart(TTGameSettings settings){
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
