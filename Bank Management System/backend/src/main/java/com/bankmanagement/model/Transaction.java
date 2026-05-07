package com.bankmanagement.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

/**
 * Transaction Entity for MongoDB
 * Stores all bank transactions
 */
@Document(collection = "transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {

    @Id
    private String id;

    private String customerId;

    private String accountNumber;

    private String customerName;

    private String type; // DEPOSIT, WITHDRAW, TRANSFER, TRANSFER_RECEIVED

    private Double amount;

    private Double balanceBefore;

    private Double balanceAfter;

    private String description;

    private String status; // PENDING, COMPLETED, FAILED

    private String referenceId;

    private String method; // For deposits: UPI, NETBANKING, CARD
                           // For withdrawals: ATM, CHECK, BANK_TRANSFER

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // For transfers
    private String recipientAccountNumber;

    private String recipientName;

    // Constructor for transaction
    public Transaction(String customerId, String accountNumber, String customerName, String type,
                      Double amount, Double balanceBefore, Double balanceAfter,
                      String description, String method) {
        this.customerId = customerId;
        this.accountNumber = accountNumber;
        this.customerName = customerName;
        this.type = type;
        this.amount = amount;
        this.balanceBefore = balanceBefore;
        this.balanceAfter = balanceAfter;
        this.description = description;
        this.method = method;
        this.status = "COMPLETED";
        this.referenceId = generateReferenceId();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Generate unique reference ID
    private String generateReferenceId() {
        return "TXN" + System.currentTimeMillis();
    }
}
