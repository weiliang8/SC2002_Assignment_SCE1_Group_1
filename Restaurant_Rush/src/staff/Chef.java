package staff;

import controller.OrderController;
import entity.Order;

// 1 by 1 preparation per turn
public class Chef{
    private int lastTaskTurn = 0;
    private final OrderController orderController;


    public Chef(OrderController orderController){
        this.orderController = orderController;

    }

    public boolean prepareOneUnit(Order order, int turn) {

    checkFreeThisTurn(turn);

    if (order == null) {throw new InvalidActionException("Invalid Order ID.");
    }

    if (!order.canBePrepared()
            || !orderController.getPendingOrders().contains(order)) {throw new InvalidActionException("Order " + order.getOrderId()+ " cannot be prepared.");
    }

    // Prepare exactly one unit
    order.updateUnit();

    // Move finished order to ready queue
    if (order.isReady()) {
        orderController.markOrderReady(order);
    }

    // Chef has completed their task this turn
    lastTaskTurn = turn;

    return true;
}

    private void checkFreeThisTurn(int turn){
        if(lastTaskTurn == turn){
            throw new InvalidActionException("The Chef has already done a task this turn.");
        }
    }

    public boolean hasActedThisTurn(int turn){
        return lastTaskTurn == turn;
    }
}
