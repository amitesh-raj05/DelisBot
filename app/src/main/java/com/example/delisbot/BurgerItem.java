package com.example.delisbot;

public class BurgerItem {
    private String title;
    private String description;
    private double price;
    private String imageUrl; // Store image URL
    private int counter;

    // Constructor to initialize the BurgerItem
    public BurgerItem(String title, String description, double price, String imageUrl) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
        this.counter = 0; // Initialize counter to 0
    }

    // Getter methods
    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public int getCounter() {
        return counter;
    }

    // Setter method for counter
    public void setCounter(int counter) {
        this.counter = counter;
    }

    // Optional: Method to increment the counter
    public void incrementCounter() {
        this.counter++;
    }

    // Optional: Method to decrement the counter
    public void decrementCounter() {
        if (this.counter > 0) {
            this.counter--;
        }
    }
}
