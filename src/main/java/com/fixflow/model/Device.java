package com.fixflow.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDate;
import java.util.Objects;

// Model class representing a Device registered for a customer
public class Device {

    // Private fields for encapsulation
    private int deviceId;
    private int customerId; // Foreign reference to owning Customer
    private String category;
    private String brand;
    private String model;
    private String serialNumber;
    private String colour;
    private String accessories;
    private Integer warrantyMonths = 0;
    private String warrantyStartDate;
    private String warrantyExpiryDate;
    private String createdAt;

    // Transient fields joined from customer table for CLI display (ignored in JSON)
    private String customerName;
    private String customerPhone;

    // Default constructor for JSON loading
    public Device() {
    }

    // Constructor for registering a basic device
    public Device(int customerId, String category, String brand, String model,
                  String serialNumber, String colour, String accessories) {
        this.customerId = customerId;
        this.category = category;
        this.brand = brand;
        this.model = model;
        this.serialNumber = serialNumber;
        this.colour = colour;
        this.accessories = accessories;
    }

    // Constructor for existing basic devices
    public Device(int deviceId, int customerId, String category, String brand, String model,
                  String serialNumber, String colour, String accessories, String createdAt) {
        this.deviceId = deviceId;
        this.customerId = customerId;
        this.category = category;
        this.brand = brand;
        this.model = model;
        this.serialNumber = serialNumber;
        this.colour = colour;
        this.accessories = accessories;
        this.createdAt = createdAt;
    }

    // Constructor with warranty details
    public Device(int deviceId, int customerId, String category, String brand, String model,
                  String serialNumber, String colour, String accessories, Integer warrantyMonths,
                  String warrantyStartDate, String warrantyExpiryDate, String createdAt) {
        this.deviceId = deviceId;
        this.customerId = customerId;
        this.category = category;
        this.brand = brand;
        this.model = model;
        this.serialNumber = serialNumber;
        this.colour = colour;
        this.accessories = accessories;
        this.warrantyMonths = (warrantyMonths != null) ? warrantyMonths : 0;
        this.warrantyStartDate = warrantyStartDate;
        this.warrantyExpiryDate = warrantyExpiryDate;
        this.createdAt = createdAt;
    }

    // Check if the device is currently under active warranty
    @JsonIgnore
    public boolean isUnderWarranty() {
        if (warrantyExpiryDate == null || warrantyExpiryDate.trim().isEmpty()) {
            return false;
        }
        try {
            return LocalDate.now().compareTo(LocalDate.parse(warrantyExpiryDate.trim())) <= 0;
        } catch (Exception e) {
            return false;
        }
    }

    // Get device ID
    public int getDeviceId() {
        return deviceId;
    }

    // Set device ID
    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    // Get customer ID
    public int getCustomerId() {
        return customerId;
    }

    // Set customer ID
    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    // Get device category
    public String getCategory() {
        return category;
    }

    // Set device category
    public void setCategory(String category) {
        this.category = category;
    }

    // Get brand
    public String getBrand() {
        return brand;
    }

    // Set brand
    public void setBrand(String brand) {
        this.brand = brand;
    }

    // Get model
    public String getModel() {
        return model;
    }

    // Set model
    public void setModel(String model) {
        this.model = model;
    }

    // Get serial number
    public String getSerialNumber() {
        return serialNumber;
    }

    // Set serial number
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    // Get colour
    public String getColour() {
        return colour;
    }

    // Set colour
    public void setColour(String colour) {
        this.colour = colour;
    }

    // Get accessories
    public String getAccessories() {
        return accessories;
    }

    // Set accessories
    public void setAccessories(String accessories) {
        this.accessories = accessories;
    }

    // Get warranty months
    public Integer getWarrantyMonths() {
        return warrantyMonths;
    }

    // Set warranty months
    public void setWarrantyMonths(Integer warrantyMonths) {
        this.warrantyMonths = (warrantyMonths != null) ? warrantyMonths : 0;
    }

    // Get warranty start date
    public String getWarrantyStartDate() {
        return warrantyStartDate;
    }

    // Set warranty start date
    public void setWarrantyStartDate(String warrantyStartDate) {
        this.warrantyStartDate = warrantyStartDate;
    }

    // Get warranty expiry date
    public String getWarrantyExpiryDate() {
        return warrantyExpiryDate;
    }

    // Set warranty expiry date
    public void setWarrantyExpiryDate(String warrantyExpiryDate) {
        this.warrantyExpiryDate = warrantyExpiryDate;
    }

    // Get creation date
    public String getCreatedAt() {
        return createdAt;
    }

    // Set creation date
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    // Get joined customer name for display
    @JsonIgnore
    public String getCustomerName() {
        return customerName;
    }

    // Set joined customer name
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    // Get joined customer phone for display
    @JsonIgnore
    public String getCustomerPhone() {
        return customerPhone;
    }

    // Set joined customer phone
    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    // Check equality based on device ID
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Device device = (Device) o;
        return deviceId == device.deviceId;
    }

    // Generate hash code using device ID
    @Override
    public int hashCode() {
        return Objects.hash(deviceId);
    }

    // Return string summary of the device
    @Override
    public String toString() {
        return brand + " " + model + (serialNumber != null && !serialNumber.isEmpty() ? " (SN: " + serialNumber + ")" : "");
    }
}
