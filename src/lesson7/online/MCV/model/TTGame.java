package lesson7.online.MCV.model;

import java.awt.*;
import java.util.Random;

public class TTGame {
    public static final Random random = new Random();

    private int fieldSizeX;
    private int fieldSizeY;
    private int winLength;

    private PlayerSymbols[][] field;
    private WinType currentStateGameOver;
    private Coordinates coordinatesBeginningVictoryLine;
    private GameState gameState;
    private PlayerSymbols currentTurn; //todo инициализацию убрал в метод start после ничьи игрок начинает с нолика
    private TTGameSettings settings;

    public TTGame(TTGameSettings settings) {
        this.settings = settings;
        gameState = GameState.NOT_STARTED;
        field = new PlayerSymbols [settings.getCellWidth()][settings.getCellHeight()];
    }

    void start(GameMode gameMode, int fieldSizeY, int fieldSizeX, int winLength, Color fieldColor) {
        this.gameMode = gameMode;
        this.fieldSizeX = fieldSizeX;
        this.fieldSizeY = fieldSizeY;
        this.winLength = winLength;
        setBackground(fieldColor);
        field = new PlayerSymbols [fieldSizeY][fieldSizeX];
        gameState = GameState.STARTED;
        currentTurn  = PlayerSymbols.CROSS; //todo инициализация currentTurn
        repaint();
    }
    private void makePlayerTurn(int cellX, int cellY, PlayerSymbols playerSymbols) {
        field[cellY][cellX] = playerSymbols;
    }

    private void handlerTurn() {
        WinType winType = checkWin(currentTurn);
        if (winType != null) {
            setGameOver(winType);
            return;
        }
        if (isFullMap()) {
            setGameOver(WinType.DRAW);
        }
        currentTurn = currentTurn == PlayerSymbols.ZERO ? PlayerSymbols.CROSS : PlayerSymbols.ZERO;
    }

    private void setGameOver(WinType gameOverState) {
        currentStateGameOver = gameOverState;
        gameState = GameState.FINISHED;
        repaint();
    }

    private void makeAITurn() {
        Coordinates winCoordinates = turnAIWinCell(PlayerSymbols.ZERO);
        if (winCoordinates == null) {
            winCoordinates = turnAIWinCell(PlayerSymbols.CROSS);
        }
        if (winCoordinates == null) {
            winCoordinates = findRandomTurn();
        }
        field[winCoordinates.x][winCoordinates.y] = PlayerSymbols.ZERO;
    }

    private Coordinates findRandomTurn() {
        int x;
        int y;
        do { //todo replace with a more efficient algo
            x = random.nextInt(fieldSizeX);
            y = random.nextInt(fieldSizeY);
        } while (!isEmptyCell(x, y));
        return new Coordinates(x, y);
    }
    private Coordinates turnAIWinCell(PlayerSymbols playerSymbols) {
        for (int i = 0; i < fieldSizeY; i++) {
            for (int j = 0; j < fieldSizeX; j++) {
                if (isEmptyCell(j, i)) {
                    field[i][j] = playerSymbols;
                    WinType winType = checkWin(playerSymbols);
                    if (winType != null) {
                        return new Coordinates(i, j);
                    }
                    field[i][j] = null;
                }
            }
        }
        return null;
    }

    private WinType checkWin(PlayerSymbols playerSymbols) {
        //todo better use hor/ver instead of x/y
        for (int i = 0; i < fieldSizeX; i++) {
            for (int j = 0; j < fieldSizeY; j++) {
                if (checkLine(i, j, 1, 0, winLength, playerSymbols)) {
                    coordinatesBeginningVictoryLine = new Coordinates(i, j);
                    return WinType.HORIZONTAL;
                }
                if (checkLine(i, j, 0, 1, winLength, playerSymbols)) {
                    coordinatesBeginningVictoryLine = new Coordinates(i, j);
                    return WinType.VERTICAL;
                }
                if (checkLine(i, j, 1, 1, winLength, playerSymbols)) {
                    coordinatesBeginningVictoryLine = new Coordinates(i, j);
                    return WinType.DIAGONAL;
                }
                if (checkLine(i, j, 1, -1, winLength, playerSymbols)) {
                    coordinatesBeginningVictoryLine = new Coordinates(i, j);
                    return WinType.REVERSE_DIAGONAL;
                }
                coordinatesBeginningVictoryLine = new Coordinates(i, j);
                //todo при игре с оппанентом при ничье падает с ошибкой
            }
        }
        return null;
    }

    //todo better parameters and method name
    private boolean checkLine(int x, int y, int vx, int vy, int len, PlayerSymbols characterSymbol) {
        final int farX = x + (len - 1) * vx;
        final int farY = y + (len - 1) * vy;
        if (isValidCell(farX, farY)) {
            return false;
        }
        for (int i = 0; i < len; i++) {
            if (field[y + i * vy][x + i * vx] != characterSymbol) {
                return false;
            }
        }
        return true;
    }
    //todo can be simply replaced with turns count.
    private boolean isFullMap() {
        for (int i = 0; i < fieldSizeX; i++) {
            for (int j = 0; j < fieldSizeY; j++) {
                if (field[i][j] == null) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isValidCell(int x, int y) {
        return x < 0 || x >= fieldSizeX || y < 0 || y >= fieldSizeY;
    }

    private boolean isEmptyCell(int x, int y) {
        return field[y][x] == null;
    }

}
