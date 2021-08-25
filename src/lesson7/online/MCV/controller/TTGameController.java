package lesson7.online.MCV.controller;

import lesson7.online.MCV.model.GameMode;
import lesson7.online.MCV.model.TTGame;
import lesson7.online.MCV.model.TTGameSettings;

public class TTGameController {
    private final TTGame game;

    public TTGameController() {
        game = new TTGame(new TTGameSettings(4,4, GameMode.HUMAN_VS_HUMAN));
    }
}
