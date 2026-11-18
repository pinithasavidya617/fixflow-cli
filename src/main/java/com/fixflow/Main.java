package com.fixflow;

import com.fixflow.cli.InputHandler;
import com.fixflow.cli.MainMenu;
import com.fixflow.service.*;
import com.fixflow.util.DataSeeder;

public class Main {

    public static void main(String[] args) {
        DataSeeder.seedIfEmpty(
                UserService.getUserRepository(),
                CustomerService.getCustomerRepository(),
                DeviceService.getDeviceRepository(),
                TechnicianService.getTechnicianRepository(),
                InventoryService.getInventoryRepository(),
                RepairService.getRepairRepository(),
                InventoryService.getPartUsageRepository(),
                PaymentService.getPaymentRepository()
        );

        InputHandler inputHandler = new InputHandler();
        MainMenu mainMenu = new MainMenu(inputHandler);
        mainMenu.start();
    }
}
