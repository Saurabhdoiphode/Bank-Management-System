package com.bankmanagement.controller;

import com.bankmanagement.dto.AuthResponse;
import com.bankmanagement.dto.CustomerRegisterRequest;
import com.bankmanagement.dto.LoginRequest;
import com.bankmanagement.service.AdminService;
import com.bankmanagement.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Authentication Controller
 * Handles customer and admin login/registration
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final CustomerService customerService;
    private final AdminService adminService;

    // ==================== CUSTOMER ENDPOINTS ====================

    /**
     * Register new customer
     * @param request - customer registration request
     * @return AuthResponse with token
     */
    @PostMapping("/customer/register")
    public ResponseEntity<AuthResponse> registerCustomer(@RequestBody CustomerRegisterRequest request) {
        AuthResponse response = customerService.registerCustomer(request);
        return response.getSuccess() ? 
                ResponseEntity.status(HttpStatus.CREATED).body(response) :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Customer login
     * @param request - login request
     * @return AuthResponse with token
     */
    @PostMapping("/customer/login")
    public ResponseEntity<AuthResponse> loginCustomer(@RequestBody LoginRequest request) {
        AuthResponse response = customerService.loginCustomer(request);
        return response.getSuccess() ? 
                ResponseEntity.ok(response) :
                ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    // ==================== ADMIN ENDPOINTS ====================

    /**
     * Admin login
     * @param request - login request with fixed credentials
     * @return AuthResponse with token
     */
    @PostMapping("/admin/login")
    public ResponseEntity<AuthResponse> loginAdmin(@RequestBody LoginRequest request) {
        AuthResponse response = adminService.loginAdmin(request);
        return response.getSuccess() ? 
                ResponseEntity.ok(response) :
                ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }
}
