package controller;

import entity.RegularCustomer;

/** Factory for Stage 1*/
public class CustomerCreator {
    public CustomerCreator(){};

    public RegularCustomer createRegularCustomer(int customerId) {
        return new RegularCustomer(customerId);
    }
}
