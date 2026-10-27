package controller;

import entity.Customer;
import entity.MenuItem;
//import java.util.Objects;

public class PricingController {

    private PricingPolicy policy = new StandardPricingPolicy();

    public void setPricingPolicy(PricingPolicy policy) {
         if (policy == null) {
        throw new IllegalArgumentException("Pricing policy cannot be null");
    }

        this.policy = policy;
    }

    public int calculateBill(Customer customer, MenuItem item) {
    if (customer == null) {
        throw new IllegalArgumentException("Customer cannot be null");
    }

    if (item == null) {
        throw new IllegalArgumentException("Menu item cannot be null");
    }

    int originalPrice = item.getPriceCents();

    return policy.apply(originalPrice);
    }
}
