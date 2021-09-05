package lesson7.online.MVC.veiw;

import javax.swing.*;
import java.awt.*;

public class SettingsColorFrame extends JDialog {

    private static final int FRAME_WIDTH = 350;
    private static final int FRAME_HEIGHT = 300;
    private static final Color colorOptionW = new Color(255, 255, 255);
    private static final Color colorOptionB = new Color(81, 81, 255);
    private static final Color colorOptionG = new Color(0, 145, 72);
    private static final Color colorOptionR = new Color(255, 62, 62);
    private final JButton butOk;
    private final SettingsWindow settingsWindow;

    SettingsColorFrame(SettingsWindow settingsWindow) {
        super(settingsWindow,"Field Color", true);
        this.settingsWindow = settingsWindow;
        setSize(FRAME_WIDTH, FRAME_HEIGHT);

        Rectangle settingsWindowBounds = settingsWindow.getBounds();
        int posX = (int) settingsWindowBounds.getX();
        int posY = (int) settingsWindowBounds.getY();
        setLocation(posX, posY);
        setResizable(false);

        String Ok = GameWindow.messages.getProperty("ok");
        butOk = new JButton(Ok);
        butOk.addActionListener(e -> setVisible(false));
        add(butOk, BorderLayout.SOUTH);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(2, 2));
        initColorButton(buttonPanel, colorOptionW);
        initColorButton(buttonPanel, colorOptionB);
        initColorButton(buttonPanel, colorOptionG);
        initColorButton(buttonPanel, colorOptionR);
        add(buttonPanel);
    }

    private void initColorButton(JPanel buttonPanel, Color color){
        JButton button = new JButton();
        button.setBackground(color);
        button.addActionListener(e -> changeButtonColor(color));
        buttonPanel.add(button);
    }

    private void changeButtonColor(Color colorId) {
        settingsWindow.setSelectedColor(colorId);
        butOk.setBackground(colorId);
    }
}
