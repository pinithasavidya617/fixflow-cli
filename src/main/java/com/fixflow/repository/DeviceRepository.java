package com.fixflow.repository;

import com.fixflow.model.Device;
import com.fixflow.util.JsonFileHandler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

// Repository for device data operations using JSON persistence (Generics: Repository<Device>)
public class DeviceRepository implements Repository<Device> {

    // Default JSON file path for device records
    private static final String DEFAULT_FILE_PATH = "data/devices.json";

    // Formatter for device registration timestamp
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // File path where device records are stored
    private final String filePath;

    // In-memory collection of devices (Collections framework)
    private List<Device> devices;

    // Default constructor using default file path
    public DeviceRepository() {
        this(DEFAULT_FILE_PATH);
    }

    // Constructor with custom file path (useful for testing)
    public DeviceRepository(String filePath) {
        this.filePath = filePath;
        this.devices = new ArrayList<>();
        load(); // Load records from JSON on startup
    }

    // Add a new device to repository and save to JSON
    @Override
    public void add(Device device) {
        // Auto-assign next ID if not already set
        if (device.getDeviceId() <= 0) {
            device.setDeviceId(generateNextId());
        }
        // Set creation timestamp if empty
        if (device.getCreatedAt() == null || device.getCreatedAt().isEmpty()) {
            device.setCreatedAt(LocalDateTime.now().format(DATE_FORMATTER));
        }
        devices.add(device);
        save();
    }

    // Find device by ID
    @Override
    public Device findById(int id) {
        for (Device d : devices) {
            if (d.getDeviceId() == id) {
                return d;
            }
        }
        return null;
    }

    // Return copy of all devices to protect internal list
    @Override
    public List<Device> findAll() {
        return new ArrayList<>(devices);
    }

    // Update existing device record by ID
    @Override
    public void update(Device device) {
        for (int i = 0; i < devices.size(); i++) {
            if (devices.get(i).getDeviceId() == device.getDeviceId()) {
                devices.set(i, device);
                save();
                return;
            }
        }
    }

    // Delete device by ID and update JSON file
    @Override
    public boolean delete(int id) {
        for (int i = 0; i < devices.size(); i++) {
            if (devices.get(i).getDeviceId() == id) {
                devices.remove(i);
                save();
                return true;
            }
        }
        return false;
    }

    // Write devices list to JSON file
    @Override
    public void save() {
        JsonFileHandler.saveList(filePath, devices);
    }

    // Read devices list from JSON file into memory
    @Override
    public void load() {
        devices = JsonFileHandler.loadList(filePath, Device.class);
        if (devices == null) {
            devices = new ArrayList<>();
        }
    }

    // Find all devices owned by a specific customer ID
    public List<Device> findByCustomerId(int customerId) {
        List<Device> results = new ArrayList<>();
        for (Device d : devices) {
            if (d.getCustomerId() == customerId) {
                results.add(d);
            }
        }
        return results;
    }

    // Search devices by keyword matching brand, model, serial, or category
    public List<Device> search(String query) {
        List<Device> results = new ArrayList<>();
        if (query == null || query.trim().isEmpty()) {
            return findAll();
        }

        String lowerQuery = query.trim().toLowerCase();
        for (Device d : devices) {
            boolean matchesBrand = d.getBrand() != null && d.getBrand().toLowerCase().contains(lowerQuery);
            boolean matchesModel = d.getModel() != null && d.getModel().toLowerCase().contains(lowerQuery);
            boolean matchesSerial = d.getSerialNumber() != null && d.getSerialNumber().toLowerCase().contains(lowerQuery);
            boolean matchesCategory = d.getCategory() != null && d.getCategory().toLowerCase().contains(lowerQuery);

            if (matchesBrand || matchesModel || matchesSerial || matchesCategory) {
                results.add(d);
            }
        }
        return results;
    }

    // Generate auto-incremented ID for the next device
    private int generateNextId() {
        int maxId = 0;
        for (Device d : devices) {
            if (d.getDeviceId() > maxId) {
                maxId = d.getDeviceId();
            }
        }
        return maxId + 1;
    }
}
