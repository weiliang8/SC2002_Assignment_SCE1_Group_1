package staff;

import controller.PricingController;
import controller.SeatingController;
import entity.Customer;
import entity.CustomerStatus;
import entity.Menu;
import entity.MenuItem;
import entity.Order;


//One task per turn (seat, order, serve)
public class Waiter{
    private final SeatingController seatingController;
    private final PricingController pricingController;
    private int lastTaskTurn = 0;

    public Waiter(SeatingController seatingController, PricingController pricingController){
        this.seatingController = seatingController;
        this.pricingController = pricingController;
    }

    //seating
    public boolean seat(Customer customer, int turn){
        checkFreeThisTurn(turn);

        if(customer == null){
            throw new InvalidActionException("Invalid customer.");
        }

        if(customer.getStatus() != CustomerStatus.WAITING){
            throw new InvalidActionException("Customer " + customer.getCustomerId() + " is not in the queue.");
        }

        if(seatingController.findAvailableTable() == null){
            throw new InvalidActionException("No free table.");
        }

        seatingController.assignSeat(customer);
        customer.markSeated();
        lastTaskTurn = turn;
        return true;
    }

    //take order
    public boolean takeOrder(Customer customer, Menu menu, int turn){
        checkFreeThisTurn(turn);

        if(customer == null){
            throw new InvalidActionException("Invalid customer.");
        }

        if(customer.getStatus() != CustomerStatus.SEATED){
            throw new InvalidActionException("Customer " + customer.getCustomerId() + " is not seated.");
        }

        if(customer.getOrder() != null){
            throw new InvalidActionException("Customer " + customer.getCustomerId() + " has already ordered.");
        }

        MenuItem item = customer.chooseMenuItem(menu.getItems());
        int priceCents = pricingController.calculateBill(customer, item);
        customer.setOrder(new Order(customer.getCustomerId(), item, priceCents));
        lastTaskTurn = turn;
        return true;
    }

    //serve
    public boolean serve(Customer customer, int turn){
        checkFreeThisTurn(turn);
        
        if(customer == null){
            throw new InvalidActionException("Invalid customer.");
        }

        Order order = customer.getOrder();
        if(order == null){
            throw new InvalidActionException("Customer " + customer.getCustomerId() + " has not ordered yet.");
        }

        if(!order.isReady()){
            throw new InvalidActionException("Order for customer " + customer.getCustomerId()
                    + " is not ready (status " + order.getStatus() + ").");
        }

        order.markServed();
        customer.markServed(turn);
        lastTaskTurn = turn;
        return true;
    }

    public boolean hasActedThisTurn(int turn){
        return lastTaskTurn == turn;
    }

    private void checkFreeThisTurn(int turn){
        if(lastTaskTurn == turn){
            throw new InvalidActionException("The Waiter has already done a task this turn.");
        }
    }
}
