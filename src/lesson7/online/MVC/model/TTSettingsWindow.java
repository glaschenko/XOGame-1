package lesson7.online.MVC.model;

public class TTSettingsWindow {
    private int fieldSizeX;
    private int fieldSizeY;
    private int winLength;

    private GameMode gameMode;

    public TTSettingsWindow(int fieldSize, int winLength, GameMode gameMode) {
        this.gameMode = gameMode;
        this.winLength = winLength;
        this.fieldSizeX = fieldSize;
        this.fieldSizeY = fieldSize;
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
}
