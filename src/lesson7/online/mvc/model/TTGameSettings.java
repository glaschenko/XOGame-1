package lesson7.online.mvc.model;

public class TTGameSettings {
    private final int fieldSize;
    private final int winLength;
    private final GameMode gameMode;

    public TTGameSettings(int fieldSize, int winLength, GameMode gameMode) {
        this.fieldSize = fieldSize;
        this.winLength = winLength;
        this.gameMode = gameMode;
    }

    public int getFieldSize() {
        return fieldSize;
    }

    public int getWinLength() {
        return winLength;
    }

    public GameMode getGameMode() {
        return gameMode;
    }
}
