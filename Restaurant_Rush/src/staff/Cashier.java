package staff;

import controller.SeatingController;
import entity.Customer;
import entity.Order;
import entity.Restaurant;

//collect payment
public class Cashier{
    private final Restaurant restaurant;
    private final SeatingController seatingController;
    private int lastTaskTurn = 0;


    public Cashier(Restaurant restaurant, SeatingController seatingController){
        this.restaurant = restaurant;
        this.seatingController = seatingController;
    }

    public Customer processPayment(int turn){
        checkFreeThisTurn(turn);
        Customer customer = restaurant.getOldestReadyPayer(turn);
        if(customer == null){
            return null;
        }

        Order order = customer.getOrder();
        int billCents = order.getFinalPriceCents();
        customer.markPaid();
        restaurant.recordPayment(customer, billCents);
        seatingController.releaseTable(customer);
        lastTaskTurn = turn;
        return customer;
    }

        public boolean hasActedThisTurn(int turn){
        return lastTaskTurn == turn;
    }
    
    private void checkFreeThisTurn(int turn){
        if(lastTaskTurn == turn){
            throw new InvalidActionException("The Cashier has already done a task this turn.");
        }
    }
}
