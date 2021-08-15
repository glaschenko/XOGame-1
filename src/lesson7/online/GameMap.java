package lesson7.online;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.Objects;
import java.util.Random;

public class GameMap extends JPanel {

    private static final Image CROSS_IMAGE;
    private static final Image ZERO_IMAGE;
    private static final Image HORIZONTAL_IMAGE;
    private static final Image VERTICAL_IMAGE;
    private static final Image DIAGONAL_IMAGE;
    private static final Image REVERSE_DIAGONAL_IMAGE;
    public static final Random random = new Random();

    private int fieldSizeX;
    private int fieldSizeY;
    private int winLength;
    private int cellWidth;
    private int cellHeight;

    private PlayerSymbols [][] field;
    private WinType currentStateGameOver;
    private GameMode gameMode;
    private Coordinates coordinatesBeginningVictoryLine;
    private GameState gameState;
    private PlayerSymbols currentTurn; //todo инициализацию убрал в метод start после ничьи игрок начинает с нолика

    static {
        try {
            CROSS_IMAGE = ImageIO.read(Objects.requireNonNull(GameMap.class.getResourceAsStream("resources//cross.png")));
            ZERO_IMAGE = ImageIO.read(Objects.requireNonNull(GameMap.class.getResourceAsStream("resources//zero.png")));
            HORIZONTAL_IMAGE = ImageIO.read(Objects.requireNonNull(GameMap.class.getResourceAsStream("resources//HORIZONTAL.png")));
            VERTICAL_IMAGE = ImageIO.read(Objects.requireNonNull(GameMap.class.getResourceAsStream("resources//VERTICAL.png")));
            DIAGONAL_IMAGE = ImageIO.read(Objects.requireNonNull(GameMap.class.getResourceAsStream("resources//DIAGONAL.png")));
            REVERSE_DIAGONAL_IMAGE = ImageIO.read(Objects.requireNonNull(GameMap.class.getResourceAsStream("resources//REVERSE_DIAGONAL.png")));
        } catch (IOException e) {
           throw new IllegalArgumentException("Неудалось закрузить изображение из ресурсов", e);
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

    private void update(MouseEvent e) {
        if (gameState != GameState.STARTED) return;
        int cellX = e.getX() / cellWidth;
        int cellY = e.getY() / cellHeight;
        System.out.println("X: " + cellX + ", Y:" + cellY); //todo use logger
        if (isValidCell(cellX, cellY) || !isEmptyCell(cellX, cellY)) {
            return;
        }

        makePlayerTurn(cellX, cellY, currentTurn);
        handlerTurn();
        if (gameMode == GameMode.HUMAN_VS_AI && gameState != GameState.FINISHED){
            //todo убрал "currentTurn == PlayerSymbols.ZERO" добавил "gameState != GameState.FINISHED" не отрисовывает последний ход и зависает
            makeAITurn();
            handlerTurn();
        }
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

    private void render(Graphics g) throws IOException {
        if (gameState == GameState.NOT_STARTED) return;
        int width = getWidth();
        int height = getHeight();
        cellWidth = width / fieldSizeX;
        cellHeight = height / fieldSizeY;

        dragGrid(g, width, height);
        dragPlayerSymbols(g);
        if (gameState == GameState.FINISHED) {
            showGameOverState(g);
        }
    }

    private void dragPlayerSymbols(Graphics g){
        for (int y = 0; y < fieldSizeY; y++) {
            for (int x = 0; x < fieldSizeX; x++) {
                if (!isEmptyCell(x, y)) {
                    Image images = field[y][x] == PlayerSymbols.CROSS ? CROSS_IMAGE : ZERO_IMAGE;
                    g.drawImage(images, x * cellWidth + 10, y * cellHeight + 10, cellWidth - 20,
                            cellHeight - 20, null);
                }
            }
        }
    }

    private void dragGrid(Graphics g, int width, int height) {
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

    private void showGameOverState(Graphics g){
        int startX = coordinatesBeginningVictoryLine.x;
        int startY = coordinatesBeginningVictoryLine.y;
        switch (currentStateGameOver) {
            case HORIZONTAL -> {
                g.drawImage(HORIZONTAL_IMAGE, startX * cellWidth - 30, startY * cellHeight + cellHeight / 2
                        - cellHeight / 8, cellWidth * winLength + 60, cellHeight / 4, null);
            }
            case VERTICAL -> {
                g.drawImage(VERTICAL_IMAGE, startX * cellWidth + cellWidth / 2 - cellWidth / 8, startY * cellHeight
                        - 30, cellWidth / 4, cellHeight * winLength + 60, null);
            }
            case DIAGONAL -> {
                for (int s = 0; s < winLength; s++){
                    g.drawImage(DIAGONAL_IMAGE, (startX + s) * cellWidth - 15, (startY + s) * cellHeight - 15,
                            cellWidth + 15, cellHeight + 15, null);
                }
            }
            case REVERSE_DIAGONAL -> {
                for (int s = 0; s < winLength; s++){
                    g.drawImage(REVERSE_DIAGONAL_IMAGE, (startX + s) * cellWidth - 15, (startY - s) * cellHeight
                            - 15, cellWidth + 15, cellHeight + 15, null);
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
                        return new Coordinates(i, j); // почему new нужно?
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
