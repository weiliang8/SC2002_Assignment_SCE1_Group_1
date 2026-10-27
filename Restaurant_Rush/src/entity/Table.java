package entity;

/** A restaurant table with at most one current customer. */
public class Table {
    private final int id;
    private Customer occupant;

    public Table(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Table id must be positive.");
        }
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public Customer getOccupant() {
        return occupant;
    }

    public boolean isFree() {
        return occupant == null;
    }

    public void seat(Customer customer) {
        if (customer == null) {
            throw new IllegalArgumentException("Customer cannot be null.");
        }
        if (!isFree()) {
            throw new IllegalStateException("Table " + id + " is already occupied.");
        }
        occupant = customer;
    }

    public void release() {
        if (isFree()) {
            throw new IllegalStateException("Table " + id + " is already free.");
        }
        occupant = null;
    }
}