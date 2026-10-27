package entity;

/** Customer states from the group UML. Food progress belongs to OrderStatus. */
public enum CustomerStatus {
    WAITING,
    SEATED,
    EATING,
    READY_TO_PAY,
    PAID,
    LEFT
}
