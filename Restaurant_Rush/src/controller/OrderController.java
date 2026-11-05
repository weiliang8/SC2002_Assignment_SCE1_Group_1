package controller;

import entity.Order;
import entity.OrderStatus;

import java.util.Queue;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
public class OrderController {
    private int totalOrderCount = 0;
    private Queue<Order> pendingOrders = new LinkedList<>();
    private Queue<Order> readyOrders = new LinkedList<>();

    // Customer places an order
    public void addOrder(Order order) {
        if (order != null) {
            pendingOrders.offer(order);
            totalOrderCount++;
        }
    }

    
public List<Order> getPendingOrders() {
    List<Order> result = new ArrayList<>();

    for (Order order : pendingOrders) {
        if (order.canBePrepared()) {
            result.add(order);
        }
    }

    return result;
}

public Order getOrderById(int orderId) {
    for (Order order : pendingOrders) {
        if (order.getOrderId() == orderId
                && order.canBePrepared()) {
            return order;
        }
    }

    return null;
}


    // Chef finishes preparing an order
    
public void markOrderReady(Order order) {

    if (order != null && order.isReady()) {

        pendingOrders.remove(order);

        if (!readyOrders.contains(order)) {
            readyOrders.offer(order);
        }
    }
}


    // Waiter takes the earliest ready order
    public Queue<Order> getAllReadyOrder() {
        return readyOrders;
    }

    public int getTotalOrderCount(){
        return totalOrderCount;
    }

    public void markOrderServed(Order order) {
        if (order != null) {
            order.markServed();
        }
    }

    public void removeCancelledOrders() {
    pendingOrders.removeIf(order -> order.getStatus() == OrderStatus.CANCELLED);
    readyOrders.removeIf(order -> order.getStatus() == OrderStatus.CANCELLED);
}
}
