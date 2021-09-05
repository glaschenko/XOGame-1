package lesson7.online.MVC.veiw;

import lesson7.online.MVC.controller.Controller;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class GameWindow extends JFrame {

    private static final int WIN_WIDTH = 500;
    private static final int WIN_HEIGHT = 550;
    private static final int WIN_POS_X = 450;
    private static final int WIN_HJS_Y = 100;
    public static final Properties messages = new Properties();
    private final SettingsWindow settingsWindow;

    public static void main(String[] args) {
        new GameWindow();
    }

    GameWindow() {
        initMessages();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(WIN_WIDTH, WIN_HEIGHT);
        setLocation(WIN_POS_X, WIN_HJS_Y);
        setTitle("The Game");
        setResizable(false);

        Controller controller = new Controller();
        GameMap gameMap = new GameMap(controller);
        settingsWindow = new SettingsWindow(this, gameMap, controller);
        initializeButtonsPanel();
        add(gameMap);
        setVisible(true);
    }

    private void initMessages() {
        try {
            InputStream in = getClass().getResourceAsStream("resources/messages.properties");
            Reader reader = new InputStreamReader(in, StandardCharsets.UTF_8);
            messages.load(reader);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при загрузке properties: " + e.getMessage(), e);
        }
    }

    private void initializeButtonsPanel() {
        String StartNewGame = GameWindow.messages.getProperty("startNewGame");
        String Exit = GameWindow.messages.getProperty("exit");
        JButton butStartGame = new JButton(StartNewGame);
        butStartGame.addActionListener(e -> settingsWindow.setVisible(true));

        JButton butExitGame = new JButton(Exit);
        butExitGame.addActionListener(e -> System.exit(0));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 2));

        buttonPanel.add(butStartGame);
        buttonPanel.add(butExitGame);
        add(buttonPanel, BorderLayout.SOUTH);
    }
}
