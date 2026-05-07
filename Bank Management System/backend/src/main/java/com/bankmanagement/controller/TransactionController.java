package com.bankmanagement.controller;

import com.bankmanagement.dto.DepositRequest;
import com.bankmanagement.dto.TransferRequest;
import com.bankmanagement.dto.WithdrawRequest;
import com.bankmanagement.model.Transaction;
import com.bankmanagement.service.JwtService;
import com.bankmanagement.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Transaction Controller
 * Handles all transaction-related endpoints
 */
@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class TransactionController {

    private final TransactionService transactionService;
    private final JwtService jwtService;

    /**
     * Deposit money
     * @param authorization - Bearer token
     * @param request - deposit request
     * @return Transaction object
     */
    @PostMapping("/deposit")
    public ResponseEntity<?> deposit(
            @RequestHeader("Authorization") String authorization,
            @RequestBody DepositRequest request) {
        String token = extractToken(authorization);
        String customerId = jwtService.extractUserId(token);

        Transaction transaction = transactionService.deposit(customerId, request);

        if (transaction != null) {
            return ResponseEntity.ok(transaction);
        } else {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Deposit failed");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * Withdraw money
     * @param authorization - Bearer token
     * @param request - withdraw request
     * @return Transaction object
     */
    @PostMapping("/withdraw")
    public ResponseEntity<?> withdraw(
            @RequestHeader("Authorization") String authorization,
            @RequestBody WithdrawRequest request) {
        String token = extractToken(authorization);
        String customerId = jwtService.extractUserId(token);

        Transaction transaction = transactionService.withdraw(customerId, request);

        if (transaction != null) {
            return ResponseEntity.ok(transaction);
        } else {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Insufficient balance");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * Transfer money
     * @param authorization - Bearer token
     * @param request - transfer request
     * @return Transaction object
     */
    @PostMapping("/transfer")
    public ResponseEntity<?> transfer(
            @RequestHeader("Authorization") String authorization,
            @RequestBody TransferRequest request) {
        String token = extractToken(authorization);
        String customerId = jwtService.extractUserId(token);

        Transaction transaction = transactionService.transfer(customerId, request);

        if (transaction != null) {
            return ResponseEntity.ok(transaction);
        } else {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Transfer failed. Check recipient account or balance");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * Get recent transactions
     * @param authorization - Bearer token
     * @param limit - number of recent transactions (default: 10)
     * @return List of recent transactions
     */
    @GetMapping("/recent")
    public ResponseEntity<List<Transaction>> getRecentTransactions(
            @RequestHeader("Authorization") String authorization,
            @RequestParam(defaultValue = "10") int limit) {
        String token = extractToken(authorization);
        String customerId = jwtService.extractUserId(token);

        List<Transaction> transactions = transactionService.getRecentTransactions(customerId, limit);
        return ResponseEntity.ok(transactions);
    }

    /**
     * Get all transactions for customer
     * @param authorization - Bearer token
     * @return List of transactions
     */
    @GetMapping("/all")
    public ResponseEntity<List<Transaction>> getAllTransactions(
            @RequestHeader("Authorization") String authorization) {
        String token = extractToken(authorization);
        String customerId = jwtService.extractUserId(token);

        List<Transaction> transactions = transactionService.getCustomerTransactions(customerId);
        return ResponseEntity.ok(transactions);
    }

    /**
     * Get all transactions (admin only)
     * @return List of all transactions
     */
    @GetMapping("/admin/all")
    public ResponseEntity<List<Transaction>> getAllTransactionsAdmin() {
        List<Transaction> transactions = transactionService.getAllTransactions();
        return ResponseEntity.ok(transactions);
    }

    /**
     * Helper method to extract token from Authorization header
     * @param authorization - Authorization header
     * @return Token string
     */
    private String extractToken(String authorization) {
        return authorization.replace("Bearer ", "");
    }
}
