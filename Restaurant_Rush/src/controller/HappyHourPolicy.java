package controller;

public class HappyHourPolicy implements PricingPolicy {
    
    @Override 
    public int apply(int priceCents){
        return (int) Math.round(priceCents * 0.8);
    }
}
