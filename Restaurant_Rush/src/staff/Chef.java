package staff;

import entity.Order;

// 1 by 1 preparation per turn
public class Chef{
    public Chef(){}

    public boolean prepareOneUnit(Order order){
        // Reject if the order does not exist
        if(order == null){
            throw new InvalidActionException("No such order.");
        }

        // Only ORDERED or PREPARING orders can be cooked
        if(!order.canBePrepared()){
            throw new InvalidActionException("Order #" + order.getOrderId()
                    + " cannot be prepared (status " + order.getStatus() + ").");
        }

        // Cook one unit;
        order.updateUnit();
        return true;
    }
}
