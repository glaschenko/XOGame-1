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
        fieldSizeAndWinControl();
        
        String SelectingFieldColo = GameWindow.messages.getProperty("selectingFieldColo");
        String StartGame = GameWindow.messages.getProperty("startGame");
        butColor = new JButton(SelectingFieldColo);
        butColor.addActionListener(e -> settingsColor.setVisible(true));
        add(butColor);

        JButton butStart = new JButton(StartGame);
        butStart.addActionListener(e -> handleStartButtonClick());
        add(butStart);
    }

    private void initSettingControls() {
        String SelectGameMode = GameWindow.messages.getProperty("selectGameMode");
        String TwoPlayers = GameWindow.messages.getProperty("twoPlayers");
        String OnePlayers = GameWindow.messages.getProperty("onePlayers");
        add(new JLabel(SelectGameMode));
        humanVsHuman = new JRadioButton(TwoPlayers);
        JRadioButton humanVsAi = new JRadioButton(OnePlayers, true);

        ButtonGroup gameMode = new ButtonGroup();
        gameMode.add(humanVsHuman);
        gameMode.add(humanVsAi);
        add(humanVsAi);
        add(humanVsHuman);

    }

    private void fieldSizeAndWinControl() {
        String fieldSizePrefix = GameWindow.messages.getProperty("fieldSizePrefix");
        String winLengthPrefix = GameWindow.messages.getProperty("winLengthPrefix");
        JLabel labelFieldSize = new JLabel(fieldSizePrefix + " " + MIN_FIELD_SIZE);
        JLabel labelWinLength = new JLabel(winLengthPrefix + " " + MIN_WIN_LENGTH);

        sliderFieldSize = new JSlider(MIN_FIELD_SIZE, MAX_FIELD_SIZE, MIN_FIELD_SIZE);
        sliderWinLength = new JSlider(MIN_WIN_LENGTH, MIN_FIELD_SIZE, MIN_FIELD_SIZE);

        sliderFieldSize.addChangeListener(e -> {
            int currentValue = sliderFieldSize.getValue();
            labelFieldSize.setText(fieldSizePrefix + " " + currentValue);
            sliderWinLength.setMaximum(currentValue);
        });

        sliderWinLength.addChangeListener(e -> labelWinLength.setText(winLengthPrefix + " " + sliderWinLength.getValue()));
        String SelectFieldSize = GameWindow.messages.getProperty("selectFieldSize");
        String SelectVictoryConditions = GameWindow.messages.getProperty("selectVictoryConditions");
        add(new JLabel(SelectFieldSize));
        add(labelFieldSize);
        add(sliderFieldSize);
        add(new JLabel(SelectVictoryConditions));
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
