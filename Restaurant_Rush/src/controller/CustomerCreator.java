package controller;

import entity.RegularCustomer;




/** Factory for Stage 1. CustomerType is deliberately not used. */
public class CustomerCreator {
    public CustomerCreator(){};

    public RegularCustomer createRegularCustomer(int customerId) {
        return new RegularCustomer(customerId);
    }
}
