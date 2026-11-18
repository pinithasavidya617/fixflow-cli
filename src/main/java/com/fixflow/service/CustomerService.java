package com.fixflow.service;

import com.fixflow.model.Customer;
import com.fixflow.model.Device;
import com.fixflow.repository.CustomerRepository;
import com.fixflow.repository.DeviceRepository;
import com.fixflow.util.ValidationUtil;

import java.util.List;

// Service layer handling Customer business logic, validation, and referential integrity
public class CustomerService {

    // Repositories for customer and device records
    private static CustomerRepository customerRepo = new CustomerRepository();
    private static DeviceRepository deviceRepo = new DeviceRepository();

    // Private constructor to prevent direct instantiation
    private CustomerService() {
    }

    // Set custom repositories for dependency injection during unit testing
    public static void setRepositories(CustomerRepository cr, DeviceRepository dr) {
        customerRepo = cr;
        deviceRepo = dr;
    }

    // Get the active customer repository
    public static CustomerRepository getCustomerRepository() {
        return customerRepo;
    }

    // Validate inputs and create a new customer
    public static int createCustomer(Customer customer) {
        if (customer == null) {
            throw new IllegalArgumentException("Customer cannot be null.");
        }
        // Name must not be empty
        if (!ValidationUtil.isNotEmpty(customer.getFullName())) {
            throw new IllegalArgumentException("Customer full name is required.");
        }
        // Phone must match valid format
        if (!ValidationUtil.isValidPhone(customer.getPhone())) {
            throw new IllegalArgumentException("Invalid phone number format: " + customer.getPhone());
        }
        // Email must be valid if entered
        if (!ValidationUtil.isValidOptionalEmail(customer.getEmail())) {
            throw new IllegalArgumentException("Invalid email address format: " + customer.getEmail());
        }

        customerRepo.add(customer);
        return customer.getCustomerId();
    }

    // Get customer by their ID
    public static Customer getCustomerById(int customerId) {
        return customerRepo.findById(customerId);
    }

    // Get all registered customers
    public static List<Customer> getAllCustomers() {
        return customerRepo.findAll();
    }

    // Search customers by keyword (name, phone, or email)
    public static List<Customer> searchCustomers(String query) {
        return customerRepo.search(query);
    }

    // Validate and update customer details
    public static boolean updateCustomer(Customer customer) {
        if (customer == null || customer.getCustomerId() <= 0) {
            throw new IllegalArgumentException("Valid customer ID is required.");
        }
        if (!ValidationUtil.isNotEmpty(customer.getFullName())) {
            throw new IllegalArgumentException("Customer full name is required.");
        }
        if (!ValidationUtil.isValidPhone(customer.getPhone())) {
            throw new IllegalArgumentException("Invalid phone number format: " + customer.getPhone());
        }
        if (!ValidationUtil.isValidOptionalEmail(customer.getEmail())) {
            throw new IllegalArgumentException("Invalid email address format: " + customer.getEmail());
        }

        Customer existing = customerRepo.findById(customer.getCustomerId());
        if (existing == null) {
            return false;
        }

        // Apply updated values
        existing.setFullName(customer.getFullName().trim());
        existing.setPhone(customer.getPhone().trim());
        existing.setEmail(customer.getEmail() != null ? customer.getEmail().trim() : "");
        existing.setAddress(customer.getAddress() != null ? customer.getAddress().trim() : "");

        customerRepo.update(existing);
        return true;
    }

    // Delete customer with referential integrity check (blocked if devices exist)
    public static boolean deleteCustomer(int customerId) {
        Customer existing = customerRepo.findById(customerId);
        if (existing == null) {
            return false;
        }

        // Referential integrity check: reject if customer has associated devices
        List<Device> devices = deviceRepo.findByCustomerId(customerId);
        if (!devices.isEmpty()) {
            throw new IllegalStateException("Cannot delete customer #" + customerId + ": Customer has " +
                    devices.size() + " associated device(s). Remove devices first.");
        }

        return customerRepo.delete(customerId);
    }
}
