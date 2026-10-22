package ui;

import java.util.List;
import java.util.Scanner;
import controller.GameController;
import entity.GameResult;
import entity.Restaurant;
import entity.RestaurantStatistics;
import entity.Table;
import staff.WaiterAction;
import entity.Customer;


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
        gameController.endGame();
        scanner.close();

    }

    public void displayTurn(Restaurant restaurant, int currentTurn){
        // displays current turn
        RestaurantStatistics stats = restaurant.getStatistics();
        System.out.printf("----------------------------Turn %d----------------------------\n", currentTurn);
        //Print revenue right now
        int revenue = stats.getRevenueCents();
        System.out.printf("Your revenue as of now is : $ %d\n", revenue);
        //Print average satisfaction (or N/A)
        double avg_satisfaction = stats.getAverageSatisfaction();
        System.out.printf("Your average satisfaction is %.2f\n", avg_satisfaction);

        //Add getter for waiting queue

        //Print Occupied Tables
        List<Table> tables = restaurant.getTables();
        for (Table table : tables) {
            if (table.isFree()) {
                System.out.println("Table " + table.getId() + ": Empty");
            } else {
                System.out.println("Table " + table.getId()
                                    + ": Customer " + table.getOccupant().getCustomerId());
            }
        }   

        // Each customer's satisfaction
        List<Customer> customers = restaurant.getCustomers();
        for (Customer customer : customers){
            System.out.printf("Customer %d's satisfaction scores: %d\n", customer.getCustomerId(), customer.getSatisfaction());
        }

        // - TODO (22/10): Display staff availability in displayTurn()
        // - Waiter: already has hasActedThisTurn(currentTurn)
        // - Chef: add turn tracking + hasActedThisTurn(currentTurn)
        // - Cashier: add turn tracking + hasActedThisTurn(currentTurn)
        // - Show whether each staff member's task is still available this turn 
    }

    public WaiterAction getWaiterAction(){
       // get waiteraction
       while(true){ 
       System.out.println("What would you like the waiter to do?\n");
       System.out.println("1: Seat a customer in the queue");
       System.out.println("2. Take an order from customer");
       System.out.println("3. Serve food to customer");
       System.out.println("4. Wait\n");
    
       System.out.print("Enter your choice (1-4): ");
       int choice = scanner.nextInt();
       
       switch(choice){
           case 1 -> {return WaiterAction.SEAT;}
           case 2 -> {return WaiterAction.TAKE_ORDER;}
           case 3 -> {return WaiterAction.SERVE;}
           case 4 -> {return WaiterAction.WAIT;}
           default -> System.out.println("Invalid Choice. Try again.");
            }
        }
    }

    public int getChefChoice(){
        // choose what chef does
        while(true){ 
       System.out.println("What would you like the chef to do?\n");
       System.out.println("1: Prepare one unit of an order");
       System.out.println("2. Wait\n");
    
       System.out.print("Enter your choice (1-2): ");
       int choice = scanner.nextInt();
       
       switch(choice){
           case 1 -> {return choice;}
           case 2 -> {return choice;}
           default -> System.out.println("Invalid Choice. Try again.");
            }
        }

    }
    /* 
    public int getTargetId(){
        // get ID of customer or order manager wants to act on
    }
    

    public void displayError(String message){
        // display error i guess(if not needed will remove)
    }
    */

    public void displayTurnSummary(Restaurant restaurant, int currentTurn){
        // summarises turn
        RestaurantStatistics stats = restaurant.getStatistics();
        System.out.println("----------------------------Turn Summary----------------------------");
        //1. Print served count
        int serve_count = stats.getServedCustomerCount();
        System.out.printf("You have served %d customers.\n", serve_count);
        //2. Print unhappy departures
        int unhappy_departures = stats.getUnhappyDepartureCustomerCount();
        System.out.printf("You had %d unhappy customers as of latest turn.\n", unhappy_departures);
        //3. Print revenue
        int revenue = stats.getRevenueCents();
        System.out.printf("Your revenue as of now is : $ %d\n", revenue);
        //4. Print average satisfaction (or N/A)
        double avg_satisfaction = stats.getAverageSatisfaction();
        System.out.printf("Your average satisfaction is %.2f\n", avg_satisfaction);
        //5. Print turns used
        System.out.printf("You have used up %d turns.\n", currentTurn);
    }

    public void displayResult(GameResult result, Restaurant restaurant){
        // prints out final result
        System.out.println("----------------------------GAME OVER----------------------------");
        if(result == GameResult.VICTORY){
            System.out.println("Congratulations!!! You have successfully ran the restaurant!!!\n\n");
        }
        else{
            System.out.println("Sorry you have not successfully succedded running this restaurant.\nBetter luck next time!\n\n");
        }
        System.out.printf("Turns played: %d\nCustomers paid: %d\nTotal revenue: $%d\nAverage Satisfaction: %.2f\n\n");
        System.out.println("-----------------------------------------------------------------\n");
    }
}
