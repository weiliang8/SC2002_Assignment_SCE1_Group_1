package entity;

/** Cumulative statistics for one restaurant run. Money is stored in cents. */
public class RestaurantStatistics {
    private int revenueCents;
    private int paidSatisfactionTotal;
    private int servedCustomerCount;
    private int paidCustomerCount;
    private int unhappyDepartureCustomerCount;

    public RestaurantStatistics() {
    }

    public void recordServed() {
        servedCustomerCount = Math.addExact(servedCustomerCount, 1);
    }

    public void recordPayment(int cents, int satisfaction) {
        if (cents < 0) {
            throw new IllegalArgumentException("Payment amount cannot be negative.");
        }
        if (satisfaction < 0 || satisfaction > 100) {
            throw new IllegalArgumentException("Satisfaction must be between 0 and 100.");
        }
        revenueCents = Math.addExact(revenueCents, cents);
        paidSatisfactionTotal = Math.addExact(paidSatisfactionTotal, satisfaction);
        paidCustomerCount = Math.addExact(paidCustomerCount, 1);
    }

    public void recordDeparture() {
        unhappyDepartureCustomerCount = Math.addExact(unhappyDepartureCustomerCount, 1);
    }

    /** Returns 0.0 if no customers have paid; callers can display N/A in that case. */
    public double getAverageSatisfaction() {
        if (paidCustomerCount == 0) {
            return -1;
        }
        return (double) paidSatisfactionTotal / paidCustomerCount;
    }

    public int getRevenueCents() {
        return revenueCents;
    }

    public int getServedCustomerCount() {
        return servedCustomerCount;
    }

    public int getPaidCustomerCount() {
        return paidCustomerCount;
    }

    public int getUnhappyDepartureCustomerCount() {
        return unhappyDepartureCustomerCount;
    }
}