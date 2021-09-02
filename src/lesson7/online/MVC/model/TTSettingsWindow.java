package lesson7.online.MVC.model;

import lesson7.online.MVC.veiw.GameMap;

public class TTSettingsWindow {
    private int fieldSizeX;
    private int fieldSizeY;
    private int winLength;
    private GameMode gameMode;
    private GameMap gameMap;

    public TTSettingsWindow(int fieldSize, int winLength, GameMode gameMode, GameMap gameMap) {
        this.gameMode = gameMode;
        this.winLength = winLength;
        this.fieldSizeX = fieldSize;
        this.fieldSizeY = fieldSize;
        this.gameMap = gameMap;
    }

    public int getFieldSizeX() {
        return fieldSizeX;
    }
    public int getFieldSizeY() {
        return fieldSizeY;
    }
    public int getWinLength() {
        return winLength;
    }
    public GameMode getGameMode() {
        return gameMode;
    }
    public GameMap getGameMap() {
        return gameMap;
    }
}
