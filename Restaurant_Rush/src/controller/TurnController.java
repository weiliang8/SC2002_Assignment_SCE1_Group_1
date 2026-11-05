package controller;

import entity.Restaurant;
import entity.Customer;
import entity.CustomerStatus;
import entity.CustomerType;
import entity.HappyHourStatus;
import entity.Order;
import entity.Table;
import staff.Host;
import staff.HostAction;
import staff.Waiter;
import staff.WaiterAction;
import staff.Chef;
import staff.InvalidActionException;
import staff.Cashier;
import ui.GameApp;

public class TurnController {
    private Restaurant restaurant;
    private CustomerController customerController;
    private OrderController orderController;
    private Host host;
    private Waiter waiter;
    private Chef chef;
    private Cashier cashier;
    private GameApp gameApp;
    private HappyHourStatus status;
    private int happyHourTurnsLeft;
    private boolean isHappyHourActive;

    public TurnController(Restaurant restaurant,
            CustomerController customerController,
            OrderController orderController, Host host,
            Waiter waiter, Chef chef, Cashier cashier,
            GameApp gameApp) {
        this.restaurant = restaurant;
        this.customerController = customerController;
        this.orderController = orderController;
        this.host = host;
        this.waiter = waiter;
        this.chef = chef;
        this.cashier = cashier;
        this.gameApp = gameApp;
        this.status = HappyHourStatus.HAVE_NOT_USED;
        this.happyHourTurnsLeft = 2;
        this.isHappyHourActive = false;
    }

    public void playTurn(int currentTurn, CustomerType arrival) {
        // 1. Process arrivals first
        processNewArrivals(currentTurn, arrival);
        // 2. Display gamestate
        displayState(currentTurn);
        // 3. Process Happy Hour Decision
        processHappyHour();
        
        // 4. Display staff tasks available
        
        gameApp.displayStaffTasks(currentTurn, restaurant);

        // 5. Staff actions
        runHostPhase(currentTurn);
        System.out.println();
        gameApp.displayWaiterTasks(restaurant);
        System.out.println("------------------------------");
        runWaiterPhase(currentTurn);
        System.out.println();
        gameApp.displayChefTasks(restaurant);
        System.out.println("------------------------------");
        runChefPhase(currentTurn);

        // 6. Eating and payment
        processEating(currentTurn);

        Customer paymentCustomer = processPayment(currentTurn);
        if (paymentCustomer == null) {
            System.out.println("No customer ready for payment");
            System.out.println("Cashier waits this turn.\n");
        } else {
            System.out.printf("Cashier processed Customer %d payment of $%.2f\n", paymentCustomer.getCustomerId(),
                    paymentCustomer.getOrder().getFinalPriceCents() / 100.0);
        }
        ;

        // 7. Satisfaction loss and abandonment
        customerController.applySatisfactionLoss();
        processAbandonment();

        // 8. End-of-turn summary
        displayTurnSummary(currentTurn);

    }

    public void displayState(int currentTurn) {
        // decides when to display the turn, ie calls the gameapp.displayTurn
        gameApp.displayTurn(restaurant, currentTurn, orderController);

    }

    public void processNewArrivals(int currentTurn, CustomerType arrival) {
        // process the arrivals
        if (arrival != null) {
            customerController.processNewCustomer(arrival);
        }

    }

    public void runHostPhase(int currentTurn){
        while(true){
            try{
                HostAction action = gameApp.getHostAction();

                switch (action) {
                    case SEAT_CUSTOMER -> {
                        int id = gameApp.getWaiterSeatCustomerId(restaurant);
                        if (id == -1) {
                        
                            continue;
                        }
                        Customer customer = customerController.getCustomerById(id);

                        if (host.seat(customer, currentTurn)) {
                            System.out.println("Customer " + id + " seated.");
                            return;
                        }
                    }
                    case WAIT -> {
                        System.out.println("Host waits.");
                        return;
                    }

                }
            }catch (InvalidActionException | IllegalStateException ex) {
                System.out.println("Invalid action: " + ex.getMessage());
            }
        }
    }

    public void runWaiterPhase(int currentTurn) {
        while (true) {
            try {
                WaiterAction action = gameApp.getWaiterAction();

                switch (action) {
                    case SEAT -> {
                        int id = gameApp.getWaiterSeatCustomerId(restaurant);
                        if (id == -1) {
                            continue;
                        }

                        Customer customer = customerController.getCustomerById(id);

                        if (waiter.seat(customer, currentTurn)) {
                            System.out.println("Customer " + id + " seated.");
                            return;
                        }
                    }

                    case TAKE_ORDER -> {
                        int id = gameApp.getTakeOrderCustomerId(restaurant);
                        if (id == -1) {
                            continue;
                        }

                        Customer customer = customerController.getCustomerById(id);

                        if (waiter.takeOrder(
                                customer, restaurant.getMenu(), currentTurn, isHappyHourActive)) {
                            Order order = customer.getOrder();

                            System.out.printf(
                                    "Customer %d ordered %s (Order %d) - $%.2f%n",
                                    id,
                                    order.getItem().getName(),
                                    order.getOrderId(),
                                    order.getFinalPriceCents() / 100.0);
                            return;
                        }
                    }

                    case SERVE -> {
                        int id = gameApp.getServeCustomerId(restaurant);
                        if (id == -1) {
                            continue;
                        }

                        if (waiter.serve(id, currentTurn)) {
                            Customer served = customerController.getCustomerById(id);
                            restaurant.recordServed(served);
                            System.out.println("Customer " + id + " served.");

                            // Stage 2
                            int impact = customerController.applyReview(served);
                            if (impact > 0) {
                                System.out.printf("\nCustomer %d posted a GOOD review! Everyone still waiting gains %d satisfaction.%n", id, impact);
                            } else if (impact < 0) {
                                System.out.printf("\nCustomer %d posted a BAD review! Everyone still waiting loses %d satisfaction.%n", id, -impact);
                            }
                            return;
                        }
                    }

                    case WAIT -> {
                        System.out.println("Waiter waits.");
                        return;
                    }
                }

            } catch (InvalidActionException | IllegalStateException ex) {
                System.out.println("Invalid action: " + ex.getMessage());
            }
        }
    }

    public void runChefPhase(int currentTurn) {

        while (true) {

            int choice = gameApp.getChefChoice();

            switch (choice) {

                case 1 -> {

                    // Display pending orders and get manager selection
                    int orderId = gameApp.getChefOrderId(
                            orderController.getPendingOrders());

                    if (orderId == -1) {
                        continue;
                    }

                    // Find selected order
                    Order order = orderController.getOrderById(orderId);

                    try {
                        if (chef.prepareOneUnit(order, currentTurn)) {

                            System.out.printf(
                                    "\nChef prepared Order %d (%s)%n",
                                    order.getOrderId(),
                                    order.getItem().getName());

                            System.out.printf(
                                    "Progress: %d/%d | Status: %s%n%n",
                                    order.getUnitsCompleted(),
                                    order.getUnitsTotal(),
                                    order.getStatus());

                            return; 
                        }

                    } catch (InvalidActionException ex) {
                        System.out.println(
                                "Invalid action: " + ex.getMessage());
                    }
                }

                case 2 -> {
                    System.out.println("Chef waits this turn.\n");
                    return;
                }
            }
        }
    }

    public Customer processPayment(int currentTurn) {
        return cashier.processPayment(currentTurn);
    }

    public void processEating(int currentTurn) {
        customerController.processEating(currentTurn);
    }

    public void processAbandonment() {
        customerController.processAbandonment();
        orderController.removeCancelledOrders();
    }

    public void displayTurnSummary(int currentTurn) {
        gameApp.displayTurnSummary(restaurant, currentTurn);
    }

    private void runHappyHourChoice(){
        while(true){
            try{
                HappyHourStatus choice = gameApp.getHappyHourChoice();

                switch(choice){
                    case ACTIVE -> {
                        if(status != choice){
                            status = choice;
                        }
                        isHappyHourActive = true;
                        happyHourTurnsLeft--;
                        for(Customer c: restaurant.getWaitingCustomers()){
                            c.adjustSatisfaction(15);
                        }

                        for(Table table: restaurant.getTables()){
                            Customer customer = table.getOccupant();

                            if(customer != null && (customer.getStatus() == CustomerStatus.WAITING || customer.getStatus() == CustomerStatus.SEATED)){
                                customer.adjustSatisfaction(15);
                            }
                        }
                        gameApp.displayUpdatedSatisfaction(restaurant);
                        return;
                    }

                    case HAVE_NOT_USED -> {
                        return;
                    }
                    case ENDED ->{
                        return;
                    }

                }
            }catch (InvalidActionException | IllegalStateException ex) {
                System.out.println("Invalid action: " + ex.getMessage());
            }
        }
    }

    public void processHappyHour(){
        if(status == HappyHourStatus.ENDED){
            System.out.println("--------------------------------------------------------------------------------------------------------------");
            System.out.println("\t\t\t\t\t Happy Hour: UNAVAILABLE ");
            System.out.println("--------------------------------------------------------------------------------------------------------------\n");
        }
        else if(happyHourTurnsLeft == 1){
            System.out.println("\n--------------------------------------------------------------------------------------------------------------");
            System.out.println("\t\t\t\t\tHAPPY HOUR: ACTIVE (ROUND 2/2)");
            System.out.println("--------------------------------------------------------------------------------------------------------------\n");
            System.out.println("\nUPDATING SATISFACTION SCORES......\n");
            if(status != HappyHourStatus.ACTIVE){
                status = HappyHourStatus.ACTIVE;
            }
            isHappyHourActive = true;
            happyHourTurnsLeft--;
            for(Customer c: restaurant.getWaitingCustomers()){
                c.adjustSatisfaction(15);
            }

            for(Table table: restaurant.getTables()){
                Customer customer = table.getOccupant();

            if(customer != null && (customer.getStatus() == CustomerStatus.WAITING || customer.getStatus() == CustomerStatus.SEATED)){
                customer.adjustSatisfaction(15);
            }
            }
                        
            gameApp.displayUpdatedSatisfaction(restaurant);
            
        }
        else if(happyHourTurnsLeft == 0){
            System.out.println("--------------------------------------------------------------------------------------------------------------");
            System.out.println("\t\t\t\t\t Happy Hour: UNAVAILABLE ");
            System.out.println("--------------------------------------------------------------------------------------------------------------\n");
            if(status != HappyHourStatus.ENDED){
                status = HappyHourStatus.ENDED;
            }
            isHappyHourActive = false;

            
        }
        else{
        runHappyHourChoice();
        }
    }

}
