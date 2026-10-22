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
        // - TODO (22/10): Display staff availability in displayTurn()
        // - Waiter: already has hasActedThisTurn(currentTurn)
        // - Chef: add turn tracking + hasActedThisTurn(currentTurn)
        // - Cashier: add turn tracking + hasActedThisTurn(currentTurn)
        // - Show whether each staff member's task is still available this turn 
    }

    public void processArrivals(int currentTurn, CustomerType arrival){
        // process the arrivals(like if this turn then it will arrive la)
        if(arrival != null){
            customerController.processNewCustomer(arrival);
        }
        
    }

    public void runWaiterPhase(int currentTurn){
        WaiterAction WaiterChoice = gameApp.getWaiterAction();
        switch(WaiterChoice){
            case SEAT -> {waiter.seat(restaurant.getfirstWaitingCustomer(), currentTurn);} 
            case TAKE_ORDER -> {waiter.takeOrder(restaurant.getFirstSeatedCustomer(), restaurant.getMenu(), currentTurn);} 
            case SERVE -> {waiter.serve(null, currentTurn);} // will add customer arg(wei liang edit)
            case WAIT -> {}
        }
        // - TODO (22/10): Display staff availability in displayTurn()
        // - Waiter: already has hasActedThisTurn(currentTurn)
        // - Chef: add turn tracking + hasActedThisTurn(currentTurn)
        // - Cashier: add turn tracking + hasActedThisTurn(currentTurn)
        // - Show whether each staff member's task is still available this turn 
    }

    public void runChefPhase(){
        // carries out chef action
        int choice = gameApp.getChefChoice();
        switch(choice){
            case 1 -> {chef.prepareOneUnit(null);} // will add customer's order(will be done later)
            case 2 -> {}
        }
        // - TODO (22/10): Display staff availability in displayTurn()
        // - Waiter: already has hasActedThisTurn(currentTurn)
        // - Chef: add turn tracking + hasActedThisTurn(currentTurn)
        // - Cashier: add turn tracking + hasActedThisTurn(currentTurn)
        // - Show whether each staff member's task is still available this turn 
    }

    public void processPayment(int currentTurn){
        // calls cashier to carry out 
        cashier.processPayment(currentTurn);
        // - TODO (22/10): Display staff availability in displayTurn()
        // - Waiter: already has hasActedThisTurn(currentTurn)
        // - Chef: add turn tracking + hasActedThisTurn(currentTurn)
        // - Cashier: add turn tracking + hasActedThisTurn(currentTurn)
        // - Show whether each staff member's task is still available this turn 
    } 

    public void processEating(int currentTurn){
        // carry on eating
        customerController.processEating(currentTurn);
        // - TODO (22/10): Display staff availability in displayTurn()
        // - Waiter: already has hasActedThisTurn(currentTurn)
        // - Chef: add turn tracking + hasActedThisTurn(currentTurn)
        // - Cashier: add turn tracking + hasActedThisTurn(currentTurn)
        // - Show whether each staff member's task is still available this turn 
    }

    public void processAbandonment(){
        customerController.processAbandonment();
        // - TODO (22/10): Display staff availability in displayTurn()
        // - Waiter: already has hasActedThisTurn(currentTurn)
        // - Chef: add turn tracking + hasActedThisTurn(currentTurn)
        // - Cashier: add turn tracking + hasActedThisTurn(currentTurn)
        // - Show whether each staff member's task is still available this turn 
    }

    public void displayTurnSummary(int currentTurn){
        // displays summary after turn
        gameApp.displayTurnSummary(restaurant, currentTurn);
    }
}
