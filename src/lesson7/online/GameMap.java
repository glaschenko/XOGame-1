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

    private int turnsCount = 0;

    private WinType currentStateGameOver;
    private final Random random = new Random();

    private GameMode gameMode;
    private int fieldSizeX;
    private int fieldSizeY;
    private int winLength;
    private PlayerSymbols [][] field;
    private int cellWidth;
    private int cellHeight;
    private Coordinates coordinatesBeginningVictoryLine;
    private GameState gameState;


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

    void start(GameMode gameMode, int fieldSizeX, int fieldSizeY, int winLength, Color colorMap) {
        this.gameMode = gameMode;
        this.fieldSizeX = fieldSizeX;
        this.fieldSizeY = fieldSizeY;
        this.winLength = winLength;
        setBackground(colorMap); //todo naming??
        turnsCount = 0;
        field = new PlayerSymbols [fieldSizeX][fieldSizeY];
        gameState = GameState.STARTED;
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

        if (gameMode == GameMode.HUMAN_VS_HUMAN && turnsCount % 2 != 0) {
            playerTurn(cellY, cellX, PlayerSymbols.ZERO);
            turnsCount++;
        } else {
            playerTurn(cellY, cellX, PlayerSymbols.CROSS);
            turnsCount++;
        }
        if (gameMode == GameMode.HUMAN_VS_AI && gameState != GameState.FINISHED) {
            aiTurn();
            turnsCount++;
            repaint();
            WinType winType = checkWin(PlayerSymbols.ZERO);
            if (winType != null) {
                setGameOver(winType);
                return;
            }
            if (isFullMap()) {
                setGameOver(WinType.DRAW);
            }
        }
    }

    private void setGameOver(WinType gameOverState) {
        currentStateGameOver = gameOverState;
        gameState = GameState.FINISHED;
        repaint();
    }
    //todo better name
    private void playerTurn(int cellY, int cellX, PlayerSymbols playerSymbols) {
        field[cellY][cellX] = playerSymbols;
        WinType winType = checkWin(playerSymbols);
        if (winType != null){
            setGameOver(winType);
            return;
        }
        if (isFullMap()) {
            setGameOver(WinType.DRAW);
            return;
        }
        repaint(); //todo I guess we will always need repaint in update().
    }

    private void render(Graphics g) throws IOException {
        if (gameState == GameState.NOT_STARTED) return;
        int width = getWidth();
        int height = getHeight();
        cellWidth = width / fieldSizeX; //todo should be init in constructor if size is static
        cellHeight = height / fieldSizeY;
        //todo extract method
        g.setColor(Color.BLACK);
        for (int i = 1; i < fieldSizeY; i++) {
            int y = i * cellHeight;
            g.drawLine(0, y, width, y);
        }
        for (int i = 1; i < fieldSizeX; i++) {
            int x = i * cellWidth;
            g.drawLine(x, 0, x, height);
        }
        //todo move to constants.
        Image cross = ImageIO.read(Objects.requireNonNull(GameMap.class.getResourceAsStream("resources//cross.png")));
        Image zero = ImageIO.read(Objects.requireNonNull(GameMap.class.getResourceAsStream("resources//zero.png")));
        //todo extract method
        for (int y = 0; y < fieldSizeY; y++) {
            for (int x = 0; x < fieldSizeX; x++) {
                if (isEmptyCell(x, y)) { //todo enum+switch
                    continue;
                }
                if (field[y][x] == PlayerSymbols.CROSS) {
                    g.drawImage(cross, x * cellWidth + 10, y * cellHeight + 10, cellWidth - 20, cellHeight - 20, null);
                } else if (field[y][x] == PlayerSymbols.ZERO) {
                    g.drawImage(zero, x * cellWidth + 10, y * cellHeight + 10, cellWidth - 20, cellHeight - 20, null);
                } else {
                    throw new RuntimeException("Ошибка при отрисовке X: " + x + " Y: " + y);
                }
            }
        }
        if (gameState == GameState.FINISHED) {
            showGameOverState(g);
        }
    }

    private void showGameOverState(Graphics g) throws IOException {
        int startX = coordinatesBeginningVictoryLine.x;
        int startY = coordinatesBeginningVictoryLine.y;
        switch (currentStateGameOver) {
            case HORIZONTAL -> {
                Image HORIZONTAL = ImageIO.read(Objects.requireNonNull(GameMap.class.getResourceAsStream("resources//HORIZONTAL.png")));
                g.drawImage(HORIZONTAL, startX * cellWidth - 30, startY * cellHeight + cellHeight / 2 - cellHeight / 8, cellWidth * winLength + 60, cellHeight / 4, null);
            }
            case VERTICAL -> {
                Image VERTICAL = ImageIO.read(Objects.requireNonNull(GameMap.class.getResourceAsStream("resources//VERTICAL.png")));
                g.drawImage(VERTICAL, startX * cellWidth + cellWidth / 2 - cellWidth / 8, startY * cellHeight - 30, cellWidth / 4, cellHeight * winLength + 60, null);
            }
            case DIAGONAL -> {
                Image DIAGONAL = ImageIO.read(Objects.requireNonNull(GameMap.class.getResourceAsStream("resources//DIAGONAL.png")));
                for (int s = 0; s < winLength; s++) //todo better use brackets.
                    g.drawImage(DIAGONAL, (startX + s) * cellWidth - 15, (startY + s) * cellHeight - 15, cellWidth + 15, cellHeight + 15, null);
            }
            case REVERSE_DIAGONAL -> {
                Image REVERSE_DIAGONAL = ImageIO.read(Objects.requireNonNull(GameMap.class.getResourceAsStream("resources//REVERSE_DIAGONAL.png")));
                for (int s = 0; s < winLength; s++)
                    g.drawImage(REVERSE_DIAGONAL, (startX + s) * cellWidth - 15, (startY - s) * cellHeight - 15, cellWidth + 15, cellHeight + 15, null);
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

    private void aiTurn() {
        //todo methods can be merged in one
        if (turnAIWinCell()) {
            return;
        }
        if (turnHumanWinCell()) {
            return;
        }
        int x;
        int y;
        do { //todo replace with a more efficient algo
            x = random.nextInt(fieldSizeX);
            y = random.nextInt(fieldSizeY);
        } while (!isEmptyCell(x, y));
        field[y][x] = PlayerSymbols.ZERO;
    }

    private boolean turnAIWinCell() {
        for (int i = 0; i < fieldSizeY; i++) {
            for (int j = 0; j < fieldSizeX; j++) {
                if (isEmptyCell(j, i)) {
                    field[i][j] = PlayerSymbols.ZERO;
                    WinType winType = checkWin(PlayerSymbols.ZERO);
                    if (winType != null) {
                        return true;
                    }
                    field[i][j] = null;
                }
            }
        }
        return false;
    }

    private boolean turnHumanWinCell() {
        for (int i = 0; i < fieldSizeY; i++) {
            for (int j = 0; j < fieldSizeX; j++) {
                if (isEmptyCell(j, i)) {
                    field[i][j] = PlayerSymbols.CROSS;
                    WinType winType = checkWin(PlayerSymbols.CROSS);
                    if (winType != null) {
                        field[i][j] = PlayerSymbols.ZERO;
                        return true;
                    }
                    field[i][j] = null;
                }
            }
        }
        return false;
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
