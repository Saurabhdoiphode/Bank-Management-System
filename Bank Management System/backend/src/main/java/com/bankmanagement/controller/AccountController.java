package com.bankmanagement.controller;

import com.bankmanagement.model.Customer;
import com.bankmanagement.service.CustomerService;
import com.bankmanagement.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Account Controller
 * Handles account-related operations
 */
@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class AccountController {

    private final CustomerService customerService;
    private final JwtService jwtService;

    /**
     * Get account balance
     * @param authorization - Bearer token
     * @return Balance information
     */
    @GetMapping("/balance")
    public ResponseEntity<Map<String, Object>> getBalance(
            @RequestHeader("Authorization") String authorization) {
        String token = extractToken(authorization);
        String customerId = jwtService.extractUserId(token);
        Customer customer = customerService.getCustomerById(customerId);

        Map<String, Object> response = new HashMap<>();
        if (customer != null) {
            response.put("balance", customer.getBalance());
            response.put("accountNumber", customer.getAccountNumber());
            response.put("accountType", customer.getAccountType());
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get account information
     * @param authorization - Bearer token
     * @return Account information
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getAccountInfo(
            @RequestHeader("Authorization") String authorization) {
        String token = extractToken(authorization);
        String customerId = jwtService.extractUserId(token);
        Customer customer = customerService.getCustomerById(customerId);

        Map<String, Object> response = new HashMap<>();
        if (customer != null) {
            response.put("accountNumber", customer.getAccountNumber());
            response.put("accountType", customer.getAccountType());
            response.put("balance", customer.getBalance());
            response.put("status", customer.getStatus());
            response.put("createdAt", customer.getCreatedAt());
            response.put("lastLogin", customer.getLastLogin());
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
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
