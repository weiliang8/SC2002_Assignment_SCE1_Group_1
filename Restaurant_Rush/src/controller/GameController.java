package controller;

import java.util.HashMap;
import java.util.Map;
import entity.CustomerType;
import entity.Restaurant;
import entity.RestaurantStatistics;
import staff.Waiter;
import staff.Cashier;
import staff.Chef;
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
        this.arrivalSchedule = new HashMap<>();
        for(int i = 1; i <= 18; i+=3){
            arrivalSchedule.put(i, CustomerType.REGULAR);
        }
        this.restaurant = new Restaurant();
        this.gameApp = gameApp;
    }

    private static void printStartGame(){
        System.out.println("--------------------------Welcome to Restaurant Rush--------------------------");
        System.out.println("\n<GOAL>\n1: Keep customers *HAPPY*\n2. serve their orders\n3. Earn enough revenue by the end of the game\n");
        System.out.println("---------------------------------HOW TO PLAY----------------------------------\n");
        System.out.println("1. The game lasts for 18 turns.");
        System.out.println("2. A new customer arrives at the start of turns 1, 4, 7, 10, 13 and 16.");
        System.out.println("3. You have 1 Waiter, 1 Chef and 1 Cashier, each with limited capacity.");
        System.out.println("4. The waiter can perform only ONE task per turn: \n\t(1)Seat a waiting customer\n\t(2)Take a seated customer's order\n\t(3)Serve a ready order\n\t(4)Wait");
        System.out.println("5. The chef can prepare one unit of an order per turn, or wait");
        System.out.println("6. The cashier automatically collects payment from eligible customers,\n   handling at most one payment per turn\n");
        System.out.println("------------------------------------MENU--------------------------------------\n");
        System.out.println("Salad\t\t$9.00\t\tPrep Units: 1 prep unit");
        System.out.println("Burger\t\t$15.00\t\tPrep Units: 1 prep unit");
        System.out.println("Pasta\t\t$18.00\t\tPrep Units: 2 prep Units\n");
        System.out.println("--------------------------------YOUR OBJECTIVE----------------------------------\n");
        System.out.println("By the end of TURN 18, you must:\n");
        System.out.println("\t- Have at least 4 paid customers\n");
        System.out.println("\t- Earn at least $45.00 in revenue\n");
        System.out.println("\t- Achieve an average satisfaction of at least 60 amongst paid customers\n");
        System.out.println("Be careful! Customers lose satisfaction while waiting\nfor a seat or food. If their satisfaction reaches zero,\nthey leave without paying.\n");
        System.out.println("Manage your staff wisely and make every turn count!\n");
        System.out.println("-------------------------------------------------------------------------------");
    }

    public void startGame(){
        // Start the game
        printStartGame();
        //creating waiter
        SeatingController seatingController = new SeatingController(restaurant);
        PricingController pricingController = new PricingController();
        
        OrderController orderController = new OrderController();
        
        CustomerController customerController = new CustomerController(restaurant, seatingController);
        
        //creating waiter 
        Waiter waiter = new Waiter(seatingController, pricingController,orderController,customerController);

        //creating chef
        Chef chef = new Chef(orderController);

        //creating cashier
        Cashier cashier = new Cashier(restaurant, seatingController);

        this.turnController = new TurnController(restaurant, customerController, orderController,waiter, chef, cashier,gameApp);
        runGame();

        
    }
    
    public void runGame(){
        this.currentTurn = 1;
        for(; currentTurn <= MAX_TURNS; currentTurn++){
            runTurn(currentTurn);
        }
    }

    private void runTurn(int currentTurn){
        CustomerType arrival = arrivalSchedule.get(currentTurn); 
        turnController.playTurn(currentTurn, arrival);
    }

    public GameResult checkResult(){
        RestaurantStatistics stats = restaurant.getStatistics();
        if(stats.getPaidCustomerCount() < 4 || stats.getRevenueCents() < 4500 || stats.getAverageSatisfaction() < 60){
            return GameResult.DEFEAT;
        }
        return GameResult.VICTORY;
    }

    public void endGame(){
        // ends the game
        gameApp.displayResult(checkResult(), restaurant);
    }
}

