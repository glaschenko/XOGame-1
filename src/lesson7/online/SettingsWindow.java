package lesson7.online;

import javax.swing.*;
import java.awt.*;

public class SettingsWindow extends JDialog {
    private static final int WIN_WIDTH = 350;
    private static final int WIN_HEIGHT = 300;

    private static final int MIN_FIELD_SIZE = 3;
    private static final int MAX_FIELD_SIZE = 6;
    private static final int MIN_WIN_LENGTH = 3;


    private final GameWindow gameWindow;
    private final SettingsColorFrame settingsColor;

    private JRadioButton humanVsHuman;
    private JSlider sliderWinLength;
    private JSlider sliderFieldSize;
    private final JButton butColor;
    private Color colorMap;

    //todo extract to .properties
    private final String WIN_LENGTH_PREFIX = "Условие победы: ";

    SettingsWindow(GameWindow gameWindow) {
        super(gameWindow, "Enter Your Settings New Game", true);
        this.gameWindow = gameWindow;
        setSize(WIN_WIDTH, WIN_HEIGHT);

        Rectangle gameWindowBounds = gameWindow.getBounds();
        int posX = (int) gameWindowBounds.getCenterX() - WIN_WIDTH / 2;
        int posY = (int) gameWindowBounds.getCenterY() - WIN_HEIGHT / 2;

        setLocation(posX, posY);
        setResizable(false);
        setLayout(new GridLayout(11, 1));

        settingsColor = new SettingsColorFrame(this);
        initSettingControls();
        initFieldSizeAndVictoryControls();

        butColor = new JButton("Selecting Field Color");
        butColor.addActionListener(e -> settingsColor.setVisible(true));
        add(butColor);

        JButton butStart = new JButton("Start Game");
        butStart.addActionListener(e -> handleStartButtonClick());
        add(butStart);
    }

    private void initSettingControls() {
        add(new JLabel("Выберите режим игры"));
        humanVsHuman = new JRadioButton("2 PLAYERS");
        JRadioButton humanVsAi = new JRadioButton("1 PLAYER", true);

        ButtonGroup gameMode = new ButtonGroup();
        gameMode.add(humanVsHuman);
        gameMode.add(humanVsAi);
        add(humanVsAi);
        add(humanVsHuman);
    }

    private void initFieldSizeAndVictoryControls() {
        String fieldSizePrefix = GameWindow.messages.getProperty("fieldSizePrefix");
        JLabel labelFieldSize = new JLabel(fieldSizePrefix + " " + MIN_FIELD_SIZE);
        JLabel labelWinLength = new JLabel(WIN_LENGTH_PREFIX + MIN_WIN_LENGTH);

        sliderFieldSize = new JSlider(MIN_FIELD_SIZE, MAX_FIELD_SIZE, MIN_FIELD_SIZE);
        sliderWinLength = new JSlider(MIN_WIN_LENGTH, MIN_FIELD_SIZE, MIN_FIELD_SIZE);

        sliderFieldSize.addChangeListener(e -> {
            int currentValue = sliderFieldSize.getValue();
            labelFieldSize.setText(fieldSizePrefix + " " + currentValue);
            sliderWinLength.setMaximum(currentValue);
        });
        sliderWinLength.addChangeListener(e -> labelWinLength.setText(WIN_LENGTH_PREFIX + sliderWinLength.getValue()));

        add(new JLabel("Выберите размер поля"));
        add(labelFieldSize);
        add(sliderFieldSize);
        add(new JLabel("Выберите условия победы"));
        add(labelWinLength);
        add(sliderWinLength);
    }

    void setSelectedColor(Color colorId) {
        butColor.setBackground(colorId);
        colorMap = colorId;
    }

    private void handleStartButtonClick() {
        GameMode gameMode = humanVsHuman.isSelected() ? GameMode.HUMAN_VS_HUMAN : GameMode.HUMAN_VS_AI;
        int fieldSize = sliderFieldSize.getValue();
        int winLength = sliderWinLength.getValue();

        gameWindow.startNewGame(gameMode, fieldSize, fieldSize, winLength, colorMap);
        setVisible(false);
    }
}
