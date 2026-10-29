package entity;

public class VIPCustomer extends Customer {
    public VIPCustomer(int id) {
        super(id, CustomerType.VIP, 5);
    }

    @Override
    public int applyDiscount(int priceCents) {
        return (int) Math.round(priceCents * 0.9);
    }
}
