package controller;

import entity.Restaurant;
import entity.CustomerType;
import staff.Waiter;
import staff.Chef;
import staff.Cashier;
import ui.GameApp;

public class TurnController {
    //private int currentTurn = ;
    private Restaurant restaurant;
    private CustomerController customerController;
    private Waiter waiter;
    private Chef chef;
    private Cashier cashier;
    private GameApp gameApp;

    public TurnController(Restaurant restaurant, 
                          CustomerController customerController,
                          Waiter waiter, Chef chef, Cashier cashier,
                          GameApp gameApp)
    {
        this.restaurant = restaurant;
        this.customerController = customerController;
        this.waiter = waiter;
        this.chef = chef;
        this.cashier = cashier;
        this.gameApp = gameApp;
    }

    public void playTurn(int currentTurn, CustomerType arrival) {
        System.out.println("Turn " + currentTurn + " started!");
        // this or that
        currentTurn++;
        System.out.println("Turn " + currentTurn + " started!2");
    }

    public void displayState() {
        // display state at start of each turn
    }

    public void processArrivals(){
        // process the arrivals(like if this turn then it will arrive la)
    }

    public void runWaiterPhase(){
        // carries out waiter action
    }

    public void runChefPhase(){
        // carries out chef action
    }

    public void processPayment(int currentTurn){
        // calls cashier to carry out 
    } 

    public void processEating(int currentTurn){
        // carry on eating
    }

    public void processAbandonment(){
        // customer leaves without paying
    }

    public void displayTurnSummary(){
        // displays summary after turn
    }
}
