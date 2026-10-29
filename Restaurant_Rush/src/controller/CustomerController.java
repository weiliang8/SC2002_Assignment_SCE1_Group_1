package controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import entity.Customer;
import entity.CustomerStatus;
import entity.Restaurant;
import entity.CustomerType;

/**
 * Person 2 control logic. Restaurant and SeatingController belong to Person 5.
 * TurnController decides WHEN arrivals and each end-of-turn phase are called.
 */
public class CustomerController {
    private final Restaurant restaurant;
    private final CustomerCreator customerCreator;
    private final SeatingController seatingController;
    private int nextCustomerId = 1;

    public CustomerController(Restaurant restaurant, 
                              SeatingController seatingController) {
        this.restaurant = Objects.requireNonNull(restaurant, "Restaurant is required.");
        this.customerCreator = new CustomerCreator();
        //this.customerCreator = Objects.requireNonNull(creator, "CustomerCreator is required.");//need change
        this.seatingController = Objects.requireNonNull(seatingController,"SeatingController is required.");
        // Normally empty at game start; avoid reusing IDs if records already exist.
        for (Customer customer : restaurant.getCustomers()) {
            nextCustomerId = Math.max(nextCustomerId, customer.getCustomerId() + 1);
        }
    }

    /**
     * Create ONE arrival. TurnController calls this only on 1,4,7,10,13,16.
     * Restaurant.addCustomer must add to BOTH records and the waiting queue.
     */
    public void processNewCustomer(CustomerType type) {
        Customer customer = customerCreator.createCustomer(type, nextCustomerId);
        restaurant.addCustomer(customer);
        nextCustomerId++;
    }

    public void processEating(int turn) {
        for (Customer customer : snapshot()) {
            customer.finishEating(turn);
        }
    }

    /** Call exactly once each turn, after service and eating/payment. */
    public void applySatisfactionLoss() {
        for (Customer customer : snapshot()) {
            customer.applyWaitingPenalty();
        }
    }

    /** Call after satisfaction loss. LEFT records stay available for reporting. */
    public void processAbandonment() {

    for (Customer customer : restaurant.getCustomers()) {

        if (customer.isWaitingForService() && customer.getSatisfaction() == 0) {
            boolean wasSeated =(customer.getStatus() == CustomerStatus.SEATED);
            if (wasSeated) {
                seatingController.releaseTable(customer);
            }
            customer.leave();
            restaurant.recordDeparture(customer);
        }
    }
}

    /** Spreads a just-served customer's review to everyone still waiting. Returns the change applied. */
    public int applyReview(Customer reviewer) {
        int impact = reviewer.getReviewImpact();
        if (impact == 0) {
            return 0;
        }
        for (Customer customer : snapshot()) {
            if (customer != reviewer && customer.isWaitingForService()) {
                customer.adjustSatisfaction(impact);
            }
        }
        return impact;
    }

    public Customer getCustomerById(int id){
         List<Customer> customers = restaurant.getCustomers();
         for (Customer c : customers){
            if(c.getCustomerId() == id)return c;
         }

            return null;
    }

    private List<Customer> snapshot() {
        // Avoid iterator invalidation when teammate code updates restaurant state.
        return new ArrayList<>(restaurant.getCustomers());
    }
}
