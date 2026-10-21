package entity;

import java.util.List;
import java.util.Objects;

/**
 * Person 2: customer state and behaviour, following the supplied UML.
 * No queue, table, revenue or CLI ownership is placed in this class.
 */
public abstract class Customer {
    private final int customerId;
    private int satisfaction;
    private CustomerStatus status;
    private Order order;
    private int servedTurn;
    private int readyToPayTurn;
    private final int waitingPenalty;

    public Customer(int id, int waitingPenalty) {
        if (id <= 0) {
            throw new IllegalArgumentException("Customer ID must be positive.");
        }
        if (waitingPenalty <= 0) {
            throw new IllegalArgumentException("Waiting penalty must be positive.");
        }
        this.customerId = id;
        this.waitingPenalty = waitingPenalty;
        this.satisfaction = 100;
        this.status = CustomerStatus.WAITING;
        this.servedTurn = -1;
        this.readyToPayTurn = -1;
    }

    public int getWaitingPenalty() {
        return waitingPenalty;
    }

    /** Called exactly once at turn end by CustomerController. */
    public void applyWaitingPenalty() {
        if (isWaitingForService()) {
            adjustSatisfaction(-getWaitingPenalty());
        }
    }

    /**
     * Deterministic customer choice: IDs 1,2,3,... cycle over the menu.
     * With menu order Salad, Burger, Pasta the choices repeat in that order.
     * The manager never selects the food. Subclasses may override this rule.
     */
    public MenuItem chooseMenuItem(List<MenuItem> menu) {
        Objects.requireNonNull(menu, "Menu must not be null.");
        if (menu.isEmpty()) {
            throw new IllegalArgumentException("Cannot choose from an empty menu.");
        }
        return Objects.requireNonNull(menu.get((customerId - 1) % menu.size()),
                "Chosen menu item must not be null.");
    }

    /** Adjust by a signed amount, clamp to 0..100, and return the new value. */
    public int adjustSatisfaction(int amount) {
        // Terminal customer records must not be changed or revived.
        if (status != CustomerStatus.PAID && status != CustomerStatus.LEFT) {
            long adjusted = (long) satisfaction + amount;
            satisfaction = (int) Math.max(0L, Math.min(100L, adjusted));
        }
        return satisfaction;
    }

    public int getCustomerId() {
        return customerId;
    }

    public int getSatisfaction() {
        return satisfaction;
    }

    public CustomerStatus getStatus() {
        return status;
    }

    /** Returns null before an order has been taken. */
    public Order getOrder() {
        return order;
    }

    /** Waiter attaches the new order after seating and price calculation. */
    public void setOrder(Order order) {
        Objects.requireNonNull(order, "Order must not be null.");
        require(status == CustomerStatus.SEATED, "Only seated customers can order.");
        require(this.order == null, "Customer already has an order.");
        require(order.getStatus() == OrderStatus.ORDERED,
                "Attach a newly created ORDERED order.");
        this.order = order;
    }

    /** Existing Waiter calls this after SeatingController has assigned a free table. */
    public void markSeated() {
        require(status == CustomerStatus.WAITING, "Customer is not waiting for a table.");
        status = CustomerStatus.SEATED;
    }

    /**
     * Called by Waiter in its phase, before Chef acts this turn.
     * Keeps customer and order states consistent. Also accepts an order that
     * Waiter has already marked SERVED immediately before this call.
     */
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

    /** Served on turn s -> ready to pay on turn s+1, not on turn s. */
    public void finishEating(int turn) {
        requirePositiveTurn(turn);
        if (status == CustomerStatus.EATING && turn > servedTurn) {
            readyToPayTurn = turn;
            status = CustomerStatus.READY_TO_PAY;
        }
    }

    /** Cashier must call this before charging: ready on r -> payment after r. */
    public boolean canPay(int turn) {
        return status == CustomerStatus.READY_TO_PAY && turn > readyToPayTurn;
    }

    /**
     * Cashier calls only after canPay(currentTurn) succeeds.
     * Retains the UML's no-argument signature; this method cannot itself know
     * the current game turn. Cashier owns the one-payment-per-turn constraint.
     */
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

    /** Cancel unserved food. Controller handles queue, table and statistics. */
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

    // Read-only helpers added to the UML for integration and cashier ordering.
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
