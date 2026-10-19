package controller;

public class TurnController {
     private int currentTurn = 1;

    public void startTurn() {
        System.out.println("Turn " + currentTurn + " started!");
    }

    public void nextTurn() {
        currentTurn++;
        System.out.println("Turn " + currentTurn + " started!2");
    }
}
