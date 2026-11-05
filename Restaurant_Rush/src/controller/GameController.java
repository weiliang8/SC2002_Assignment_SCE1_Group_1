package controller;

import java.util.HashMap;
import java.util.Map;
import entity.CustomerType;
import entity.Restaurant;
import entity.RestaurantStatistics;
import staff.Host;
import staff.Waiter;
import staff.Cashier;
import staff.Chef;
import entity.GameResult;
import entity.MenuItem;
import ui.GameApp;

public class GameController {
    private int currentTurn;
    private static final int MAX_TURNS = 18;
    private Map<Integer, CustomerType> arrivalSchedule;
    private Restaurant restaurant;
    private TurnController turnController;
    private GameApp gameApp;
    private String choice;

    public GameController(GameApp gameApp) {
        this.arrivalSchedule = new HashMap<>();
        arrivalSchedule.put(1, CustomerType.REGULAR);
        arrivalSchedule.put(4, CustomerType.VIP);
        arrivalSchedule.put(7, CustomerType.REGULAR);
        arrivalSchedule.put(10, CustomerType.CRITIC);
        arrivalSchedule.put(13, CustomerType.REGULAR);
        arrivalSchedule.put(16, CustomerType.REGULAR);
        this.restaurant = new Restaurant();
        this.gameApp = gameApp;
    }

    private static void printStartGame(Restaurant restaurant) {
        System.out.println("==============================================================================\n");
        System.out.println("--------------------------Welcome to Restaurant Rush--------------------------");
        System.out.println(
                "\n\u001B[4mGOAL\u001B[0m\n\n1: Keep customers \u001B[1mHAPPY\u001B[0m\n2. Serve their orders\n3. Earn enough revenue by the end of the game while maintaing customer satisfaction\n");
        System.out.println("---------------------------------HOW TO PLAY----------------------------------\n");
        System.out.println("1. The game lasts for \u001B[1m18 turns\u001B[0m.");
        System.out.println("2. A new customer arrives at the start of \u001B[1mturns 1, 4, 7, 10, 13 and 16\u001B[0m.");
        System.out.println(
                "3. You have \u001B[4m1 Host\u001B[0m, \u001B[4m1 Waiter\u001B[0m, \u001B[4m1 Chef\u001B[0m and \u001B[4m1 Cashier\u001B[0m, each with limited capacity.");
        System.out.println(
                "4. The host can perform only \u001B[1mONE\u001B[0m task per turn: \n\t\u001B[4m(1)Seat a waiting customer\u001B[0m\n\t\u001B[4m(2)Wait\u001B[0m");
        System.out.println(
                "5. The waiter can perform only \u001B[1mONE\u001B[0m task per turn: \n\t\u001B[4m(1)Seat a waiting customer\u001B[0m\n\t\u001B[4m(2)Take a seated customer's order\u001B[0m\n\t\u001B[4m(3)Serve a ready order\u001B[0m\n\t\u001B[4m(4)Wait\u001B[0m");
        System.out.println("6. The chef can prepare \u001B[1mone\u001B[0m unit of an order per turn, or wait");
        System.out.println(
                "7. The cashier automatically collects payment from eligible customers,\n   handling at most \u001B[1mone\u001B[0m payment per turn");
        System.out.println("8. Customer types:");
        System.out.println("\tREGULAR : loses 8 satisfaction per waiting turn, pays full price");
        System.out.println("\tVIP     : loses 5 satisfaction per waiting turn, gets 10% off");
        System.out.println("\tCRITIC  : loses 12 satisfaction per waiting turn, posts a review when served:\n\t          satisfaction >= 70 -> everyone still waiting +10, below 50 -> everyone still waiting -10\n");
        displayMenu(restaurant);
        System.out.println("---------------------------------HAPPY HOUR-----------------------------------\n");
        System.out.println("1. You may activate \u001B[1mHAPPY HOUR\u001B[0m \u001B[4monce\u001b[0m per game");
        System.out.println("2. Happy Hour lasts for \u001B[4m2 consecutive turns\u001B[0m, including the turn where is activated");
        System.out.println("3. During Happy Hour, all new orders recieve a \u001B[4m20% discount\u001B[0m");
        System.out.println("4. If a customer is a VIP, their VIP discount is applied first, followed by the Happy Hour discount.");
        System.out.println("5. The order price is fixed when the order is placed. Activating or ending Happy Hour does not change existing orders.");
        System.out.println("6. At the start of each Happy Hour turn, customers who are,\n\t\u001B[4m(1) Waiting for a table\u001B[0m \n\t\u001B[4m(2) Waiting to place an order\u001B[0m\n\t\u001B[4m(3) Waiting for their food\u001B[0m \n\nCan recover 15 satisfaction points, up to a maximum of 100 satisfaction points.\n");
        System.out.println("7. Customers who have already departed are not affected.");
        System.out.println("8. The game displays the status of Happy Hour as \u001B[4mHAPPY HOUR: ACTIVE (ROUND X/2)\u001B[0m, where X indicates the current Happy Hour round.\n");
        System.out.println("--------------------------------YOUR OBJECTIVE----------------------------------\n");
        System.out.println("By the end of TURN 18, you must:\n");
        System.out.println("\t- \u001B[4mHave at least 4 paid customers\u001B[0m\n");
        System.out.println("\t- \u001B[4mEarn at least $45.00 in revenue\u001B[0m\n");
        System.out.println(
                "\t- \u001B[4mAchieve an average satisfaction of at least 60 amongst paid customers\u001B[0m\n");
        System.out.println(
                "-Be careful! Customers lose satisfaction while waiting for a seat or food.\n-If their satisfaction reaches zero,they leave without paying.\n");
        System.out.println("\u001B[1mManage your staff wisely and make every turn count!\u001B[0m\n");
        System.out.println("=================================================================================\n");
    }

    public void startGame() {

        // creating waiter
        SeatingController seatingController = new SeatingController(restaurant);
        PricingController pricingController = new PricingController();

        OrderController orderController = new OrderController();

        CustomerController customerController = new CustomerController(restaurant, seatingController);
        // creating Host
        Host host = new Host(seatingController);

        // creating waiter
        Waiter waiter = new Waiter(seatingController, pricingController, orderController, customerController);

        // creating chef
        Chef chef = new Chef(orderController);

        // creating cashier
        Cashier cashier = new Cashier(restaurant, seatingController);

        this.turnController = new TurnController(restaurant, customerController, orderController, host, waiter, chef, cashier,
                gameApp);

        // Start the game
        printStartGame(restaurant);

        System.out.print("Are you ready to play!!! (Press 1 to Start): ");
        this.choice = gameApp.getScanner().nextLine();
        System.out.print("\n");


        if (choice.equals("1")) {
            runGame();
        }

    }

    public void runGame() {
        this.currentTurn = 1;
        for (; currentTurn <= MAX_TURNS; currentTurn++) {
            runTurn(currentTurn);
        }
    }

    private void runTurn(int currentTurn) {
        CustomerType arrival = arrivalSchedule.get(currentTurn);
        turnController.playTurn(currentTurn, arrival);
    }

    public GameResult checkResult() {
        RestaurantStatistics stats = restaurant.getStatistics();
        if (stats.getPaidCustomerCount() < 4 || stats.getRevenueCents() < 4500 || stats.getAverageSatisfaction() < 60) {
            return GameResult.DEFEAT;
        }
        return GameResult.VICTORY;
    }

    public void endGame() {
        // ends the game
        if (choice.equals("1")) {
            gameApp.displayResult(checkResult(), restaurant);
        } else {
            System.out.println("EXITTED GAME");
        }
    }

    public static void  displayMenu(Restaurant restaurant) {

        System.out.println("------------------------------------MENU--------------------------------------\n");

        System.out
                .println("\u001B[4mITEM\u001B[0m\t\t\t\u001B[4mPRICE\u001B[0m\t\t\u001B[4mPREPARATION UNITS\u001B[0m\n");

        for (MenuItem item : restaurant.getMenu().getItems()) {
            if(item.getName().equals("Salad") || item.getName().equals("Burger") || item.getName().equals("Pasta")){
                System.out.printf(
                    "%s\t\t\t$%.2f\t\t%d Prep Unit%s%n",
                    item.getName(),
                    item.getPriceCents() / 100.0,
                    item.getPreparationUnits(),
                    item.getPreparationUnits() == 1 ? "" : "s");
            }
            else{
                System.out.printf(
                        "%s\t\t$%.2f\t\t%d Prep Unit%s%n",
                        item.getName(),
                        item.getPriceCents() / 100.0,
                        item.getPreparationUnits(),
                        item.getPreparationUnits() == 1 ? "" : "s");
            }
        }

        System.out.println();
    }
}
