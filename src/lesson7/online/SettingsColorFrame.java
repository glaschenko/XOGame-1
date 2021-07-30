package lesson7.online;

import javax.swing.*;
import java.awt.*;

public class SettingsColorFrame extends JFrame {

    //todo naming
    private static final int COLOR_WIDTH = 350;
    private static final int COLOR_HEIGHT = 300;

    SettingsColorFrame(SettingsWindow settingsWindow) {
        setSize(COLOR_WIDTH, COLOR_HEIGHT);


        Rectangle settingsWindowBounds = settingsWindow.getBounds();
        int PosX = (int) settingsWindowBounds.getX();
        int PosY = (int) settingsWindowBounds.getY();

        setLocation(PosX, PosY);
        setResizable(false);
        setTitle("Field Color"); //todo some names in En, some in Ru


        JButton butOk = new JButton("Ok");
        butOk.addActionListener(e -> setVisible(false));
        add(butOk, BorderLayout.SOUTH);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(2, 2));

        //todo better colors, extract method
        JButton butWhite = new JButton();
        butWhite.setBackground(Color.WHITE);
        butWhite.addActionListener(e -> changeButtonColor(settingsWindow, butOk, Color.WHITE));

        JButton butBlue = new JButton();
        butBlue.setBackground(Color.BLUE);
        butBlue.addActionListener(e -> changeButtonColor(settingsWindow, butOk, Color.BLUE));

        JButton butGreen = new JButton();
        butGreen.setBackground(Color.GREEN);
        butGreen.addActionListener(e -> changeButtonColor(settingsWindow, butOk, Color.GREEN));

        JButton butRed = new JButton();
        butRed.setBackground(Color.RED);
        butRed.addActionListener(e -> changeButtonColor(settingsWindow, butOk, Color.RED));

        buttonPanel.add(butWhite);
        buttonPanel.add(butBlue);
        buttonPanel.add(butGreen);
        buttonPanel.add(butRed);

        add(buttonPanel);


    }

    private void changeButtonColor(SettingsWindow settingsWindow, Component butOk, Color colorId) {
        settingsWindow.setSelectedColor(colorId);
        butOk.setBackground(colorId);
    }


}
