package com.example.zajecia6.model;

public class MenuItem {
    private String id;
    private String name;
    private String imageRef;
    private double price;
    private boolean available;
    private MenuItemCategory category;

    public MenuItem() {}

    public MenuItem(String id, String name, double price, boolean available, MenuItemCategory category) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.available = available;
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public MenuItemCategory getCategory() {
        return category;
    }

    public void setCategory(MenuItemCategory category) {
        this.category = category;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getImageRef() {
        return imageRef;
    }

    public void setImageRef(String imageRef) {
        this.imageRef = imageRef;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "MenuItem{" +
                "name='" + name + '\'' +
                '}';
    }
}
