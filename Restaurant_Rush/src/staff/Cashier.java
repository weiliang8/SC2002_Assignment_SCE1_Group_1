package staff;

import controller.SeatingController;
import entity.Customer;
import entity.Order;
import entity.Restaurant;

//collect payment
public class Cashier{
    private final Restaurant restaurant;
    private final SeatingController seatingController;

    public Cashier(Restaurant restaurant, SeatingController seatingController){
        this.restaurant = restaurant;
        this.seatingController = seatingController;
    }

    public boolean processPayment(int turn){
        Customer customer = restaurant.getOldestReadyPayer(turn);
        if(customer == null){
            return false;
        }

        Order order = customer.getOrder();
        int billCents = order.getFinalPriceCents();
        order.markPaid();
        customer.markPaid();
        restaurant.recordPayment(customer, billCents);
        seatingController.releaseTable(customer);
        return true;
    }
}
