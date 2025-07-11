package com.example.delisbot;


import java.util.ArrayList;
import java.util.List;

public class Customer {
    private String id;
    private String name;          // Customer's name
    private String email;         // Customer's email address
    private String tableNo;       // Customer's table number
    private String status;        // Customer's status (Waiting/Serviced)
    private List<OrderItem> orders; // List of items in the customer's order

    // Constructor to initialize the customer with basic details and an empty order list
    public Customer(String id,String name, String email, String tableNo, String status) {
        this.name = name;
        this.id = id;
        this.email = email;
        this.tableNo = tableNo;
        this.status = status;
        this.orders = new ArrayList<>(); // Initialize the order list
    }

    // Getter and setter methods
    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getEmail() {
        return email;
    }

    public String getTableNo() {
        return tableNo;
    }

    public String getStatus() {
        return status;
    }

    public List<OrderItem> getOrders() {
        return orders;
    }

    public void setOrders(List<OrderItem> orders) {
        this.orders = orders;
    }
}
