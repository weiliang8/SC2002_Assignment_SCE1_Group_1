package controller;

import entity.Customer;
import entity.RegularCustomer;

/** Factory for Stage 1. CustomerType is deliberately not used. */
public class CustomerCreator {
    public Customer createCustomer(int customerId) {
        return new RegularCustomer(customerId);
    }
}
