package com.bankmanagement.controller;

import com.bankmanagement.model.Customer;
import com.bankmanagement.service.CustomerService;
import com.bankmanagement.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Customer Controller
 * Handles customer-related endpoints
 */
@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class CustomerController {

    private final CustomerService customerService;
    private final JwtService jwtService;

    /**
     * Get customer profile
     * @param authorization - Bearer token
     * @return Customer profile
     */
    @GetMapping("/profile")
    public ResponseEntity<Customer> getProfile(@RequestHeader("Authorization") String authorization) {
        String token = extractToken(authorization);
        String customerId = jwtService.extractUserId(token);
        Customer customer = customerService.getCustomerById(customerId);

        return customer != null ? 
                ResponseEntity.ok(customer) :
                ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    /**
     * Get all customers (admin only)
     * @return List of customers
     */
    @GetMapping
    public ResponseEntity<List<Customer>> getAllCustomers() {
        List<Customer> customers = customerService.getAllCustomers();
        return ResponseEntity.ok(customers);
    }

    /**
     * Get customer by ID
     * @param customerId - customer ID
     * @return Customer object
     */
    @GetMapping("/{customerId}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable String customerId) {
        Customer customer = customerService.getCustomerById(customerId);
        return customer != null ? 
                ResponseEntity.ok(customer) :
                ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    /**
     * Update customer profile
     * @param authorization - Bearer token
     * @param customer - updated customer object
     * @return Updated customer
     */
    @PutMapping("/profile")
    public ResponseEntity<Customer> updateProfile(
            @RequestHeader("Authorization") String authorization,
            @RequestBody Customer customer) {
        String token = extractToken(authorization);
        String customerId = jwtService.extractUserId(token);
        Customer updated = customerService.updateCustomer(customerId, customer);

        return updated != null ? 
                ResponseEntity.ok(updated) :
                ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    /**
     * Delete customer (admin only)
     * @param customerId - customer ID
     * @return Success message
     */
    @DeleteMapping("/{customerId}")
    public ResponseEntity<Map<String, String>> deleteCustomer(@PathVariable String customerId) {
        boolean deleted = customerService.deleteCustomer(customerId);
        Map<String, String> response = new HashMap<>();

        if (deleted) {
            response.put("message", "Customer deleted successfully");
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "Customer not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * Get all active customers
     * @return List of active customers
     */
    @GetMapping("/active/all")
    public ResponseEntity<List<Customer>> getActiveCustomers() {
        List<Customer> customers = customerService.getActiveCustomers();
        return ResponseEntity.ok(customers);
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
