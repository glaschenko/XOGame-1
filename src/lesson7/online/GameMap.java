package lesson7.online;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.Objects;
import java.util.Random;

import static lesson7.online.GameState.FINISHED;

public class GameMap extends JPanel {
    public int turnsCount = 0;
    public final Random RANDOM = new Random();

    private WinType currentStateGameOver;

    private GameMode gameMode;
    private int fieldSizeX;
    private int fieldSizeY;
    private int winLength;
    private FieldStatus[][] field;
    private int cellWidth;
    private int cellHeight;
    private Coordinate winLineStart;
    private GameState gameState;

    static class Coordinate{
        int x;
        int y;

        public Coordinate(int x, int y) {
            this.x = x;
            this.y = y;
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

    void start(GameMode gameMode, int fieldSizeX, int fieldSizeY, int winLength, Color colorMap) {
        this.gameMode = gameMode;
        this.fieldSizeX = fieldSizeX;
        this.fieldSizeY = fieldSizeY;
        this.winLength = winLength;
        setBackground(colorMap); //todo naming??
        turnsCount = 0;
        field = new FieldStatus[fieldSizeX][fieldSizeY];
        gameState = GameState.STARTED;
        repaint();
    }

    private void update(MouseEvent e) {
        if (gameState != GameState.STARTED) return;
        int cellX = e.getX() / cellWidth;
        int cellY = e.getY() / cellHeight;
        System.out.println("X: " + cellX + ", Y:" + cellY); //todo use logger
        if (isInvalidCell(cellX, cellY) || !isEmptyCell(cellX, cellY)) {
            return;
        }
        //todo refactor ifs and use ternary operator (see original version commented out)
        boolean evenTurn = turnsCount % 2 == 0;
        if ( (gameMode == GameMode.HUMAN_VS_HUMAN) || evenTurn) {
            player(cellY, cellX, evenTurn ? FieldStatus.CROSS : FieldStatus.ZERO);
        }
        if(gameMode == GameMode.HUMAN_VS_AI){
            aiTurn();
            WinType winType = checkWin(FieldStatus.ZERO);
            if (winType != null) {
                setGameOver(winType);
                return; //todo replace with "else" below
            }
            if (isFullMap()) {
                setGameOver(WinType.DRAW);
            }
        }
        repaint();

//        if (gameMode == GAME_MODE_HVH && turnsCount % 2 == 0) {
//            player(cellY, cellX, AI_DOT);
//        } else {
//            player(cellY, cellX, HUMAN_DOT);
//        }
//        if (gameMode == GAME_MODE_HVA && !isGameOver) {
//            aiTurn();
//            repaint();
//            if (checkWin(AI_DOT)) {
//                setGameOver(stateWin);
//                return;
//            }
//            if (isFullMap()) {
//                setGameOver(STATE_DRAW);
//            }
//        }
    }

    private void setGameOver(WinType gameOverState) {
        currentStateGameOver = gameOverState;
        gameState = FINISHED;
        repaint();
    }
    //todo better name
    private void player(int cellY, int cellX, FieldStatus fieldStatus) {
        turnsCount++;
        field[cellY][cellX] = fieldStatus;
        WinType winType = checkWin(fieldStatus);
        if (winType != null) {
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
        if (gameState != GameState.STARTED) return;
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
                if (field[y][x] == FieldStatus.CROSS) {
                    g.drawImage(cross, x * cellWidth + 10, y * cellHeight + 10, cellWidth - 20, cellHeight - 20, null);
                } else if (field[y][x] == FieldStatus.ZERO) {
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
        int startX = winLineStart.x;
        int startY = winLineStart.y;
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
        turnsCount++;
        if (turnAIWinCell()) {
            return;
        }
        if (turnHumanWinCell()) {
            return;
        }
        int x;
        int y;
        do { //todo replace with a more efficient algo
            x = RANDOM.nextInt(fieldSizeX);
            y = RANDOM.nextInt(fieldSizeY);
        } while (!isEmptyCell(x, y));
        field[y][x] = FieldStatus.ZERO;
    }

    private boolean turnAIWinCell() {
        for (int i = 0; i < fieldSizeY; i++) {
            for (int j = 0; j < fieldSizeX; j++) {
                if (isEmptyCell(j, i)) {
                    field[i][j] = FieldStatus.ZERO;
                    if (checkWin(FieldStatus.ZERO) != null) {
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
                    field[i][j] = FieldStatus.CROSS;
                    if (checkWin(FieldStatus.CROSS) != null) {
                        field[i][j] = FieldStatus.ZERO;
                        return true;
                    }
                    field[i][j] = null;
                }
            }
        }
        return false;
    }

    private WinType checkWin(FieldStatus fieldStatus) {
        //todo better use hor/ver instead of x/y
        for (int i = 0; i < fieldSizeX; i++) {
            for (int j = 0; j < fieldSizeY; j++) {
                if (checkLine(i, j, 1, 0, winLength, fieldStatus)) {
                    winLineStart = new Coordinate(i, j);
                    return WinType.HORIZONTAL;
                }
                if (checkLine(i, j, 0, 1, winLength, fieldStatus)) {
                    winLineStart = new Coordinate(i, j);
                    return WinType.VERTICAL;
                }
                if (checkLine(i, j, 1, 1, winLength, fieldStatus)) {
                    winLineStart = new Coordinate(i, j);
                    return WinType.DIAGONAL;
                }
                if (checkLine(i, j, 1, -1, winLength, fieldStatus)) {
                    winLineStart = new Coordinate(i, j);
                    return WinType.REVERSE_DIAGONAL;
                }
            }
        }
        return null;
    }


    //todo better parameters and method name
    private boolean checkLine(int x, int y, int vx, int vy, int len, FieldStatus fieldStatus) {
        final int farX = x + (len - 1) * vx;
        final int farY = y + (len - 1) * vy;
        if (isInvalidCell(farX, farY)) {
            return false;
        }
        for (int i = 0; i < len; i++) {
            if (field[y + i * vy][x + i * vx] != fieldStatus) {
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

    private boolean isInvalidCell(int x, int y) {
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
