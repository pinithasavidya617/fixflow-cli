package com.fixflow.model;

import java.util.Objects;

// Model class representing a Customer entity (Encapsulation)
public class Customer {

    // Private fields to protect data
    private int customerId;
    private String fullName;
    private String phone;
    private String email;
    private String address;
    private String createdAt;

    // Default constructor for JSON deserialization
    public Customer() {
    }

    // Constructor for creating a new customer before ID is assigned
    public Customer(String fullName, String phone, String email, String address) {
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.address = address;
    }

    // Full constructor for existing customer records
    public Customer(int customerId, String fullName, String phone, String email, String address, String createdAt) {
        this.customerId = customerId;
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.createdAt = createdAt;
    }

    // Get customer ID
    public int getCustomerId() {
        return customerId;
    }

    // Set customer ID
    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    // Get full name
    public String getFullName() {
        return fullName;
    }

    // Set full name
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    // Get phone number
    public String getPhone() {
        return phone;
    }

    // Set phone number
    public void setPhone(String phone) {
        this.phone = phone;
    }

    // Get email
    public String getEmail() {
        return email;
    }

    // Set email
    public void setEmail(String email) {
        this.email = email;
    }

    // Get address
    public String getAddress() {
        return address;
    }

    // Set address
    public void setAddress(String address) {
        this.address = address;
    }

    // Get creation timestamp
    public String getCreatedAt() {
        return createdAt;
    }

    // Set creation timestamp
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    // Check equality based on customer ID
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return customerId == customer.customerId;
    }

    // Generate hash code using customer ID
    @Override
    public int hashCode() {
        return Objects.hash(customerId);
    }

    // Return simple string summary of customer
    @Override
    public String toString() {
        return fullName + " (ID: #" + customerId + ", Tel: " + phone + ")";
    }
}
