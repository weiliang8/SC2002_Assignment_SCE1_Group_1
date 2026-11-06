package entity;

// loses 8 satisfaction per unserved waiting turn
public class RegularCustomer extends Customer {
    public RegularCustomer(int id) {
        super(id,CustomerType.REGULAR, 8);
    }
}
