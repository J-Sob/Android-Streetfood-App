package com.example.zajecia6.model;

import com.google.firebase.firestore.DocumentReference;

import java.util.List;

public class OrderModel {
    private String id;
    private String userId;
    private String phoneNumber;
    private String address;
    private String additionalInfo;
    private List<String> items;
    private OrderStatus orderStatus;
    private double totalPrice;

    public OrderModel() {}

    public OrderModel(String id,
                      String userId,
                      String phoneNumber,
                      String address,
                      String additionalInfo,
                      List<String> items,
                      OrderStatus orderStatus,
                      double totalPrice) {
        this.id = id;
        this.userId = userId;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.additionalInfo = additionalInfo;
        this.items = items;
        this.orderStatus = orderStatus;
        this.totalPrice = totalPrice;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public List<String> getItems() {
        return items;
    }

    public void setItems(List<String> items) {
        this.items = items;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
}
