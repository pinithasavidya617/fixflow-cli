package com.fixflow.service;

import java.io.File;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.fixflow.model.Customer;
import com.fixflow.model.Device;
import com.fixflow.repository.CustomerRepository;
import com.fixflow.repository.DeviceRepository;

// Automated unit tests for CustomerService and customer-device relationship
public class CustomerServiceTest {

    // Temporary directory for isolated test data
    private File tempDir;

    // Set up fresh isolated repository instances before each test
    @BeforeEach
    void setUp() throws Exception {
        tempDir = File.createTempFile("fixflow_custtest_", "_dir");
        tempDir.delete();
        tempDir.mkdirs();

        // Initialize test repositories pointing to temporary files
        CustomerRepository cr = new CustomerRepository(new File(tempDir, "customers.json").getAbsolutePath());
        DeviceRepository dr = new DeviceRepository(new File(tempDir, "devices.json").getAbsolutePath());

        // Inject test repositories into services
        CustomerService.setRepositories(cr, dr);
        DeviceService.setRepositories(dr, cr);
    }

    // Clean up temporary test files after each test execution
    @AfterEach
    void tearDown() {
        if (tempDir != null && tempDir.exists()) {
            File[] files = tempDir.listFiles();
            if (files != null) {
                for (File f : files) f.delete();
            }
            tempDir.delete();
        }
    }

    // Verify successful registration of customers and keyword search
    @Test
    @DisplayName("Verify customer registration and search")
    void testCustomerRegistrationAndSearch() {
        // Register customer 1
        Customer c1 = new Customer("Kasun Jayawardena", "0771122334", "kasun@test.com", "Colombo");
        int id1 = CustomerService.createCustomer(c1);
        assertTrue(id1 > 0);

        // Register customer 2
        Customer c2 = new Customer("Dilani Perera", "0785566778", "dilani@test.com", "Kandy");
        int id2 = CustomerService.createCustomer(c2);
        assertTrue(id2 > 0);

        // Search by keyword "Kasun"
        List<Customer> searchResults = CustomerService.searchCustomers("Kasun");
        assertEquals(1, searchResults.size());
        assertEquals("Kasun Jayawardena", searchResults.get(0).getFullName());
    }

    // Verify that phone number validation rejects invalid formats
    @Test
    @DisplayName("Verify customer validation rejects invalid phone numbers")
    void testInvalidCustomerPhone() {
        Customer c = new Customer("Invalid Phone", "abc", "test@test.com", "Galle");
        assertThrows(IllegalArgumentException.class, () -> CustomerService.createCustomer(c));
    }

    // Verify referential integrity: customer with registered devices cannot be deleted
    @Test
    @DisplayName("Verify customer cannot be deleted if associated with devices")
    void testDeleteCustomerBlockedWithDevices() {
        // Register a customer
        Customer c = new Customer("Nuwan Silva", "0712345678", "nuwan@test.com", "Matara");
        int custId = CustomerService.createCustomer(c);

        // Register a device for this customer
        Device d = new Device(custId, "Smartphone", "Apple", "iPhone 12", "SN-9988", "Blue", "None");
        DeviceService.createDevice(d);

        // Expect IllegalStateException when attempting deletion
        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                CustomerService.deleteCustomer(custId)
        );
        assertTrue(ex.getMessage().contains("device"));
    }
}
