package controller;

public class StandardPricingPolicy implements PricingPolicy {

    @Override
    public int apply(int customerPriceCents) {

        if (customerPriceCents < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }

        return customerPriceCents;
    }
}
