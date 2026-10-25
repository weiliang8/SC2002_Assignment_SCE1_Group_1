package entity;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Queue;
import java.util.Set;

/** Owns the restaurant's customers, queue, two tables, menu and statistics. */
public class Restaurant {
    private static final int TABLE_COUNT = 2;

    private final Queue<Customer> waitingQueue;
    private final List<Customer> customers; // stored all customer in
    private final List<Table> tables;
    private final Menu menu;
    private final RestaurantStatistics statistics;
    private final Set<Customer> servedCustomers;
    private final Set<Customer> paidCustomers;
    private final Set<Customer> departedCustomers;

    public Restaurant() {
        waitingQueue = new ArrayDeque<>();
        customers = new ArrayList<>();
        tables = new ArrayList<>();
        for (int id = 1; id <= TABLE_COUNT; id++) {
            tables.add(new Table(id));
        }
        menu = createBaseMenu();
        statistics = new RestaurantStatistics();
        servedCustomers = identitySet();
        paidCustomers = identitySet();
        departedCustomers = identitySet();
    }

    private static Menu createBaseMenu() {
        Menu baseMenu = new Menu();
        baseMenu.addItem(new Dish("Salad", 900, 1));
        baseMenu.addItem(new Dish("Burger", 1500, 1));
        baseMenu.addItem(new Dish("Pasta", 1800, 2));
        return baseMenu;
    }

    private static Set<Customer> identitySet() {
        return Collections.newSetFromMap(new IdentityHashMap<Customer, Boolean>());
    }

    /** Adds a newly arrived customer to the permanent records and waiting queue. */
    public boolean addCustomer(Customer customer) {
        if (customer == null) {
            throw new IllegalArgumentException("Customer cannot be null.");
        }
        for (Customer existing : customers) {
            if (existing == customer || existing.getCustomerId() == customer.getCustomerId()) {
                throw new IllegalArgumentException("Customer is already registered: " + customer.getCustomerId());
            }
        }
        customers.add(customer);
        waitingQueue.offer(customer);
        return true;
    }

    /** Removes a customer from the queue without deleting their history. */
    public boolean removeCustomerFromQueue(Customer customer) {
        if (customer == null) {
            throw new IllegalArgumentException("Customer cannot be null.");
        }
        return waitingQueue.remove(customer);
    }

    /**
     * Returns the earliest registered customer who is eligible to pay this turn.
     */
    public Customer getOldestReadyPayer(int turn) {
        Customer oldest = null;

        for (Customer customer : customers) {

            if (!customer.canPay(turn)
                    || paidCustomers.contains(customer)) {
                continue;
            }

            if (oldest == null || customer.getReadyToPayTurn() < oldest.getReadyToPayTurn()) {
                oldest = customer;
            }
        }
        return oldest;

    }

    public List<Customer> getCustomers() {
        return Collections.unmodifiableList(customers);
    }

    public List<Table> getTables() {
        return Collections.unmodifiableList(tables);
    }

    public Menu getMenu() {
        return menu;
    }

    public RestaurantStatistics getStatistics() {
        return statistics;
    }

    public int getWaitingQueueSize(){
        return waitingQueue.size();
    }

    public boolean hasFreeTable(){
        int no_of_table_seats_avaliable = 2;
        for(Table t: tables){
            if(!t.isFree()){
                no_of_table_seats_avaliable -= 1;
            }
            if(no_of_table_seats_avaliable == 0){
                return false;
            }
        }
        return true;
    }

    public boolean hasSeatedWithoutOrder(){
        for(Customer c : customers){
            if(c.getStatus() == CustomerStatus.SEATED && c.getOrder() == null){
                return true;
            }
        }
        return false;
    }

    public boolean hasReadyOrder(){
        for(Customer c: customers){
            Order o = c.getOrder();
            if(c.getStatus() == CustomerStatus.SEATED && o != null && o.getStatus() == OrderStatus.READY){
                return true;
            }
        }
        return false;
    }

    public boolean hasOrderToPrepare(){
        for(Customer c: customers){
            Order o = c.getOrder();
            if(o != null && (o.getStatus() == OrderStatus.ORDERED || o.getStatus() == OrderStatus.PREPARING)){
                return true;
            }
        }
        return false;
    }

    /** Records a customer's first successful serving event. */
    public void recordServed(Customer customer) {
        requireRegistered(customer);
        if (servedCustomers.add(customer)) {
            statistics.recordServed();
        }
    }

    /**
     * Records a payment once and snapshots the customer's satisfaction at payment.
     */
    public void recordPayment(Customer customer, int cents) {
        requireRegistered(customer);
        if (cents < 0) {
            throw new IllegalArgumentException("Payment amount cannot be negative.");
        }
        if (paidCustomers.contains(customer)) {
            throw new IllegalStateException("Customer " + customer.getCustomerId() + " has already paid.");
        }
        if (customer.getStatus() != CustomerStatus.PAID) {
            throw new IllegalStateException("Payment can only be recorded after the customer is marked PAID.");
        }
        Order order = customer.getOrder();
        if (order == null || order.getFinalPriceCents() != cents) {
            throw new IllegalArgumentException("Payment must match the customer's order total.");
        }
        int satisfaction = customer.getSatisfaction();
        statistics.recordPayment(cents, satisfaction);
        paidCustomers.add(customer);
    }

    /**
     * Records an unhappy departure once and removes the customer from the queue.
     */
    public void recordDeparture(Customer customer) {
        requireRegistered(customer);
        removeCustomerFromQueue(customer);
        if (departedCustomers.add(customer)) {
            statistics.recordDeparture();
        }
    }

    private void requireRegistered(Customer customer) {
        if (customer == null) {
            throw new IllegalArgumentException("Customer cannot be null.");
        }
        for (Customer existing : customers) {
            if (existing == customer) {
                return;
            }
        }
        throw new IllegalArgumentException("Customer is not registered with this restaurant.");
    }

    // public Customer getfirstWaitingCustomer() {
    // Customer dequeued_customer = waitingQueue.peek();
    // waitingQueue.remove();
    // return dequeued_customer;
    // }

    // public Customer getFirstSeatedCustomer() {
    // for (Customer customer : customers) {
    // if (customer.getStatus() == CustomerStatus.SEATED
    // && customer.getOrder() == null) {
    // return customer;
    // }
    // }
    // return null;
    // }
}