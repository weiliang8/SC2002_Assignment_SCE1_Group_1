package controller;

public interface PricingPolicy {
    int apply(int customerPriceCents);
}
