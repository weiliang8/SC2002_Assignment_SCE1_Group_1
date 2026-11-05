package controller;

import entity.Customer;
import entity.MenuItem;

public class PricingController {

    private PricingPolicy policy = new StandardPricingPolicy();
    private PricingPolicy happyHourPolicy = new HappyHourPolicy();

    public void setPricingPolicy(PricingPolicy policy) {
         if (policy == null) {
        throw new IllegalArgumentException("Pricing policy cannot be null");
    }

        this.policy = policy;
    }

    public int calculateBill(Customer customer, MenuItem item, boolean isHappyHourActive) {
    if (customer == null) {
        throw new IllegalArgumentException("Customer cannot be null");
    }

    if (item == null) {
        throw new IllegalArgumentException("Menu item cannot be null");
    }

    int originalPrice = item.getPriceCents();
    int customerPrice = customer.applyDiscount(originalPrice); // e.g. VIP 10% off

    if(isHappyHourActive){
        return happyHourPolicy.apply(customerPrice);
    }

    return policy.apply(customerPrice);
    }
}
