package controller;

import entity.Restaurant;
import entity.CustomerType;
import staff.Waiter;
import staff.WaiterAction;
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
        // 1. Display Current State
        displayState(currentTurn);
        // 2. Process Arrivals
        processArrivals(currentTurn, arrival);
        // 3. Waiter phase
        runWaiterPhase(currentTurn);
        //4. Chef phase
        runChefPhase();
        //5. Eating and Payment
        processEating(currentTurn);
        processPayment(currentTurn); 
        //6. Waiting and abandonment
        processAbandonment();
        //7. Display turn results
        displayTurnSummary(currentTurn);
    }

    public void displayState(int currentTurn) {
        // decides when to display the turn, ie calls the gameapp.displayTurn
        gameApp.displayTurn(restaurant, currentTurn);
    }

    public void processArrivals(int currentTurn, CustomerType arrival){
        // process the arrivals(like if this turn then it will arrive la)
        if(arrival != null){
            customerController.processArrival(arrival);
        }
    }

    public void runWaiterPhase(int currentTurn){
        WaiterAction WaiterChoice = gameApp.getWaiterAction();
        switch(WaiterChoice){
            case SEAT -> {waiter.seat(null, currentTurn);} // will add customer arg
            case TAKE_ORDER -> {waiter.takeOrder(null, restaurant.getMenu(), currentTurn);} // will add customer arg
            case SERVE -> {waiter.serve(null, currentTurn);} // will add customer arg
            case WAIT -> {}
        }
    }

    public void runChefPhase(){
        // carries out chef action
        int choice = gameApp.getChefChoice();
        switch(choice){
            case 1 -> {chef.prepareOneUnit(null);} // will add customer's order
            case 2 -> {}
        }
    }

    public void processPayment(int currentTurn){
        // calls cashier to carry out 
        cashier.processPayment(currentTurn);
        
    } 

    public void processEating(int currentTurn){
        // carry on eating
        customerController.processEating(currentTurn);
    }

    public void processAbandonment(){
        customerController.processAbandonment();
    }

    public void displayTurnSummary(int currentTurn){
        // displays summary after turn
        gameApp.displayTurnSummary(restaurant, currentTurn);
    }
}
