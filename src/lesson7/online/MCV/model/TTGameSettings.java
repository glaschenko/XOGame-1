package lesson7.online.MCV.model;

public class TTGameSettings {

    private int cellWidth;
    private int cellHeight;
    private GameMode gameMode;

    public TTGameSettings(int cellWidth, int cellHeight, GameMode gameMode) {
        this.cellWidth = cellWidth;
        this.cellHeight = cellHeight;
        this.gameMode = gameMode;
    }

    public int getCellWidth() {
        return cellWidth;
    }

    public int getCellHeight() {
        return cellHeight;
    }

    public GameMode getGameMode() {
        return gameMode;
    }
}
