package ui;

import java.util.List;
import java.util.Scanner;
import controller.GameController;
import controller.OrderController;
import entity.GameResult;
import entity.HappyHourStatus;
import entity.Order;
import entity.Restaurant;
import entity.RestaurantStatistics;
import entity.Table;
import staff.HostAction;
//import staff.Waiter;
import staff.WaiterAction;
import entity.Customer;
import entity.CustomerStatus;

public class GameApp {
    private Scanner scanner;
    private HappyHourStatus status;
    private int happyHourTurnsLeft = 2;

    public GameApp(Scanner scanner) {
        this.scanner = scanner;
        this.status = HappyHourStatus.HAVE_NOT_USED;
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

    public void displayTurn(Restaurant restaurant, int currentTurn, OrderController orderController) {
        // displays current turn
        RestaurantStatistics stats = restaurant.getStatistics();
        System.out.printf("-------------------------------Turn %d--------------------------------\n", currentTurn);
        // Print revenue right now
        int revenue = stats.getRevenueCents();
        double revenue_in_dollars = revenue / 100.0;
        System.out.printf("Your revenue as of now is : $%.2f\n", revenue_in_dollars);
        // Print average satisfaction (or N/A)
        double avg_satisfaction = stats.getAverageSatisfaction();

        if (stats.getPaidCustomerCount() == 0) {
            System.out.printf("Your average satisfaction among paid customers are N/A\n\n");
        } else {
            System.out.printf("Your average satisfaction among paid customers are %.2f\n\n", avg_satisfaction);
        }

        System.out.println("--- Waiting Queue ---");
        List<Customer> waitingCustomers = restaurant.getCustomers();
        boolean hasWaitingCustomer = false;

        for (Customer customer : waitingCustomers) {
            if (customer.getStatus() == CustomerStatus.WAITING) {
                System.out.println("Customer " + customer.getCustomerId());
                hasWaitingCustomer = true;
            }
        }

        if (!hasWaitingCustomer) {
            System.out.println("No Customer in waiting queue.");
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

        System.out.println("\n--- Pending Orders ---");
        if (orderController.getPendingOrders().isEmpty()) {
            System.out.println("NO PENDING ORDERS");
        } else {
            for (Order order : orderController.getPendingOrders()) {
                System.out.printf("Order " + order.getOrderId() + ": {Order ID: " + order.getOrderId()
                        + " | Food Item: " + order.getItem().getName()
                        + " | Order Status: " + order.getStatus() + "}\n");
            }
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
     */

    public HostAction getHostAction(){
        while (true) {
            System.out.println("\n\u001b[4mWhat would you like the host to do?\u001b[0m");
            System.out.println("1. Seat a customer in the queue");
            System.out.println("2. Wait\n");

            int choice = readInt("Enter your choice (1 or 2): ");

            switch (choice) {
                case 1 -> {
                    return HostAction.SEAT_CUSTOMER;
                }
                case 2 -> {
                    return HostAction.WAIT;
                }
                default -> System.out.println("Invalid Choice. Try again.");
            }
        }
    }
    
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
        double revenue_in_dollars = revenue / 100.0;
        System.out.printf("Your revenue as of now is : \u001B[4m$%.2f\u001B[0m\n", revenue_in_dollars);
        // 4. Print average satisfaction (or N/A)
        double avg_satisfaction = stats.getAverageSatisfaction();
        System.out.printf(
                "Your average satisfaction among paid customers is \u001B[4m%s\u001B[0m%n",
                avg_satisfaction == -1 ? "N/A" : String.format("%.2f", avg_satisfaction)); // 5. Print turns used
        System.out.printf("You have used up \u001B[4m%d turns\u001B[0m.\n", currentTurn);
        System.out.println("--------------------------------------------------------------------\n");
    }

    /*
     * public void displayStaffAvailability(boolean waiterAvailable, boolean
     * chefAvailable, boolean cashierAvailable) {
     * 
     * System.out.println("--- STAFF AVAILABILITY ---");
     * System.out.println("Waiter  : " + (waiterAvailable ? "AVAILABLE" : "DONE"));
     * System.out.println("Chef    : " + (chefAvailable ? "AVAILABLE" : "DONE"));
     * System.out.println("Cashier : " + (cashierAvailable ? "AVAILABLE" : "DONE"));
     * 
     * }
     */
    public void displayStaffTasks(int turn, Restaurant restaurant) {
        System.out.printf("--- STAFF TASKS AVALIABLE (TURN %d) ---\n\n", turn);
        displayHostTasks(restaurant);
        displayWaiterTasks(restaurant);
        displayChefTasks(restaurant);
        displayCashierTasks(turn, restaurant);
        System.out.println("--------------------------------------\n");
    }

    public void displayHostTasks(Restaurant restaurant){
        System.out.print("--- HOST'S AVALIABLE TASKS ---\n");
        if (restaurant.getWaitingQueueSize() > 0 && restaurant.hasFreeTable()) {
            System.out.printf("1. \u001B[4mSeat a customer in the queue\u001B[0m\n");
        }
        System.out.printf("2. \u001B[4mWait\n\u001B[0m");
        System.out.println();
    }

    public void displayWaiterTasks(Restaurant restaurant) {
        System.out.print("--- WAITER'S AVALIABLE TASKS ---\n");
        if (restaurant.getWaitingQueueSize() > 0 && restaurant.hasFreeTable()) {
            System.out.printf("1. \u001B[4mSeat a customer in the queue\u001B[0m\n");
        }
        if (restaurant.hasSeatedWithoutOrder()) {
            System.out.printf("2. \u001B[4mTake an order from customer\u001B[0m\n");
        }
        if (restaurant.hasReadyOrder()) {
            System.out.printf("3. \u001B[4mServe food to customer\u001B[0m\n");
        }
        System.out.printf("4. \u001B[4mWait\n\u001B[0m");
        System.out.println();
    }

    public void displayChefTasks(Restaurant restaurant) {
        System.out.print("--- CHEF'S AVAILABLE TASKS ---\n");
        if (restaurant.hasOrderToPrepare()) {
            System.out.printf("1. \u001B[4mPrepare one unit of an order\u001B[0m\n");
        }
        System.out.printf("2. \u001B[4mWait\n\n\u001B[0m");
    }

    public void displayCashierTasks(int turn, Restaurant restaurant) {
        System.out.print("--- CASHIER'S CURRENT TASK (AUTO)---\n");
        Customer payer = restaurant.getOldestReadyPayer(turn);
        if (payer == null) {
            System.out
                    .println("Action: \u001B[4mCashier will not process any payments at the end of the turn\u001B[0m");
        } else {
            double dollars = (payer.getOrder().getFinalPriceCents() / 100.0);
            System.out.printf(
                    "Action: \u001B[4mCashier will process payment $%.2f from Customer %d at the end of the turn\u001B[0m%n",
                    dollars, payer.getCustomerId());
        }
        System.out.println();
    }

    public void displayResult(GameResult result, Restaurant restaurant) {
        // prints out final result
        int reasons = 0;
        RestaurantStatistics stats = restaurant.getStatistics();
        System.out.println("----------------------------GAME OVER----------------------------");
        if (result == GameResult.VICTORY) {
            System.out.println("Congratulations!!! You have successfully ran the restaurant!!!\n\n");
        } else {
            System.out.println(
                    "Sorry you have not succedded running this restaurant.\n");
            System.out.println("\u001B[4mREASONS\u001B[0m");
            if (stats.getPaidCustomerCount() < 4) {
                reasons++;
                System.out.printf(
                        "%d. You only have %d paid customers, but a minimum of 4 paid customers are required.\n",
                        reasons, stats.getPaidCustomerCount());
            }
            if (stats.getRevenueCents() < 4500) {
                reasons++;
                System.out.printf("%d. You only had $%.2f in revenue, but a minimum revenue of $45.00 is required.\n",
                        reasons, stats.getRevenueCents() / 100.0);
            }
            if (stats.getAverageSatisfaction() < 60.00) {
                reasons++;
                if (stats.getAverageSatisfaction() == -1) {
                    System.out.printf(
                            "%d. You only have a average satisfaction score of N/A, but a minimum score of 60.00 is required.\n",
                            reasons);
                } else {
                    System.out.printf(
                            "%d. You only have a average satisfaction score of %.2f, but a minimum score of 60.00 is required.\n",
                            reasons, stats.getAverageSatisfaction());
                }
            }
            System.out.println("\nBetter luck next time!!!\n\n");
        }

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

    public HappyHourStatus getHappyHourChoice(){
        while(true){
            if(status == HappyHourStatus.ENDED){
                return status;
            }
            if(happyHourTurnsLeft == 1){
                System.out.println("\n\t\t    ------------------------------");
                System.out.println("\t\t    HAPPY HOUR: ACTIVE (ROUND 2/2)");
                System.out.println("\t\t    ------------------------------\n");
                System.out.println("\t\t   <===========UPDATED===========>\n");
                happyHourTurnsLeft--;
                return status;
            }
            if(happyHourTurnsLeft == 0){
                status = HappyHourStatus.ENDED;
                return status;
            }
            System.out.println("\u001B[4mWould you like to enable Happy Hour?\u001b[0m");
            System.out.println("(1) Yes");
            System.out.println("(2) No\n");
            System.out.print("Enter your choice (1 or 2): ");
            int choice = scanner.nextInt();
            switch(choice){
                case 1 -> {
                    status = HappyHourStatus.ACTIVE;
                    System.out.println("\n\t\t    ------------------------------");
                    System.out.println("\t\t    HAPPY HOUR: ACTIVE (ROUND 1/2)");
                    System.out.println("\t\t    ------------------------------\n");
                    System.out.println("\t\t   <===========UPDATED===========>\n");
                    happyHourTurnsLeft--;
                    return status;
                }
                case 2 -> {
                    return status;
                }
                default -> System.out.println("Invalid Choice. Try again.");
            }
        }
    }
}
