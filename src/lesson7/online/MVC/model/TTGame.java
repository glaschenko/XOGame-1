package lesson7.online.MVC.model;

import java.util.Random;
import java.util.logging.Logger;

public class TTGame {

    public static final Random random = new Random();
    public static Logger logger = Logger.getLogger(TTGame.class.getName());
    private PlayerSymbols[][] field;
    private WinType currentStateGameOver;
    private Coordinates coordinatesBeginningVictoryLine;
    private GameState gameState = GameState.NOT_STARTED;
    private PlayerSymbols currentTurn;
    private TTSettingsWindow settings;
    private TTGameListener listener;

    public void start(TTSettingsWindow settings) {
        this.settings = settings;
        field = new PlayerSymbols [settings.getFieldSizeX()][settings.getFieldSizeY()];
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
    handlerTurn();
        if (settings.getGameMode() == GameMode.HUMAN_VS_AI && getGameState() != GameState.FINISHED){
        makeAITurn();
        handlerTurn();
    }
        listener.onTurn();
}

    private void makePlayerTurn(int cellX, int cellY, PlayerSymbols currentTurn) {
        field[cellX][cellY] = currentTurn;
    }

    private void handlerTurn() {
        WinType winType = checkWin(currentTurn);
        if (winType != null) {
            setGameOver(winType);
            return;
        }
        if (isFullMap()) {
            setGameOver(WinType.DRAW);
            return;
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
        do { //todo replace with a more efficient algo
            x = random.nextInt(settings.getFieldSizeX());
            y = random.nextInt(settings.getFieldSizeY());
        } while (!isEmptyCell(x, y));
        return new Coordinates(x, y);
    }

    private Coordinates turnAIWinCell(PlayerSymbols playerSymbols) {
        for (int i = 0; i < settings.getFieldSizeX(); i++) {
            for (int j = 0; j < settings.getFieldSizeY(); j++) {
                if (isEmptyCell(i, j)) {
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
        for (int i = 0; i < settings.getFieldSizeX(); i++) {
            for (int j = 0; j < settings.getFieldSizeY(); j++) {
                if (checkLine(i, j, 1, 0, settings.getWinLength(), playerSymbols)) {
                    coordinatesBeginningVictoryLine = new Coordinates(i, j);
                    return WinType.HORIZONTAL;
                }
                if (checkLine(i, j, 0, 1, settings.getWinLength(), playerSymbols)) {
                    coordinatesBeginningVictoryLine = new Coordinates(i, j);
                    return WinType.VERTICAL;
                }
                if (checkLine(i, j, 1, 1, settings.getWinLength(), playerSymbols)) {
                    coordinatesBeginningVictoryLine = new Coordinates(i, j);
                    return WinType.DIAGONAL;
                }
                if (checkLine(i, j, 1, -1, settings.getWinLength(), playerSymbols)) {
                    coordinatesBeginningVictoryLine = new Coordinates(i, j);
                    return WinType.REVERSE_DIAGONAL;
                }
                coordinatesBeginningVictoryLine = new Coordinates(i, j);
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
            if (field[x + i * vx][y + i * vy] != characterSymbol) {
                return false;
            }
        }
        return true;
    }
    private boolean isFullMap() {
        for (int i = 0; i < settings.getFieldSizeX(); i++) {
            for (int j = 0; j < settings.getFieldSizeY(); j++) {
                if (field[i][j] == null) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isValidCell(int x, int y) {
        return x < 0 || x >= settings.getFieldSizeX() || y < 0 || y >= settings.getFieldSizeY();
    }

    public boolean isEmptyCell(int x, int y) {
        return field[x][y] == null;
    }
    public TTSettingsWindow getSettings() {
        return settings;
    }
    public PlayerSymbols  getCellContent(int x, int y){
        return field[x][y];
    }
    public Coordinates getCoordinatesBeginningVictoryLine(){
        return coordinatesBeginningVictoryLine;
    }
    public WinType getCurrentStateGameOver(){
        return currentStateGameOver;
    }
    public GameState getGameState(){
        return  gameState;
    }
    public void setSettings(TTGameListener listener) {
        this.listener = listener;
    }
}
