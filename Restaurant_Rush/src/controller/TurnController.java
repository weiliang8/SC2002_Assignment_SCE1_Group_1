package controller;

import entity.Restaurant;
import entity.Customer;
import entity.CustomerType;
import entity.Order;
import staff.Waiter;
import staff.WaiterAction;
import staff.Chef;
import staff.InvalidActionException;
import staff.Cashier;
import ui.GameApp;

public class TurnController {
    // private int currentTurn = ;
    private Restaurant restaurant;
    private CustomerController customerController;
    private OrderController orderController;
    private Waiter waiter;
    private Chef chef;
    private Cashier cashier;
    private GameApp gameApp;

    public TurnController(Restaurant restaurant,
            CustomerController customerController,
            OrderController orderController,
            Waiter waiter, Chef chef, Cashier cashier,
            GameApp gameApp) {
        this.restaurant = restaurant;
        this.customerController = customerController;
        this.orderController = orderController;
        this.waiter = waiter;
        this.chef = chef;
        this.cashier = cashier;
        this.gameApp = gameApp;
    }

    public void playTurn(int currentTurn, CustomerType arrival) {
        // 1. Process arrivals first
        processNewArrivals(currentTurn, arrival);

        // 2. Display updated game state
        displayState(currentTurn);

        gameApp.displayStaffAvailability(
                !waiter.hasActedThisTurn(currentTurn),
                !chef.hasActedThisTurn(currentTurn),
                !cashier.hasActedThisTurn(currentTurn));

        // 3. Staff actions
        runWaiterPhase(currentTurn);
        runChefPhase(currentTurn);

        // 4. Eating and payment
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

        // 5. Satisfaction loss and abandonment
        customerController.applySatisfactionLoss();
        processAbandonment();

        // 6. End-of-turn summary
        displayTurnSummary(currentTurn);

    }

    public void displayState(int currentTurn) {
        // decides when to display the turn, ie calls the gameapp.displayTurn
        gameApp.displayTurn(restaurant, currentTurn);

    }

    public void processNewArrivals(int currentTurn, CustomerType arrival) {
        // process the arrivals(like if this turn then it will arrive la)
        if (arrival != null) {
            customerController.processNewCustomer(arrival);
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
                            // System.out.println("\u001B[4mEnter valid customer ID\u001B[0m");
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
                            // System.out.println("\u001B[4mEnter valid customer ID\u001B[0m");
                            continue;
                        }

                        Customer customer = customerController.getCustomerById(id);

                        if (waiter.takeOrder(
                                customer, restaurant.getMenu(), currentTurn)) {
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
                            // System.out.println("\u001B[4mEnter valid customer ID\u001B[0m");
                            continue;
                        }

                        if (waiter.serve(id, currentTurn)) {
                            restaurant.recordServed(
                                    customerController.getCustomerById(id));
                            System.out.println("Customer " + id + " served.");
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

                            return; // End Chef phase
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
        // calls cashier to carry out
        return cashier.processPayment(currentTurn);
    }

    public void processEating(int currentTurn) {
        // carry on eating
        customerController.processEating(currentTurn);
    }

    public void processAbandonment() {
        customerController.processAbandonment();
        orderController.removeCancelledOrders();
    }

    public void displayTurnSummary(int currentTurn) {
        // displays summary after turn
        gameApp.displayTurnSummary(restaurant, currentTurn);
    }

}
