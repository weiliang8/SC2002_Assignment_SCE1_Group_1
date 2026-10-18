package ui;

import controller.TurnController;

public class GameApp {
    public static void main(String[] args) throws Exception {
        System.out.println("Hello, World!2");

        TurnController turnController = new TurnController();

        turnController.startTurn();
        turnController.nextTurn();
    }
}
