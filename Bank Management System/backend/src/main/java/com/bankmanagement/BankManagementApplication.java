package com.bankmanagement;

import com.bankmanagement.service.AdminService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * Main Spring Boot Application Class
 */
@SpringBootApplication
public class BankManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(BankManagementApplication.class, args);
    }

    /**
     * Initialize default admin on application startup
     */
    @Bean
    public CommandLineRunner init(AdminService adminService) {
        return args -> {
            adminService.initializeDefaultAdmin();
            System.out.println("Application started successfully!");
        };
    }
}
