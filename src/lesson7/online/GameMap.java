package lesson7.online;

import lesson7.online.MCV.controller.TTGameController;
import lesson7.online.MCV.model.GameMode;
import lesson7.online.MCV.model.GameState;
import lesson7.online.MCV.model.PlayerSymbols;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.Objects;

public class GameMap extends JPanel {

    private static final Image CROSS_IMAGE;
    private static final Image ZERO_IMAGE;
    private static final Image HORIZONTAL_IMAGE;
    private static final Image VERTICAL_IMAGE;
    private static final Image DIAGONAL_IMAGE;
    private static final Image REVERSE_DIAGONAL_IMAGE;






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

    GameMap(TTGameController controller) {
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



    private void update(MouseEvent e) {
        if (gameState != GameState.STARTED) return;
        int cellX = e.getX() / cellWidth;
        int cellY = e.getY() / cellHeight;
        System.out.println("X: " + cellX + ", Y:" + cellY); //todo use logger
        if (isValidCell(cellX, cellY) || !isEmptyCell(cellX, cellY)) {
            return;
        }

        makePlayerTurn(cellX, cellY, currentTurn);
        handleTurn();
        if (gameMode == GameMode.HUMAN_VS_AI && gameState != GameState.FINISHED){
            //todo убрал "currentTurn == PlayerSymbols.ZERO" добавил "gameState != GameState.FINISHED" не отрисовывает последний ход и зависает
            makeAITurn();
            handleTurn();
        }
        repaint();
    }



    private void render(Graphics g)  {
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
                String draw = GameWindow.messages.getProperty("draw");
                g.drawString(draw, 150, getHeight() / 2);
            }
            default -> throw new RuntimeException("Непредвиденная ошибка: " + currentStateGameOver);
        }
    }





    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
            render(g);
    }
}
