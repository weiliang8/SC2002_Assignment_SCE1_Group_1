package staff;

import controller.CustomerController;
import controller.OrderController;
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
    private final OrderController orderController;
    private  final CustomerController customerController;

    private int lastTaskTurn = 0;

    public Waiter(SeatingController seatingController, PricingController pricingController,OrderController orderController, CustomerController customerController){
        this.seatingController = seatingController;
        this.pricingController = pricingController;
        this.orderController = orderController;
        this.customerController = customerController;
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
        Order order = new Order(orderController.getTotalOrderCount()+1,customer.getCustomerId(), item, priceCents);

        customer.setOrder(order);
        orderController.addOrder(order);
        
        lastTaskTurn = turn;
        return true;
    }

    //serve
    
public boolean serve(int customerId, int turn) {

    checkFreeThisTurn(turn);

    // Find the customer selected by the manager
    Customer customer =
        customerController.getCustomerById(customerId);

    if (customer == null) {
        throw new InvalidActionException("Customer not found.");
    }

    if (customer.getStatus() != CustomerStatus.SEATED) {
        throw new InvalidActionException(
            "Customer is not seated."
        );
    }

    // Retrieve the customer's order
    Order order = customer.getOrder();

    if (order == null) {
        throw new InvalidActionException(
            "Customer has not placed an order."
        );
    }

    if (!order.isReady()) {
        throw new InvalidActionException(
            "Order is not READY."
        );
    }

   

    // Mark customer as EATING and order as SERVED
    customer.markServed(turn);

    // Remove the selected order from the ready queue
    orderController.getAllReadyOrder().remove(order);

    // Record Waiter's completed task
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
