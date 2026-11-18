package com.fixflow.repository;

import com.fixflow.model.Customer;
import com.fixflow.util.JsonFileHandler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

// Repository for customer data operations using JSON persistence (Generics: Repository<Customer>)
public class CustomerRepository implements Repository<Customer> {

    // Default JSON file path for customer records
    private static final String DEFAULT_FILE_PATH = "data/customers.json";

    // Formatter for customer registration timestamp
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // File path where customer records are stored
    private final String filePath;

    // In-memory collection of customers (Collections framework)
    private List<Customer> customers;

    // Default constructor using default file path
    public CustomerRepository() {
        this(DEFAULT_FILE_PATH);
    }

    // Constructor with custom file path (useful for testing)
    public CustomerRepository(String filePath) {
        this.filePath = filePath;
        this.customers = new ArrayList<>();
        load(); // Load records from JSON on startup
    }

    // Add a new customer to repository and save to JSON
    @Override
    public void add(Customer customer) {
        // Auto-assign next ID if not already set
        if (customer.getCustomerId() <= 0) {
            customer.setCustomerId(generateNextId());
        }
        // Set creation timestamp if empty
        if (customer.getCreatedAt() == null || customer.getCreatedAt().isEmpty()) {
            customer.setCreatedAt(LocalDateTime.now().format(DATE_FORMATTER));
        }
        customers.add(customer);
        save();
    }

    // Find customer by ID
    @Override
    public Customer findById(int id) {
        for (Customer c : customers) {
            if (c.getCustomerId() == id) {
                return c;
            }
        }
        return null;
    }

    // Return copy of all customers to protect internal list
    @Override
    public List<Customer> findAll() {
        return new ArrayList<>(customers);
    }

    // Update existing customer record by ID
    @Override
    public void update(Customer customer) {
        for (int i = 0; i < customers.size(); i++) {
            if (customers.get(i).getCustomerId() == customer.getCustomerId()) {
                customers.set(i, customer);
                save();
                return;
            }
        }
    }

    // Delete customer by ID and update JSON file
    @Override
    public boolean delete(int id) {
        for (int i = 0; i < customers.size(); i++) {
            if (customers.get(i).getCustomerId() == id) {
                customers.remove(i);
                save();
                return true;
            }
        }
        return false;
    }

    // Write customers list to JSON file
    @Override
    public void save() {
        JsonFileHandler.saveList(filePath, customers);
    }

    // Read customers list from JSON file into memory
    @Override
    public void load() {
        customers = JsonFileHandler.loadList(filePath, Customer.class);
        if (customers == null) {
            customers = new ArrayList<>();
        }
    }

    // Search customers by keyword matching name, phone, or email
    public List<Customer> search(String query) {
        List<Customer> results = new ArrayList<>();
        if (query == null || query.trim().isEmpty()) {
            return findAll();
        }

        String lowerQuery = query.trim().toLowerCase();
        for (Customer c : customers) {
            boolean matchesName = c.getFullName() != null && c.getFullName().toLowerCase().contains(lowerQuery);
            boolean matchesPhone = c.getPhone() != null && c.getPhone().toLowerCase().contains(lowerQuery);
            boolean matchesEmail = c.getEmail() != null && c.getEmail().toLowerCase().contains(lowerQuery);

            if (matchesName || matchesPhone || matchesEmail) {
                results.add(c);
            }
        }
        return results;
    }

    // Generate auto-incremented ID for the next customer
    private int generateNextId() {
        int maxId = 0;
        for (Customer c : customers) {
            if (c.getCustomerId() > maxId) {
                maxId = c.getCustomerId();
            }
        }
        return maxId + 1;
    }
}
