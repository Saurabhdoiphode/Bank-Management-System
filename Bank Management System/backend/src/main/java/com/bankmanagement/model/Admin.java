package com.bankmanagement.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

/**
 * Admin Entity for MongoDB
 * Stores admin user information
 */
@Document(collection = "admins")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Admin {

    @Id
    private String id;

    private String name;

    private String email;

    private String password; // Hashed password

    private String role; // SUPER_ADMIN, ADMIN, MANAGER

    private String status; // ACTIVE, INACTIVE

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime lastLogin;

    private Boolean active;

    // Default Admin Constructor
    public Admin(String name, String email, String password, String role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.status = "ACTIVE";
        this.active = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}
