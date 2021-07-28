package lesson7.online;

import javax.swing.*;
import java.awt.*;

public class SettingsColorFrame extends JDialog {
    private static final int FRAME_WIDTH = 350;
    private static final int FRAME_HEIGHT = 300;
    public static final Color colorOption1 = new Color(180, 198, 239);
    private final JButton butOk;
    private final SettingsWindow settingsWindow;

    SettingsColorFrame(SettingsWindow settingsWindow) {
        super(settingsWindow, "Field Color", true);
        this.settingsWindow = settingsWindow;
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        Rectangle settingsWindowBounds = settingsWindow.getBounds();
        int PosX = (int) settingsWindowBounds.getX();
        int PosY = (int) settingsWindowBounds.getY();

        setLocation(PosX, PosY);
        setResizable(false);

        butOk = new JButton("Ok");
        butOk.addActionListener(e -> setVisible(false));
        add(butOk, BorderLayout.SOUTH);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(2, 2));

        //todo better colors
        initColorButton(buttonPanel, Color.WHITE);
        initColorButton(buttonPanel, colorOption1);
        initColorButton(buttonPanel, Color.GREEN);
        initColorButton(buttonPanel, Color.RED);
        add(buttonPanel);
    }

    private void initColorButton(JPanel buttonPanel, Color color){
        JButton button = new JButton();
        button.setBackground(color);
        button.addActionListener(e -> handleColorButtonClick(color));
        buttonPanel.add(button);
    }

    private void handleColorButtonClick(Color colorId) {
        settingsWindow.setSelectedColor(colorId);
        butOk.setBackground(colorId);
    }
}
