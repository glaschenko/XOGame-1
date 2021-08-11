package lesson7.online;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.Objects;
import java.util.Random;
import java.util.logging.Level;

public class GameMap extends JPanel {

    private static final Image CROSS_IMAGE;
    private static final Image ZERO_IMAGE;
    private static final Image HORIZONTAL_LINE;
    private static final Image VERTICAL_LINE;
    private static final Image DIAGONAL_LINE;
    private static final Image REVERSE_DIAGONAL_LINE;
    private static final Random random = new Random();

    private PlayerSymbols currentTurn = PlayerSymbols.CROSS;
    private WinType currentStateGameOver;

    private GameMode gameMode;
    private int fieldSizeX;
    private int fieldSizeY;
    private int winLength;
    private PlayerSymbols[][] field;
    private int cellWidth;
    private int cellHeight;
    private Coordinates coordinatesBeginningVictoryLine;
    private GameState gameState;

    static {
        try {
            CROSS_IMAGE = ImageIO.read(Objects.requireNonNull(GameMap.class.getResourceAsStream("resources//cross.png")));
            ZERO_IMAGE = ImageIO.read(Objects.requireNonNull(GameMap.class.getResourceAsStream("resources//zero.png")));
            HORIZONTAL_LINE = ImageIO.read(Objects.requireNonNull(GameMap.class.getResourceAsStream("resources//HORIZONTAL.png")));
            VERTICAL_LINE = ImageIO.read(Objects.requireNonNull(GameMap.class.getResourceAsStream("resources//VERTICAL.png")));
            DIAGONAL_LINE = ImageIO.read(Objects.requireNonNull(GameMap.class.getResourceAsStream("resources//DIAGONAL.png")));
            REVERSE_DIAGONAL_LINE = ImageIO.read(Objects.requireNonNull(GameMap.class.getResourceAsStream("resources//REVERSE_DIAGONAL.png")));
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load images from resources", e);
        }
    }

    GameMap() {
        setBackground(Color.WHITE);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                update(e);
            }
        });
        gameState = GameState.NOT_STARTED;
    }

    void start(GameMode gameMode, int fieldSizeX, int fieldSizeY, int winLength, Color fieldColor) {
        this.gameMode = gameMode;
        this.fieldSizeX = fieldSizeX;
        this.fieldSizeY = fieldSizeY;
        this.winLength = winLength;
        setBackground(fieldColor);
        currentTurn = PlayerSymbols.CROSS;
        field = new PlayerSymbols[fieldSizeX][fieldSizeY];
        gameState = GameState.STARTED;
        repaint();
    }

    private void update(MouseEvent e) {
        if (gameState != GameState.STARTED) return;
        int cellX = e.getX() / cellWidth;
        int cellY = e.getY() / cellHeight;
        GameWindow.logger.log(Level.INFO, "X: " + cellX + ", Y:" + cellY);
        if (isValidCell(cellX, cellY) || !isEmptyCell(cellX, cellY)) {
            return;
        }
        makePlayerTurn(cellY, cellX, currentTurn);
        handleTurn();
        if (gameMode == GameMode.HUMAN_VS_AI && currentTurn == PlayerSymbols.ZERO) {
            makeAITurn();
            handleTurn();
        }
        repaint();
    }

    private void handleTurn() {
        WinType winType = checkWin(currentTurn);
        if (winType != null) {
            setGameOver(winType);
        } else if (isFullMap()) {
            setGameOver(WinType.DRAW);
        }
        currentTurn = currentTurn == PlayerSymbols.ZERO ? PlayerSymbols.CROSS : PlayerSymbols.ZERO;
    }

    private void setGameOver(WinType gameOverState) {
        currentStateGameOver = gameOverState;
        gameState = GameState.FINISHED;
        repaint();
    }

    private void makePlayerTurn(int cellY, int cellX, PlayerSymbols playerSymbols) {
        field[cellY][cellX] = playerSymbols;
    }

    private void render(Graphics g) throws IOException {
        if (gameState == GameState.NOT_STARTED) return;
        int width = getWidth();
        int height = getHeight();
        cellWidth = width / fieldSizeX;
        cellHeight = height / fieldSizeY;

        drawGrid(g, width, height);
        drawPlayerSymbols(g);
        if (gameState == GameState.FINISHED) {
            showGameOverState(g);
        }
    }

    private void drawPlayerSymbols(Graphics g) {
        for (int y = 0; y < fieldSizeY; y++) {
            for (int x = 0; x < fieldSizeX; x++) {
                if (!isEmptyCell(x, y)) {
                    Image image = field[y][x] == PlayerSymbols.CROSS ? CROSS_IMAGE : ZERO_IMAGE;
                    g.drawImage(image, x * cellWidth + 10, y * cellHeight + 10, cellWidth - 20, cellHeight - 20, null);
                }
            }
        }
    }

    private void drawGrid(Graphics g, int width, int height) {
        g.setColor(Color.BLACK);
        for (int i = 1; i < fieldSizeY; i++) {
            int y = i * cellHeight;
            g.drawLine(0, y, width, y);
        }
        for (int i = 1; i < fieldSizeX; i++) {
            int x = i * cellWidth;
            g.drawLine(x, 0, x, height);
        }
    }

    private void showGameOverState(Graphics g) throws IOException {
        int startX = coordinatesBeginningVictoryLine.x;
        int startY = coordinatesBeginningVictoryLine.y;
        switch (currentStateGameOver) {
            case HORIZONTAL -> {
                g.drawImage(HORIZONTAL_LINE, startX * cellWidth - 30, startY * cellHeight + cellHeight / 2 - cellHeight / 8,
                        cellWidth * winLength + 60, cellHeight / 4, null);
            }
            case VERTICAL -> {
                g.drawImage(VERTICAL_LINE, startX * cellWidth + cellWidth / 2 - cellWidth / 8,
                        startY * cellHeight - 30, cellWidth / 4, cellHeight * winLength + 60, null);
            }
            case DIAGONAL -> {
                for (int s = 0; s < winLength; s++) {
                    g.drawImage(DIAGONAL_LINE, (startX + s) * cellWidth - 15, (startY + s) * cellHeight - 15,
                            cellWidth + 15, cellHeight + 15, null);
                }
            }
            case REVERSE_DIAGONAL -> {
                for (int s = 0; s < winLength; s++) {
                    g.drawImage(REVERSE_DIAGONAL_LINE, (startX + s) * cellWidth - 15, (startY - s) * cellHeight - 15, cellWidth + 15, cellHeight + 15, null);
                }
            }
            case DRAW -> {
                g.setColor(Color.WHITE);
                g.fillRect(120, 170, 250, 100);
                g.setColor(Color.BLACK);
                g.setFont(new Font("Times New Roman", Font.ITALIC, 70));
                g.drawString("Ничья!", 150, getHeight() / 2);
            }
            default -> throw new RuntimeException("Непредвиденная ошибка: " + currentStateGameOver);
        }
    }

    private void makeAITurn() {
        //find winning turn
        Coordinates winCoordinates = findWinningTurn(PlayerSymbols.ZERO);
        if(winCoordinates == null){
            //can we prevent player from winning?
            winCoordinates = findWinningTurn(PlayerSymbols.CROSS);
        }
        if(winCoordinates == null){
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
        return new Coordinates(x,y);
    }

    private Coordinates findWinningTurn(PlayerSymbols symbol){
        for (int i = 0; i < fieldSizeY; i++) {
            for (int j = 0; j < fieldSizeX; j++) {
                if (isEmptyCell(j, i)) {
                    field[i][j] = symbol;
                    WinType winType = checkWin(symbol);
                    if (winType != null) {
                        return new Coordinates(i,j);
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

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        try {
            render(g);
        } catch (IOException e) { //todo why are you catching this one?
            e.printStackTrace();
        }
    }
}
