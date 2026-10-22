package entity;

/** Stage 1 customer: loses 8 satisfaction per unserved waiting turn. */
public class RegularCustomer extends Customer {
    public RegularCustomer(int id) {
        super(id, 8);
    }
}
