package com.fixflow.cli;

import com.fixflow.model.Customer;
import com.fixflow.model.Device;
import com.fixflow.service.CustomerService;
import com.fixflow.service.DeviceService;

import java.util.List;

// CLI presentation menu for customer management operations
public class CustomerMenu {

    // Helper for reading validated console input
    private final InputHandler input;

    // Constructor receiving shared InputHandler
    public CustomerMenu(InputHandler input) {
        this.input = input;
    }

    // Display main customer menu loop
    public void display() {
        boolean back = false;
        while (!back) {
            System.out.println("\n========================================");
            System.out.println("          CUSTOMER MANAGEMENT           ");
            System.out.println("========================================");
            System.out.println("1. Register New Customer");
            System.out.println("2. List All Customers");
            System.out.println("3. Search Customers");
            System.out.println("4. View Customer Details");
            System.out.println("5. Update Customer Information");
            System.out.println("6. Delete Customer");
            System.out.println("0. Back to Main Menu");
            System.out.println("========================================");

            int choice = input.readInt("Enter choice: ", 0, 6);
            switch (choice) {
                case 1 -> registerCustomer();
                case 2 -> listCustomers();
                case 3 -> searchCustomers();
                case 4 -> viewCustomerDetails();
                case 5 -> updateCustomer();
                case 6 -> deleteCustomer();
                case 0 -> back = true;
            }
        }
    }

    // Register a new customer with input prompts
    private void registerCustomer() {
        System.out.println("\n--- Register New Customer ---");
        String name = input.readString("Enter Customer Full Name: ");
        String phone = input.readString("Enter Phone Number (e.g. 0771234567): ");
        String email = input.readOptionalString("Enter Email (Optional, press Enter to skip): ");
        String address = input.readOptionalString("Enter Address (Optional, press Enter to skip): ");

        try {
            Customer customer = new Customer(name, phone, email, address);
            int id = CustomerService.createCustomer(customer);
            System.out.println("\n[✓] Customer registered successfully with Customer ID #" + id);
        } catch (IllegalArgumentException e) {
            System.out.println("\n[!] Registration failed: " + e.getMessage());
        }
        input.pressEnterToContinue();
    }

    // List all registered customers in a formatted table
    private void listCustomers() {
        System.out.println("\n--- Registered Customers ---");
        List<Customer> list = CustomerService.getAllCustomers();
        if (list.isEmpty()) {
            System.out.println("No customer records found.");
        } else {
            printCustomerTable(list);
        }
        input.pressEnterToContinue();
    }

    // Search customers by keyword
    private void searchCustomers() {
        System.out.println("\n--- Search Customers ---");
        String query = input.readString("Enter search keyword (name, phone, or email): ");
        List<Customer> results = CustomerService.searchCustomers(query);
        if (results.isEmpty()) {
            System.out.println("No matching customers found for: '" + query + "'");
        } else {
            System.out.println("Found " + results.size() + " matching customer(s):");
            printCustomerTable(results);
        }
        input.pressEnterToContinue();
    }

    // View customer profile and all their registered devices
    private void viewCustomerDetails() {
        System.out.println("\n--- View Customer Details ---");
        int id = input.readInt("Enter Customer ID: ");
        Customer c = CustomerService.getCustomerById(id);
        if (c == null) {
            System.out.println("[!] Customer #" + id + " not found.");
            input.pressEnterToContinue();
            return;
        }

        // Display customer profile
        System.out.println("----------------------------------------");
        System.out.println("Customer ID : #" + c.getCustomerId());
        System.out.println("Full Name   : " + c.getFullName());
        System.out.println("Phone       : " + c.getPhone());
        System.out.println("Email       : " + (c.getEmail() != null && !c.getEmail().isEmpty() ? c.getEmail() : "(Not provided)"));
        System.out.println("Address     : " + (c.getAddress() != null && !c.getAddress().isEmpty() ? c.getAddress() : "(Not provided)"));
        System.out.println("Registered  : " + c.getCreatedAt());
        System.out.println("----------------------------------------");

        // Display devices owned by this customer
        List<Device> devices = DeviceService.getDevicesByCustomerId(id);
        System.out.println("Registered Devices (" + devices.size() + "):");
        if (devices.isEmpty()) {
            System.out.println("  No devices registered for this customer.");
        } else {
            for (Device d : devices) {
                String warranty = d.isUnderWarranty() ? "[Active Warranty]" : "[Out of Warranty]";
                System.out.printf("  - Device #%d: %s %s (%s) %s%n",
                        d.getDeviceId(), d.getBrand(), d.getModel(), d.getCategory(), warranty);
            }
        }
        input.pressEnterToContinue();
    }

    // Update existing customer contact details
    private void updateCustomer() {
        System.out.println("\n--- Update Customer ---");
        int id = input.readInt("Enter Customer ID to update: ");
        Customer c = CustomerService.getCustomerById(id);
        if (c == null) {
            System.out.println("[!] Customer #" + id + " not found.");
            input.pressEnterToContinue();
            return;
        }

        // Allow user to press Enter to keep current values
        System.out.println("Current Name: " + c.getFullName());
        String name = input.readOptionalString("New Full Name (press Enter to keep current): ");
        if (!name.isEmpty()) c.setFullName(name);

        System.out.println("Current Phone: " + c.getPhone());
        String phone = input.readOptionalString("New Phone Number (press Enter to keep current): ");
        if (!phone.isEmpty()) c.setPhone(phone);

        System.out.println("Current Email: " + c.getEmail());
        String email = input.readOptionalString("New Email (press Enter to keep current): ");
        if (!email.isEmpty()) c.setEmail(email);

        System.out.println("Current Address: " + c.getAddress());
        String address = input.readOptionalString("New Address (press Enter to keep current): ");
        if (!address.isEmpty()) c.setAddress(address);

        try {
            boolean updated = CustomerService.updateCustomer(c);
            if (updated) {
                System.out.println("\n[✓] Customer #" + id + " updated successfully.");
            } else {
                System.out.println("\n[!] Failed to update customer.");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("\n[!] Update error: " + e.getMessage());
        }
        input.pressEnterToContinue();
    }

    // Delete customer after confirmation and referential integrity check
    private void deleteCustomer() {
        System.out.println("\n--- Delete Customer ---");
        int id = input.readInt("Enter Customer ID to delete: ");
        Customer c = CustomerService.getCustomerById(id);
        if (c == null) {
            System.out.println("[!] Customer #" + id + " not found.");
            input.pressEnterToContinue();
            return;
        }

        // Ask for user confirmation before deletion
        boolean confirm = input.readBoolean("Are you sure you want to delete customer '" + c.getFullName() + "'?");
        if (confirm) {
            try {
                boolean deleted = CustomerService.deleteCustomer(id);
                if (deleted) {
                    System.out.println("\n[✓] Customer #" + id + " deleted successfully.");
                } else {
                    System.out.println("\n[!] Customer could not be deleted.");
                }
            } catch (IllegalStateException e) {
                // Catch referential integrity rejection (customer has devices)
                System.out.println("\n[!] Cannot delete customer: " + e.getMessage());
            }
        } else {
            System.out.println("Deletion cancelled.");
        }
        input.pressEnterToContinue();
    }

    // Format and print customer table
    private void printCustomerTable(List<Customer> list) {
        System.out.printf("%-6s | %-24s | %-14s | %-26s%n", "ID", "Full Name", "Phone", "Email");
        System.out.println("-------+--------------------------+----------------+---------------------------");
        for (Customer c : list) {
            String email = c.getEmail() != null ? c.getEmail() : "-";
            System.out.printf("#%-5d | %-24s | %-14s | %-26s%n",
                    c.getCustomerId(),
                    truncate(c.getFullName(), 24),
                    c.getPhone(),
                    truncate(email, 26));
        }
    }

    // Truncate long strings for neat table columns
    private String truncate(String str, int maxLen) {
        if (str == null) return "-";
        return str.length() <= maxLen ? str : str.substring(0, maxLen - 3) + "...";
    }
}
