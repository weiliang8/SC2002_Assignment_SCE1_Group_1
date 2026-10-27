package entity;

public class Order {
    private final int orderId;
    private final int customerId;
    private final MenuItem item;
    private final int finalPriceCents;
    private int unitsCompleted = 0;
    private OrderStatus status = OrderStatus.ORDERED;

    public Order(int orderId, int customerId, MenuItem item, int finalPriceCents) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.item = item;
        this.finalPriceCents = finalPriceCents;
    }

    // chef cook 1 unit, the last unit make the order READY
    public void updateUnit() {
        if (!canBePrepared()) {
            throw new IllegalStateException(
                    "Order cannot be prepared in status: " + status);
        }

        if (unitsCompleted >= getUnitsTotal()) {
            throw new IllegalStateException(
                    "All preparation units are already completed.");
        }

        unitsCompleted++;
        status = (unitsCompleted >= getUnitsTotal()) ? OrderStatus.READY : OrderStatus.PREPARING;
    }

    public void markServed() {
        if (status != OrderStatus.READY) {
            throw new IllegalStateException(
                    "Only READY orders can be served.");
        }

        status = OrderStatus.SERVED;
    }

    public void markPaid() {
        if (status != OrderStatus.SERVED) {
        throw new IllegalStateException(
            "Only SERVED orders can be paid."
        );
    }

    status = OrderStatus.PAID;
    }

    // if customer left, cancel only if the food not served
    public void markCancel() {
        if (status != OrderStatus.SERVED && status != OrderStatus.PAID) {
            status = OrderStatus.CANCELLED;
        }
    }

    public boolean isReady() {
        return status == OrderStatus.READY;
    }

    public boolean canBePrepared() {
        return status == OrderStatus.ORDERED || status == OrderStatus.PREPARING;
    }

    public int getOrderId() {
        return orderId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public MenuItem getItem() {
        return item;
    }

    public int getUnitsTotal() {
        return item.getPreparationUnits();
    }

    public int getUnitsCompleted() {
        return unitsCompleted;
    }

    public int getFinalPriceCents() {
        return finalPriceCents;
    }

    public OrderStatus getStatus() {
        return status;
    }
}