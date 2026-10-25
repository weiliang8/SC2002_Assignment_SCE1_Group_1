package ui;

import java.util.List;
import java.util.Scanner;
import controller.GameController;
import entity.GameResult;
import entity.Order;
import entity.Restaurant;
import entity.RestaurantStatistics;
import entity.Table;
import staff.WaiterAction;
import entity.Customer;
import entity.CustomerStatus;

public class GameApp {
    private Scanner scanner;

    public GameApp(Scanner scanner) {
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

    public Scanner getScanner() {
        return scanner;
    }

    public void displayTurn(Restaurant restaurant, int currentTurn) {
        // displays current turn
        RestaurantStatistics stats = restaurant.getStatistics();
        System.out.printf("-------------------------------Turn %d--------------------------------\n", currentTurn);
        // Print revenue right now
        int revenue = stats.getRevenueCents();
        float revenue_in_dollars = revenue / 100;
        System.out.printf("Your revenue as of now is : $%.2f\n", revenue_in_dollars);
        // Print average satisfaction (or N/A)
        double avg_satisfaction = stats.getAverageSatisfaction();

        if (stats.getPaidCustomerCount() == 0) {
            System.out.printf("Your average satisfaction among paid customers are N/A\n\n");
        } else {
            System.out.printf("Your average satisfaction among paid customers are %.2f\n\n", avg_satisfaction);
        }

        System.out.println("--- Waiting Queue ---");
        for (Customer customer : restaurant.getCustomers()) {
            if (customer.getStatus() == CustomerStatus.WAITING) {
                System.out.println("Customer " + customer.getCustomerId());
            }
        }

        System.out.println("\n--- Overall Customer and Order Status ---");

        for (Customer customer : restaurant.getCustomers()) {
            String orderStatus = customer.getOrder() == null ? "NO ORDER" : customer.getOrder().getStatus().toString();
            System.out.printf(
                    "Customer %d : {Customer ID: %d | Customer Type: %s | Customer Status: %s | Satisfaction: %d | Order Status: %s}%n",
                    customer.getCustomerId(),
                    customer.getCustomerId(),
                    customer.getCustomerType().toString(),
                    customer.getStatus(),
                    customer.getSatisfaction(),
                    orderStatus);
        }

        System.out.println("\n---Table Availability---");

        // Print Occupied Tables
        List<Table> tables = restaurant.getTables();
        for (Table table : tables) {
            if (table.isFree()) {
                System.out.println("Table " + table.getId() + ": Empty");
            } else {
                System.out.println("Table " + table.getId()
                        + ": Customer " + table.getOccupant().getCustomerId());
            }
        }

        System.out.print("\n");
    }

    // Each customer's satisfaction ==> Venka: Already in customer n order status, i
    // feel like dn, if needed can uncomment (Project doc say need this part though)
    /*
     * List<Customer> customers = restaurant.getCustomers();
     * for (Customer customer : customers) {
     * System.out.printf("Customer %d's satisfaction scores: %d\n",
     * customer.getCustomerId(),
     * customer.getSatisfaction());
     * }
     * System.out.print("\n");
     * }
     * 
     * TODO: Stil need
     * "What are the staff tasks avaliable according to proj requirements"
     */

    public WaiterAction getWaiterAction() {
        // get waiteraction
        while (true) {
            System.out.println("\n\u001b[4mWhat would you like the waiter to do?\u001b[0m");
            System.out.println("1. Seat a customer in the queue");
            System.out.println("2. Take an order from customer");
            System.out.println("3. Serve food to customer");
            System.out.println("4. Wait\n");

            int choice = readInt("Enter your choice (1-4): ");

            switch (choice) {
                case 1 -> {
                    return WaiterAction.SEAT;
                }
                case 2 -> {
                    return WaiterAction.TAKE_ORDER;
                }
                case 3 -> {
                    return WaiterAction.SERVE;
                }
                case 4 -> {
                    return WaiterAction.WAIT;
                }
                default -> System.out.println("Invalid Choice. Try again.");
            }
        }

    }

    public int getWaiterSeatCustomerId(Restaurant restaurant) {

        System.out.println("\n--- Waiting Customers ---");

        boolean hasWaitingCustomer = false;

        for (Customer customer : restaurant.getCustomers()) {

            if (customer.getStatus() == CustomerStatus.WAITING) {

                System.out.println(
                        "Customer " + customer.getCustomerId() + ": {ID: " + customer.getCustomerId()
                                + " | Customer Type: " + customer.getCustomerType()
                                + " | Satisfaction: "
                                + customer.getSatisfaction() + "}");

                hasWaitingCustomer = true;
            }
        }
        System.out.println();

        if (!hasWaitingCustomer) {
            System.out.println("No customers waiting. Choose another option.");
            return -1;
        }

        System.out.print("Enter Customer ID to seat: ");

        while (!scanner.hasNextInt()) {
            System.out.println("Please enter a valid number.");
            scanner.next();
            System.out.print("Enter Customer ID to seat: ");
        }

        int Id = scanner.nextInt();
        scanner.nextLine();
        if (Id == -1) {
            return -2;
        }
        return Id;
    }

    public int getTakeOrderCustomerId(Restaurant restaurant) {

        System.out.println("\n--- Seated Customers Available to Order ---");

        boolean found = false;

        for (Customer customer : restaurant.getCustomers()) {

            if (customer.getStatus() == CustomerStatus.SEATED
                    && customer.getOrder() == null) {

                System.out.println(
                        "Customer " + customer.getCustomerId() + ": {Customer ID: " + customer.getCustomerId()
                                + " | Customer Type: " + customer.getCustomerType()
                                + " | Satisfaction: "
                                + customer.getSatisfaction() + "}");

                found = true;
            }
        }
        System.out.println();

        if (!found) {
            System.out.println("No customers available to order. Please choose another option.");
            return -1;
        }

        System.out.print("Enter Customer ID to take order: ");

        while (!scanner.hasNextInt()) {
            System.out.println("Please enter a valid number.");
            scanner.next();
            System.out.print("Enter Customer ID to take order: ");
        }

        int Id = scanner.nextInt();
        scanner.nextLine();
        if (Id == -1) {
            return -2;
        }
        return Id;

    }

    public int getServeCustomerId(Restaurant restaurant) {

        System.out.println("\n--- Customers Ready to Serve ---");

        boolean found = false;

        for (Customer customer : restaurant.getCustomers()) {

            Order order = customer.getOrder();

            if (customer.getStatus() == CustomerStatus.SEATED
                    && order != null
                    && order.isReady()) {

                System.out.println(
                        "Customer " + customer.getCustomerId() + ": {Customer ID: " + customer.getCustomerId()
                                + " | Customer Type: " + customer.getCustomerType()
                                + " | Order ID: " + order.getOrderId()
                                + " | Food Item: " + order.getItem().getName()
                                + " | Order Status: READY}");

                found = true;
            }
        }
        System.out.println();

        if (!found) {
            System.out.println("No READY orders to serve. Please choose another option.");
            return -1;
        }

        int Id = readInt("Enter Customer ID to serve: ");
        scanner.nextLine();
        if (Id == -1) {
            return -2;
        }
        return Id;
    }

    public int getChefChoice() {
        // choose what chef does
        while (true) {
            System.out.println("\n\u001B[4mWhat would you like the chef to do?\u001B[0m");
            System.out.println("1. Prepare one unit of an order");
            System.out.println("2. Wait\n");

            int choice = readInt("Enter your choice (1-2): ");

            switch (choice) {
                case 1 -> {
                    return choice;
                }
                case 2 -> {
                    return choice;
                }
                default -> System.out.println("Invalid Choice. Try again.");
            }
        }

    }

    public int getChefOrderId(List<Order> pendingOrders) {

        System.out.println("\n--- PENDING ORDERS ---");

        if (pendingOrders.isEmpty()) {
            System.out.println("No pending orders to prepare. Chef can only wait.");
            return -1;
        }

        // Display available orders
        for (Order order : pendingOrders) {
            System.out.printf(
                    "{Order ID: %d | Customer ID: %d | FOOD ITEM: %s | PROGRESS: %d/%d units}%n",
                    order.getOrderId(),
                    order.getCustomerId(),
                    order.getItem().getName(),
                    order.getUnitsCompleted(),
                    order.getUnitsTotal());
        }
        System.out.println("-----------------------");
        System.out.print("Enter Order ID to prepare: ");

        while (!scanner.hasNextInt()) {
            System.out.println("Please enter a valid number.");
            scanner.next();
            System.out.print("Enter Order ID to prepare: ");
        }

        int Id = scanner.nextInt();
        scanner.nextLine();
        if (Id == -1) {
            return -2;
        }
        return Id;
    }

    /*
     * public int getTargetId(){
     * // get ID of customer or order manager wants to act on
     * }
     * 
     * 
     * public void displayError(String message){
     * // display error i guess(if not needed will remove)
     * }
     */

    public void displayTurnSummary(Restaurant restaurant, int currentTurn) {
        // summarises turn
        RestaurantStatistics stats = restaurant.getStatistics();
        System.out.println("----------------------------Turn Summary----------------------------");
        // 1. Print served count
        int serve_count = stats.getServedCustomerCount();
        System.out.printf("You have served \u001B[4m%d customers.\u001B[0m\n", serve_count);
        // 2. Print unhappy departures
        int unhappy_departures = stats.getUnhappyDepartureCustomerCount();
        System.out.printf("You had \u001B[4m%d unhappy customers\u001B[0m as of latest turn.\n", unhappy_departures);
        // 3. Print revenue
        int revenue = stats.getRevenueCents();
        float revenue_in_dollars = revenue / 100;
        System.out.printf("Your revenue as of now is : \u001B[4m$%.2f\u001B[0m\n", revenue_in_dollars);
        // 4. Print average satisfaction (or N/A)
        double avg_satisfaction = stats.getAverageSatisfaction();
        System.out.printf(
                "Your average satisfaction among paid customers is \u001B[4m%s\u001B[0m%n",
                avg_satisfaction == -1 ? "N/A" : String.format("%.2f", avg_satisfaction)); // 5. Print turns used
        System.out.printf("You have used up \u001B[4m%d turns\u001B[0m.\n", currentTurn);
        System.out.println("--------------------------------------------------------------------\n");
    }

    public void displayStaffAvailability(boolean waiterAvailable, boolean chefAvailable, boolean cashierAvailable) {

        System.out.println("--- STAFF AVAILABILITY ---");
        System.out.println("Waiter  : " + (waiterAvailable ? "AVAILABLE" : "DONE"));
        System.out.println("Chef    : " + (chefAvailable ? "AVAILABLE" : "DONE"));
        System.out.println("Cashier : " + (cashierAvailable ? "AVAILABLE" : "DONE"));
    }

    public void displayResult(GameResult result, Restaurant restaurant) {
        // prints out final result
        System.out.println("----------------------------GAME OVER----------------------------");
        if (result == GameResult.VICTORY) {
            System.out.println("Congratulations!!! You have successfully ran the restaurant!!!\n\n");
        } else {
            System.out.println(
                    "Sorry you have not succedded running this restaurant.\nBetter luck next time!\n\n");
        }
        RestaurantStatistics stats = restaurant.getStatistics();

        System.out.printf("Turns played: %d%n", 18);
        System.out.printf("Customers served: %d%n", stats.getServedCustomerCount());
        System.out.printf("Customers paid: %d%n", stats.getPaidCustomerCount());
        System.out.printf("Unhappy departures: %d%n", stats.getUnhappyDepartureCustomerCount());
        System.out.printf("Total revenue: $%.2f%n", stats.getRevenueCents() / 100.0);

        if (stats.getPaidCustomerCount() == 0) {
            System.out.println("Average satisfaction: N/A");
        } else {
            System.out.printf("Average satisfaction: %.2f%n", stats.getAverageSatisfaction());
        }
        System.out.println("-----------------------------------------------------------------\n");
    }

    private int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);

            if (scanner.hasNextInt()) {
                return scanner.nextInt();
            }

            System.out.println("Invalid input. Please enter a number.");
            scanner.next(); // Discard invalid input
        }
    }

    
}
