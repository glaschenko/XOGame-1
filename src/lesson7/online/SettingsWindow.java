package lesson7.online;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class SettingsWindow extends JDialog {
    private static final int WIN_WIDTH = 350;
    private static final int WIN_HEIGHT = 300;

    private static final int MIN_FIELD_SIZE = 3;
    private static final int MAX_FIELD_SIZE = 6;
    private static final int MIN_WIN_LENGTH = 3;




    private final GameWindow gameWindow;
    private final SettingsColorFrame settingsColor;

    private JRadioButton humanVsHuman;
    private JRadioButton humanVsAi;
    private JSlider sliderWinLength;
    private JSlider sliderFieldSize;
    private final JButton butColor;
    private Color colorMap;

    SettingsWindow(GameWindow gameWindow) {
        super(gameWindow, "Enter Your Settings New Game", true);
        this.gameWindow = gameWindow;
        setSize(WIN_WIDTH, WIN_HEIGHT);

        Rectangle gameWindowBounds = gameWindow.getBounds();
        int posX = (int) gameWindowBounds.getCenterX() - WIN_WIDTH / 2;
        int posY = (int) gameWindowBounds.getCenterY() - WIN_HEIGHT / 2;

        setLocation(posX, posY);
        setResizable(false);
        //todo why partly in code partly in consts?

        settingsColor = new SettingsColorFrame(this);

        setLayout(new GridLayout(11, 1));


        initSettingControls();
        //todo naming convention
        fieldSizeAndWinControl();

        butColor = new JButton("Selecting Field Color");
        butColor.addActionListener(e -> settingsColor.setVisible(true));
        add(butColor);

        JButton butStart = new JButton("Start Game");
        //todo naming
        butStart.addActionListener(e -> gameModeControls());
        add(butStart);
    }

    private void initSettingControls() {
        add(new JLabel("Выберите режим игры"));
        humanVsHuman = new JRadioButton("2 PLAYERS");
        humanVsAi = new JRadioButton("1 PLAYER", true);

        ButtonGroup gameMode = new ButtonGroup();
        gameMode.add(humanVsHuman);
        gameMode.add(humanVsAi);
        add(humanVsAi);
        add(humanVsHuman);

    }

    private void fieldSizeAndWinControl() {
        String fieldSizePrefix = new GameWindow().messages.getProperty("fieldSizePrefix");
        String winLengthPrefix = new GameWindow().messages.getProperty("winLengthPrefix");
        JLabel labelFieldSize = new JLabel(fieldSizePrefix + " "+ MIN_FIELD_SIZE);
        JLabel labelWinLength = new JLabel(winLengthPrefix + MIN_WIN_LENGTH);

        sliderFieldSize = new JSlider(MIN_FIELD_SIZE, MAX_FIELD_SIZE, MIN_FIELD_SIZE);
        //todo perhaps can move lower
        sliderFieldSize.addChangeListener(e -> {
            int currentValue = sliderFieldSize.getValue();
            labelFieldSize.setText(fieldSizePrefix + currentValue);
            sliderWinLength.setMaximum(currentValue);
        });

        sliderWinLength = new JSlider(MIN_WIN_LENGTH, MIN_FIELD_SIZE, MIN_FIELD_SIZE);
        sliderWinLength.addChangeListener(e -> labelWinLength.setText(winLengthPrefix + sliderWinLength.getValue()));


        add(new JLabel("Выберите размер поля"));
        add(labelFieldSize);
        add(sliderFieldSize);

        add(new JLabel("Выберите условия победы"));
        add(labelWinLength);
        add(sliderWinLength);

    }

    void colorButton(Color colorId) {
        butColor.setBackground(colorId);
        colorMap = colorId;
    }

    private void gameModeControls() {

        int gameMode;
        //todo use : ? operator
        if (humanVsHuman.isSelected()) {
            gameMode = GameMap.GAME_MODE_HVH;
        } else if (humanVsAi.isSelected()) {
            gameMode = GameMap.GAME_MODE_HVA;
        } else { //todo can never happen, you control it.
            throw new RuntimeException("Неизвестный тип игры");
        }

        int fieldSize = sliderFieldSize.getValue();
        int winLength = sliderWinLength.getValue();

        gameWindow.startNewGame(gameMode, fieldSize, fieldSize, winLength, colorMap);
        setVisible(false);


    }

}
