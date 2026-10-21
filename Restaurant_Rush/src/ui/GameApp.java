package ui;

import java.util.Scanner;
import controller.GameController;
import entity.GameResult;
import entity.Restaurant;
import staff.WaiterAction;


public class GameApp {
    private Scanner scanner;

    public GameApp(Scanner scanner){
        this.scanner = scanner;
    }
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        GameApp gameApp = new GameApp(scanner);
        GameController gameController = new GameController(gameApp);

        gameController.startGame();

    }

    public void displayTurn(Restaurant restaurant, int currentTurn){
        // displays current turn 
    }

    public WaiterAction getWaiterAction(){
       // get waiteraction
    }

    public int getChefChoice(){
        // choose what chef does
    }

    public int getTargetId(){
        // get ID of customer or order manager wants to act on
    }

    public void displayTurnSummary(Restaurant restaurant, int currentTurn){
        // summarises turn
    }

    public void displayResult(GameResult result, Restaurant restaurant){
        // prints out final result
    }
}
