package entity;

import java.util.List;
import java.util.Objects;

public abstract class Customer {
    private final int customerId;
    private CustomerType customerType;
    private int satisfaction;
    private CustomerStatus status;
    private Order order;
    private int servedTurn;
    private int readyToPayTurn;
    private final int waitingPenalty;

    public Customer(int id, CustomerType customerType, int waitingPenalty) {
        if (id <= 0) {
            throw new IllegalArgumentException("Customer ID must be positive.");
        }
        if (waitingPenalty <= 0) {
            throw new IllegalArgumentException("Waiting penalty must be positive.");
        }
        this.customerId = id;
        this.customerType = customerType;
        this.waitingPenalty = waitingPenalty;
        this.satisfaction = 100;
        this.status = CustomerStatus.WAITING;
        this.servedTurn = -1;
        this.readyToPayTurn = -1;
    }

    public int getWaitingPenalty() {
        return waitingPenalty;
    }

    public void applyWaitingPenalty() {
        if (isWaitingForService()) {
            adjustSatisfaction(-getWaitingPenalty());
        }
    }

    // customer choice: IDs 1,2,3,... cycle over the menu
    public MenuItem chooseMenuItem(List<MenuItem> menu) {
        Objects.requireNonNull(menu, "Menu must not be null.");
        if (menu.isEmpty()) {
            throw new IllegalArgumentException("Cannot choose from an empty menu.");
        }
        return Objects.requireNonNull(menu.get((customerId - 1) % menu.size()),
                "Chosen menu item must not be null.");
    }

    public int applyDiscount(int priceCents) {
        return priceCents;
    }

    public int getReviewImpact() {
        return 0;
    }

    public int adjustSatisfaction(int amount) {
        //Terminal customer records must not be changed or revived.
        if (status != CustomerStatus.PAID && status != CustomerStatus.LEFT) {
            long adjusted = (long) satisfaction + amount;
            satisfaction = (int) Math.max(0L, Math.min(100L, adjusted));
        }
        return satisfaction;
    }

    public int getCustomerId () {
        return customerId;
    }

    public CustomerType getCustomerType() {
        return customerType;
    }

    public int getSatisfaction() {
        return satisfaction;
    }

    public CustomerStatus getStatus() {
        return status;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        Objects.requireNonNull(order, "Order must not be null.");
        require(status == CustomerStatus.SEATED, "Only seated customers can order.");
        require(this.order == null, "Customer already has an order.");
        require(order.getStatus() == OrderStatus.ORDERED,
                "Attach a newly created ORDERED order.");
        this.order = order;
    }

    public void markSeated() {
        require(status == CustomerStatus.WAITING, "Customer is not waiting for a table.");
        status = CustomerStatus.SEATED;
    }

     public void markServed(int turn) {
         requirePositiveTurn(turn);
        require(status == CustomerStatus.SEATED && order != null,
                 "Customer must be seated with an order before serving.");
        require(order.getStatus() == OrderStatus.READY
                        || order.getStatus() == OrderStatus.SERVED,
                 "Food must be READY before serving.");
        if (order.getStatus() == OrderStatus.READY) {
            order.markServed();
       }
        servedTurn = turn;
        status = CustomerStatus.EATING;
     }

    public void finishEating(int turn) {
        requirePositiveTurn(turn);
        if (status == CustomerStatus.EATING && turn > servedTurn) {
            readyToPayTurn = turn;
            status = CustomerStatus.READY_TO_PAY;
        }
    }

    public boolean canPay(int turn) {
        return status == CustomerStatus.READY_TO_PAY && turn > readyToPayTurn;
    }

    public void markPaid() {
        require(status == CustomerStatus.READY_TO_PAY, "Customer is not ready to pay.");
        require(order != null && (order.getStatus() == OrderStatus.SERVED
                        || order.getStatus() == OrderStatus.PAID),
                "Customer must have a served order.");
        if (order.getStatus() == OrderStatus.SERVED) {
            order.markPaid();
        }
        status = CustomerStatus.PAID;
    }

    public void leave() {
        require(isWaitingForService() && satisfaction == 0,
                "Only an unserved customer with zero satisfaction abandons.");
        if (order != null) {
            OrderStatus foodState = order.getStatus();
            require(foodState == OrderStatus.ORDERED || foodState == OrderStatus.PREPARING
                            || foodState == OrderStatus.READY || foodState == OrderStatus.CANCELLED,
                    "A served or paid order cannot be abandoned.");
            if (foodState != OrderStatus.CANCELLED) {
                order.markCancel();
            }
        }
        status = CustomerStatus.LEFT;
    }

    public int getServedTurn() {
        return servedTurn;
    }

    public int getReadyToPayTurn() {
        return readyToPayTurn;
    }


    public boolean isWaitingForService() {
        return status == CustomerStatus.WAITING || status == CustomerStatus.SEATED;
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new IllegalStateException(message);
        }
    }

    private static void requirePositiveTurn(int turn) {
        if (turn <= 0) {
            throw new IllegalArgumentException("Turn must be positive.");
        }
    }

}
