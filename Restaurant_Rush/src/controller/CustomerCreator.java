package controller;

import entity.CriticCustomer;
import entity.Customer;
import entity.CustomerType;
import entity.RegularCustomer;
import entity.VIPCustomer;

public class CustomerCreator {
    public CustomerCreator() {
    }

    public Customer createCustomer(CustomerType type, int customerId) {
        return switch (type) {
            case REGULAR -> new RegularCustomer(customerId);
            case VIP -> new VIPCustomer(customerId);
            case CRITIC -> new CriticCustomer(customerId);
        };
    }
}
