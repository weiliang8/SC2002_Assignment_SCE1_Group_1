package controller;

import java.util.HashMap;
import java.util.Map;
import entity.CustomerType;
import entity.Restaurant;
import entity.GameResult;
import ui.GameApp;

public class GameController {
    private int currentTurn;
    private static final int MAX_TURNS = 18;
    private Map<Integer, CustomerType> arrivalSchedule;
    private Restaurant restaurant;
    private TurnController turnController;
    private GameApp gameApp;

    public GameController(GameApp gameApp){
        this.currentTurn = 0;
        this.arrivalSchedule = new HashMap<>();
        for(int i = 1; i <= 18; i+=3){
            arrivalSchedule.put(i, CustomerType.REGULAR);
        }
        this.restaurant = new Restaurant();
        this.turnController = new TurnController();
        this.gameApp = gameApp;
    }

    public void startGame(){
        // Start the game
        // rmb to create waiter, chef, and cashier obj
    }
    
    public void runGame(){
        runTurn();
        // runs the whole game
    }

    private void runTurn(){
        // runs turn
    }

    public GameResult checkResult(){
        return GameResult.DEFEAT;
        // returns VICTORY or DEFEAT
    }

    public void endGame(){
        // ends the game
    }
}

