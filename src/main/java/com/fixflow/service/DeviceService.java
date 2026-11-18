package com.fixflow.service;

import com.fixflow.model.Customer;
import com.fixflow.model.Device;
import com.fixflow.repository.CustomerRepository;
import com.fixflow.repository.DeviceRepository;
import com.fixflow.util.ValidationUtil;

import java.util.List;

// Service layer handling Device business logic, customer link validation, and transients
public class DeviceService {

    // Repositories for device and customer records
    private static DeviceRepository deviceRepo = new DeviceRepository();
    private static CustomerRepository customerRepo = new CustomerRepository();

    // Private constructor to prevent direct instantiation
    private DeviceService() {
    }

    // Set custom repositories for dependency injection during unit testing
    public static void setRepositories(DeviceRepository dr, CustomerRepository cr) {
        deviceRepo = dr;
        customerRepo = cr;
    }

    // Get the active device repository
    public static DeviceRepository getDeviceRepository() {
        return deviceRepo;
    }

    // Validate inputs and register a new device for an existing customer
    public static int createDevice(Device device) {
        if (device == null) {
            throw new IllegalArgumentException("Device cannot be null.");
        }
        // Brand must not be empty
        if (!ValidationUtil.isNotEmpty(device.getBrand())) {
            throw new IllegalArgumentException("Device brand is required.");
        }
        // Model must not be empty
        if (!ValidationUtil.isNotEmpty(device.getModel())) {
            throw new IllegalArgumentException("Device model is required.");
        }

        // Referential integrity check: customer must exist in database
        Customer customer = customerRepo.findById(device.getCustomerId());
        if (customer == null) {
            throw new IllegalArgumentException("Referenced customer #" + device.getCustomerId() + " does not exist.");
        }

        deviceRepo.add(device);
        return device.getDeviceId();
    }

    // Get device by ID and populate transient customer details
    public static Device getDeviceById(int deviceId) {
        Device device = deviceRepo.findById(deviceId);
        if (device != null) {
            populateTransientCustomerDetails(device);
        }
        return device;
    }

    // Get all registered devices with populated customer details
    public static List<Device> getAllDevices() {
        List<Device> list = deviceRepo.findAll();
        for (Device d : list) {
            populateTransientCustomerDetails(d);
        }
        return list;
    }

    // Get all devices belonging to a specific customer ID
    public static List<Device> getDevicesByCustomerId(int customerId) {
        List<Device> list = deviceRepo.findByCustomerId(customerId);
        for (Device d : list) {
            populateTransientCustomerDetails(d);
        }
        return list;
    }

    // Search devices by keyword and populate customer details
    public static List<Device> searchDevices(String query) {
        List<Device> list = deviceRepo.search(query);
        for (Device d : list) {
            populateTransientCustomerDetails(d);
        }
        return list;
    }

    // Validate and update device details
    public static boolean updateDevice(Device device) {
        if (device == null || device.getDeviceId() <= 0) {
            throw new IllegalArgumentException("Valid device ID is required.");
        }
        Device existing = deviceRepo.findById(device.getDeviceId());
        if (existing == null) {
            return false;
        }

        // Apply updated device fields
        existing.setCategory(device.getCategory());
        existing.setBrand(device.getBrand());
        existing.setModel(device.getModel());
        existing.setSerialNumber(device.getSerialNumber());
        existing.setColour(device.getColour());
        existing.setAccessories(device.getAccessories());
        existing.setWarrantyMonths(device.getWarrantyMonths());
        existing.setWarrantyStartDate(device.getWarrantyStartDate());
        existing.setWarrantyExpiryDate(device.getWarrantyExpiryDate());

        deviceRepo.update(existing);
        return true;
    }

    // Delete a device by ID
    public static boolean deleteDevice(int deviceId) {
        Device existing = deviceRepo.findById(deviceId);
        if (existing == null) {
            return false;
        }
        return deviceRepo.delete(deviceId);
    }

    // Helper method to join customer name and phone into device transient fields
    private static void populateTransientCustomerDetails(Device device) {
        Customer c = customerRepo.findById(device.getCustomerId());
        if (c != null) {
            device.setCustomerName(c.getFullName());
            device.setCustomerPhone(c.getPhone());
        }
    }
}
