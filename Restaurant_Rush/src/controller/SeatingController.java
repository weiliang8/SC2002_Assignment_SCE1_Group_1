package controller;

import entity.Customer;
import entity.Restaurant;
import entity.Table;

public class SeatingController {
    private final Restaurant restaurant;

    public SeatingController(Restaurant restaurant) {
        if (restaurant == null) {
            throw new IllegalArgumentException("Restaurant cannot be null.");
        }
        this.restaurant = restaurant;
    }

    public Table assignSeat(Customer customer) {
        if (customer == null) {
            throw new IllegalArgumentException("Customer cannot be null.");
        }
        for (Table existingTable : restaurant.getTables()) {
            if (existingTable.getOccupant() == customer) {
                throw new IllegalStateException("Customer " + customer.getCustomerId() + " is already seated.");
            }
        }
        Table table = findAvailableTable();
        if (table == null) {
            throw new IllegalStateException("No free table is available.");
        }
        try {
            table.seat(customer);
            if (!restaurant.removeCustomerFromQueue(customer)) {
                table.release();
                throw new IllegalStateException("Customer " + customer.getCustomerId() + " is not in the waiting queue.");
            }
            return table;
        } catch (RuntimeException exception) {
            // Undo a partial allocation if queue removal fails unexpectedly.
            if (table.getOccupant() == customer) {
                try {
                    table.release();
                } catch (RuntimeException recoveryException) {
                    exception.addSuppressed(recoveryException);
                }
            }
            throw exception;
        }
    }

    public Table findAvailableTable() {
        for (Table table : restaurant.getTables()) {
            if (table.isFree()) {
                return table;
            }
        }
        return null;
    }

    public void releaseTable(Customer customer) {
        if (customer == null) {
            throw new IllegalArgumentException("Customer cannot be null.");
        }
        for (Table table : restaurant.getTables()) {
            if (table.getOccupant() == customer) {
                table.release();
                return;
            }
        }
        throw new IllegalStateException("Customer " + customer.getCustomerId() + " does not occupy a table.");
    }
}
