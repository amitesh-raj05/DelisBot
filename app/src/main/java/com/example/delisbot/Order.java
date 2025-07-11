package com.example.delisbot;

public class Order {
    private String imageUrl;  // URL for the image associated with the order
    private String title;     // Title of the order item
    private String price;     // Price of the order item
    private int quantity;     // Quantity of the order item

    // Constructor to initialize an order with image URL, title, price, and quantity
    public Order(String imageUrl, String title, String price, int quantity) {
        this.imageUrl = imageUrl;
        this.title = title;
        this.price = price;
        this.quantity = quantity;
    }

    // Getter for image URL
    public String getImageUrl() {
        return imageUrl;
    }

    // Setter for image URL
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    // Getter for title
    public String getTitle() {
        return title;
    }

    // Setter for title
    public void setTitle(String title) {
        this.title = title;
    }

    // Getter for price
    public String getPrice() {
        return price;
    }

    // Setter for price
    public void setPrice(String price) {
        this.price = price;
    }

    // Getter for quantity
    public int getQuantity() {
        return quantity;
    }

    // Setter for quantity
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
