package com.example.zajecia6.model;

public class User {
    private String email;
    private String fullName;
    private String address;

    public User(String email, String fullName, String address) {
        this.email = email;
        this.fullName = fullName;
        this.address = address;
    }

    public User() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
