package com.bankmanagement.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

/**
 * Customer Entity for MongoDB
 * Stores customer account information
 */
@Document(collection = "customers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {

    @Id
    private String id;

    private String fullName;

    private String email;

    private String password; // Hashed password

    private String mobileNumber;

    private String address;

    private String aadhaarNumber;

    private String panNumber;

    private String accountType; // SAVINGS, CURRENT, STUDENT

    private Double balance;

    private String accountNumber; // Unique account number

    private String status; // ACTIVE, INACTIVE, BLOCKED

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime lastLogin;

    private Boolean active;

    // Constructor for creating new customer
    public Customer(String fullName, String email, String password, String mobileNumber,
                   String address, String aadhaarNumber, String panNumber, String accountType) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.mobileNumber = mobileNumber;
        this.address = address;
        this.aadhaarNumber = aadhaarNumber;
        this.panNumber = panNumber;
        this.accountType = accountType;
        this.balance = 0.0;
        this.status = "ACTIVE";
        this.active = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}
