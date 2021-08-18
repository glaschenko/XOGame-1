package lesson7.online.mvc.model;

import java.util.Random;

public class TTGame {
    public static final Random random = new Random();
    private TTGameListener listener;
    private TTGameSettings settings;

    private PlayerSymbols[][] field;
    private WinType currentStateGameOver;
    private Coordinates coordinatesBeginningVictoryLine;
    private GameState gameState;
    private PlayerSymbols currentTurn; //todo инициализацию убрал в метод start после ничьи игрок начинает с нолика


    public TTGame(TTGameSettings settings) {
        this.settings = settings;
        gameState = GameState.NOT_STARTED;
        field = new PlayerSymbols [settings.getFieldSize()][settings.getFieldSize()];
    }

    public void setListener(TTGameListener listener) {
        this.listener = listener;
    }

    public void start(TTGameSettings settings) {
        this.settings = settings;
        field = new PlayerSymbols [settings.getFieldSize()][settings.getFieldSize()];
        gameState = GameState.STARTED;
        currentTurn  = PlayerSymbols.CROSS;
        listener.onGameStarted();
    }

    public void makeTurn(int x, int y){
        if (getGameState() != GameState.STARTED) return;
        if (isValidCell(x, y) || !isEmptyCell(x, y)) {
            return;
        }

        makePlayerTurn(x, y, currentTurn);
        handleTurn();
        if (settings.getGameMode() == GameMode.HUMAN_VS_AI && gameState != GameState.FINISHED){
            makeAITurn();
            handleTurn();
        }
        listener.onTurn();
    }

    private void makePlayerTurn(int cellX, int cellY, PlayerSymbols playerSymbols) {
        field[cellY][cellX] = playerSymbols;
    }

    private void handleTurn() {
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
        do {
            x = random.nextInt(settings.getFieldSize());
            y = random.nextInt(settings.getFieldSize());
        } while (!isEmptyCell(x, y));
        return new Coordinates(x, y);
    }

    private Coordinates turnAIWinCell(PlayerSymbols playerSymbols) {
        for (int i = 0; i < settings.getFieldSize(); i++) {
            for (int j = 0; j < settings.getFieldSize(); j++) {
                if (isEmptyCell(j, i)) {
                    field[i][j] = playerSymbols;
                    WinType winType = checkWin(playerSymbols);
                    if (winType != null) {
                        return new Coordinates(i, j); // почему new нужно?
                    }
                    field[i][j] = null;
                }
            }
        }
        return null;
    }

    private WinType checkWin(PlayerSymbols playerSymbols) {
        int winLength = settings.getWinLength();
        for (int i = 0; i < settings.getFieldSize(); i++) {
            for (int j = 0; j < settings.getFieldSize(); j++) {
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

    public boolean isValidCell(int x, int y) {
        return x < 0 || x >= settings.getFieldSize() || y < 0 || y >= settings.getFieldSize();
    }

    public boolean isEmptyCell(int x, int y) {
        return field[y][x] == null;
    }

    private boolean isFullMap() {
        for (int i = 0; i < settings.getFieldSize(); i++) {
            for (int j = 0; j < settings.getFieldSize(); j++) {
                if (field[i][j] == null) {
                    return false;
                }
            }
        }
        return true;
    }

    public WinType getCurrentStateGameOver() {
        return currentStateGameOver;
    }

    public Coordinates getCoordinatesBeginningVictoryLine() {
        return coordinatesBeginningVictoryLine;
    }

    public GameState getGameState() {
        return gameState;
    }

    public TTGameSettings getSettings() {
        return settings;
    }

    public PlayerSymbols getCellContent(int x, int y){
        return field[x][y];
    }
}
