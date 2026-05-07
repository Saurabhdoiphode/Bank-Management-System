package com.bankmanagement.controller;

import com.bankmanagement.model.Customer;
import com.bankmanagement.model.Transaction;
import com.bankmanagement.service.CustomerService;
import com.bankmanagement.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Admin Controller
 * Handles admin dashboard operations
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8080", "http://127.0.0.1:3000", "http://127.0.0.1:8080"}, maxAge = 3600)
public class AdminController {

    private final CustomerService customerService;
    private final TransactionService transactionService;

    // ==================== CUSTOMER ENDPOINTS ====================

    /**
     * Get all customers
     * @return List of customers
     */
    @GetMapping("/customers")
    public ResponseEntity<List<Customer>> getAllCustomers() {
        List<Customer> customers = customerService.getAllCustomers();
        return ResponseEntity.ok(customers);
    }

    /**
     * Search customers
     * @param search - search query
     * @return List of matching customers
     */
    @GetMapping("/customers/search")
    public ResponseEntity<List<Customer>> searchCustomers(@RequestParam String search) {
        List<Customer> customers = customerService.getAllCustomers()
                .stream()
                .filter(c -> c.getFullName().toLowerCase().contains(search.toLowerCase()) ||
                        c.getEmail().toLowerCase().contains(search.toLowerCase()))
                .toList();
        return ResponseEntity.ok(customers);
    }

    /**
     * Get customer by ID
     * @param customerId - customer ID
     * @return Customer object
     */
    @GetMapping("/customers/{customerId}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable String customerId) {
        Customer customer = customerService.getCustomerById(customerId);
        return customer != null ? 
                ResponseEntity.ok(customer) :
                ResponseEntity.notFound().build();
    }

    /**
     * Delete customer
     * @param customerId - customer ID
     * @return Success message
     */
    @DeleteMapping("/customers/{customerId}")
    public ResponseEntity<Map<String, String>> deleteCustomer(@PathVariable String customerId) {
        boolean deleted = customerService.deleteCustomer(customerId);
        Map<String, String> response = new HashMap<>();

        if (deleted) {
            response.put("message", "Customer deleted successfully");
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "Customer not found");
            return ResponseEntity.notFound().build();
        }
    }

    // ==================== TRANSACTION ENDPOINTS ====================

    /**
     * Get all transactions
     * @return List of all transactions
     */
    @GetMapping("/transactions")
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        List<Transaction> transactions = transactionService.getAllTransactions();
        return ResponseEntity.ok(transactions);
    }

    /**
     * Get transactions by type
     * @param type - transaction type
     * @return List of transactions
     */
    @GetMapping("/transactions/type/{type}")
    public ResponseEntity<List<Transaction>> getTransactionsByType(@PathVariable String type) {
        List<Transaction> transactions = transactionService.getTransactionsByType(type);
        return ResponseEntity.ok(transactions);
    }

    // ==================== STATISTICS ENDPOINTS ====================

    /**
     * Get total users count
     * @return Total users count
     */
    @GetMapping("/stats/users")
    public ResponseEntity<Map<String, Object>> getTotalUsers() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", customerService.getTotalCustomersCount());
        return ResponseEntity.ok(stats);
    }

    /**
     * Get total transactions count
     * @return Total transactions count
     */
    @GetMapping("/stats/transactions")
    public ResponseEntity<Map<String, Object>> getTotalTransactions() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", transactionService.getTotalTransactionsCount());
        return ResponseEntity.ok(stats);
    }

    /**
     * Get total bank balance
     * @return Total bank balance
     */
    @GetMapping("/stats/balance")
    public ResponseEntity<Map<String, Object>> getTotalBalance() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalBalance", customerService.getTotalBankBalance());
        return ResponseEntity.ok(stats);
    }

    /**
     * Get pending approvals count
     * @return Pending approvals count
     */
    @GetMapping("/stats/pending-approvals")
    public ResponseEntity<Map<String, Object>> getPendingApprovals() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("pending", 0); // Placeholder for pending approvals
        return ResponseEntity.ok(stats);
    }

    /**
     * Get dashboard statistics summary
     * @return Dashboard statistics
     */
    @GetMapping("/stats/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", customerService.getTotalCustomersCount());
        stats.put("totalTransactions", transactionService.getTotalTransactionsCount());
        stats.put("totalBalance", customerService.getTotalBankBalance());
        stats.put("totalDeposits", transactionService.getTotalDepositAmount());
        stats.put("totalWithdrawals", transactionService.getTotalWithdrawalAmount());
        return ResponseEntity.ok(stats);
    }

    // ==================== APPROVAL ENDPOINTS ====================

    /**
     * Get all account approvals
     * @return List of approvals
     */
    @GetMapping("/approvals")
    public ResponseEntity<List<Customer>> getApprovals() {
        // Return all pending/new customers
        List<Customer> customers = customerService.getAllCustomers()
                .stream()
                .filter(c -> "ACTIVE".equals(c.getStatus()))
                .toList();
        return ResponseEntity.ok(customers);
    }

    /**
     * Approve customer account
     * @param customerId - customer ID
     * @return Success message
     */
    @PutMapping("/approvals/{customerId}/approve")
    public ResponseEntity<Map<String, String>> approveAccount(@PathVariable String customerId) {
        Customer customer = customerService.getCustomerById(customerId);
        Map<String, String> response = new HashMap<>();

        if (customer != null) {
            customer.setStatus("APPROVED");
            customer.setActive(true);
            customerService.updateCustomer(customerId, customer);
            response.put("message", "Account approved successfully");
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "Customer not found");
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Reject customer account
     * @param customerId - customer ID
     * @return Success message
     */
    @PutMapping("/approvals/{customerId}/reject")
    public ResponseEntity<Map<String, String>> rejectAccount(@PathVariable String customerId) {
        Customer customer = customerService.getCustomerById(customerId);
        Map<String, String> response = new HashMap<>();

        if (customer != null) {
            customer.setStatus("REJECTED");
            customer.setActive(false);
            customerService.updateCustomer(customerId, customer);
            response.put("message", "Account rejected");
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "Customer not found");
            return ResponseEntity.notFound().build();
        }
    }
}
