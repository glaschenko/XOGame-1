package lesson7.online.MVC.veiw;

import lesson7.online.MVC.controller.Controller;
import lesson7.online.MVC.model.Coordinates;
import lesson7.online.MVC.model.GameState;
import lesson7.online.MVC.model.PlayerSymbols;
import lesson7.online.MVC.model.TTGame;

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
    private final GameState gameState;

    private final TTGame game;


    private int cellWidth;
    private int cellHeight;




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

    private final Controller controller;


    GameMap(Controller controller) {
        this.controller = controller;
        game = controller.getGame();
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

        int cellX = e.getX() / cellWidth;
        int cellY = e.getY() / cellHeight;
        controller.handleClick(cellX, cellY);
    }

    public void accord(){
        repaint();
    }

    private void render(Graphics g) throws IOException {
        if (gameState == GameState.NOT_STARTED) return;
        int width = getWidth();
        int height = getHeight();
        cellWidth = width / game.getSettings().getFieldSizeX();
        cellHeight = height / game.getSettings().getFieldSizeY();

        dragGrid(g, width, height);
        dragPlayerSymbols(g);
        if (gameState == GameState.FINISHED) {
            showGameOverState(g);
        }
    }

    private void dragPlayerSymbols(Graphics g){
        for (int y = 0; y < game.getSettings().getFieldSizeX(); y++) {
            for (int x = 0; x < game.getSettings().getFieldSizeY(); x++) {
                if (!game.isEmptyCell(x, y)) {
                    Image images = game.getCellContent(x, y) == PlayerSymbols.CROSS ? CROSS_IMAGE : ZERO_IMAGE;
                    g.drawImage(images, x * cellWidth + 10, y * cellHeight + 10, cellWidth - 20,
                            cellHeight - 20, null);
                }
            }
        }
    }

    private void dragGrid(Graphics g, int width, int height) {
        g.setColor(Color.BLACK);
        for (int i = 1; i < game.getSettings().getFieldSizeY(); i++) {
            int y = i * cellHeight;
            g.drawLine(0, y, width, y);
        }
        for (int i = 1; i < game.getSettings().getFieldSizeX(); i++) {
            int x = i * cellWidth;
            g.drawLine(x, 0, x, height);
        }
    }

    private void showGameOverState(Graphics g){
        Coordinates start = game.getCoordinatesBeginningVictoryLine();
        int startX = start.x;
        int startY = start.y;
        int winLength = game.getSettings().getWinLength();
        switch (game.getCurrentStateGameOver()) {
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
            default -> throw new RuntimeException("Непредвиденная ошибка: " + game.getCurrentStateGameOver());
        }
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
