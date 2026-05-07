package com.bankmanagement.service;

import com.bankmanagement.dto.AuthResponse;
import com.bankmanagement.dto.CustomerRegisterRequest;
import com.bankmanagement.dto.LoginRequest;
import com.bankmanagement.model.Customer;
import com.bankmanagement.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service for customer-related operations
 */
@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    /**
     * Register a new customer
     * @param request - customer registration request
     * @return AuthResponse with customer details and token
     */
    public AuthResponse registerCustomer(CustomerRegisterRequest request) {
        // Check if customer already exists
        if (customerRepository.findByEmail(request.getEmail()).isPresent()) {
            return AuthResponse.builder()
                    .success(false)
                    .message("Customer with this email already exists")
                    .build();
        }

        // Create new customer
        Customer customer = new Customer(
                request.getFullName(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getMobileNumber(),
                request.getAddress(),
                request.getAadhaarNumber(),
                request.getPanNumber(),
                request.getAccountType()
        );

        // Generate unique account number
        String accountNumber = generateAccountNumber();
        customer.setAccountNumber(accountNumber);

        // Save customer
        customer = customerRepository.save(customer);

        // Generate JWT token
        String token = jwtService.generateToken(customer.getId(), "CUSTOMER");

        return AuthResponse.builder()
                .success(true)
                .token(token)
                .id(customer.getId())
                .name(customer.getFullName())
                .email(customer.getEmail())
                .accountNumber(customer.getAccountNumber())
                .message("Customer registered successfully")
                .build();
    }

    /**
     * Login customer
     * @param request - login request
     * @return AuthResponse with token and customer details
     */
    public AuthResponse loginCustomer(LoginRequest request) {
        Optional<Customer> customerOpt = customerRepository.findByEmail(request.getEmail());

        if (customerOpt.isEmpty()) {
            return AuthResponse.builder()
                    .success(false)
                    .message("Invalid email or password")
                    .build();
        }

        Customer customer = customerOpt.get();

        // Check password
        if (!passwordEncoder.matches(request.getPassword(), customer.getPassword())) {
            return AuthResponse.builder()
                    .success(false)
                    .message("Invalid email or password")
                    .build();
        }

        // Check if account is active
        if (!customer.getActive() || !"ACTIVE".equals(customer.getStatus())) {
            return AuthResponse.builder()
                    .success(false)
                    .message("Your account is inactive or blocked")
                    .build();
        }

        // Update last login
        customer.setLastLogin(LocalDateTime.now());
        customerRepository.save(customer);

        // Generate JWT token
        String token = jwtService.generateToken(customer.getId(), "CUSTOMER");

        return AuthResponse.builder()
                .success(true)
                .token(token)
                .id(customer.getId())
                .name(customer.getFullName())
                .email(customer.getEmail())
                .accountNumber(customer.getAccountNumber())
                .message("Login successful")
                .build();
    }

    /**
     * Get customer by ID
     * @param customerId - customer ID
     * @return Customer object
     */
    public Customer getCustomerById(String customerId) {
        return customerRepository.findById(customerId).orElse(null);
    }

    /**
     * Get customer by email
     * @param email - customer email
     * @return Customer object
     */
    public Customer getCustomerByEmail(String email) {
        return customerRepository.findByEmail(email).orElse(null);
    }

    /**
     * Get customer by account number
     * @param accountNumber - account number
     * @return Customer object
     */
    public Customer getCustomerByAccountNumber(String accountNumber) {
        return customerRepository.findByAccountNumber(accountNumber).orElse(null);
    }

    /**
     * Get all customers
     * @return List of all customers
     */
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    /**
     * Get all active customers
     * @return List of active customers
     */
    public List<Customer> getActiveCustomers() {
        return customerRepository.findByStatusAndActive("ACTIVE", true);
    }

    /**
     * Update customer
     * @param customerId - customer ID
     * @param customer - updated customer object
     * @return Updated customer
     */
    public Customer updateCustomer(String customerId, Customer customer) {
        Optional<Customer> existingCustomer = customerRepository.findById(customerId);

        if (existingCustomer.isPresent()) {
            Customer customerToUpdate = existingCustomer.get();
            customerToUpdate.setFullName(customer.getFullName());
            customerToUpdate.setMobileNumber(customer.getMobileNumber());
            customerToUpdate.setAddress(customer.getAddress());
            customerToUpdate.setUpdatedAt(LocalDateTime.now());
            return customerRepository.save(customerToUpdate);
        }

        return null;
    }

    /**
     * Delete customer
     * @param customerId - customer ID
     * @return true if deleted, false otherwise
     */
    public boolean deleteCustomer(String customerId) {
        if (customerRepository.existsById(customerId)) {
            customerRepository.deleteById(customerId);
            return true;
        }
        return false;
    }

    /**
     * Generate unique account number
     * @return Account number
     */
    private String generateAccountNumber() {
        // Format: SB + 10 random digits
        return "SB" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 6).hashCode();
    }

    /**
     * Count total customers
     * @return Total customer count
     */
    public long getTotalCustomersCount() {
        return customerRepository.count();
    }

    /**
     * Calculate total bank balance
     * @return Total balance across all customers
     */
    public Double getTotalBankBalance() {
        return customerRepository.findAll()
                .stream()
                .mapToDouble(c -> c.getBalance() != null ? c.getBalance() : 0.0)
                .sum();
    }
}
