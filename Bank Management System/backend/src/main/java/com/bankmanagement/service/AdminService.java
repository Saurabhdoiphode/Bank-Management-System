package com.bankmanagement.service;

import com.bankmanagement.dto.AuthResponse;
import com.bankmanagement.dto.LoginRequest;
import com.bankmanagement.model.Admin;
import com.bankmanagement.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service for admin-related operations
 */
@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    /**
     * Login admin
     * @param request - login request
     * @return AuthResponse with token and admin details
     */
    public AuthResponse loginAdmin(LoginRequest request) {
        Optional<Admin> adminOpt = adminRepository.findByEmailAndActive(request.getEmail(), true);

        if (adminOpt.isEmpty()) {
            return AuthResponse.builder()
                    .success(false)
                    .message("Invalid admin credentials")
                    .build();
        }

        Admin admin = adminOpt.get();

        // Check password
        if (!passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
            return AuthResponse.builder()
                    .success(false)
                    .message("Invalid admin credentials")
                    .build();
        }

        // Check if admin is active
        if (!"ACTIVE".equals(admin.getStatus())) {
            return AuthResponse.builder()
                    .success(false)
                    .message("Admin account is inactive")
                    .build();
        }

        // Update last login
        admin.setLastLogin(LocalDateTime.now());
        adminRepository.save(admin);

        // Generate JWT token
        String token = jwtService.generateToken(admin.getId(), admin.getRole());

        return AuthResponse.builder()
                .success(true)
                .token(token)
                .id(admin.getId())
                .name(admin.getName())
                .email(admin.getEmail())
                .message("Admin login successful")
                .build();
    }

    /**
     * Get admin by ID
     * @param adminId - admin ID
     * @return Admin object
     */
    public Admin getAdminById(String adminId) {
        return adminRepository.findById(adminId).orElse(null);
    }

    /**
     * Get admin by email
     * @param email - admin email
     * @return Admin object
     */
    public Admin getAdminByEmail(String email) {
        return adminRepository.findByEmail(email).orElse(null);
    }

    /**
     * Get all admins
     * @return List of all admins
     */
    public List<Admin> getAllAdmins() {
        return adminRepository.findAll();
    }

    /**
     * Initialize default admin if not exists
     * This should be run on application startup
     */
    public void initializeDefaultAdmin() {
        Optional<Admin> defaultAdmin = adminRepository.findByEmail("admin@gmail.com");

        if (defaultAdmin.isEmpty()) {
            Admin admin = Admin.builder()
                    .name("Administrator")
                    .email("admin@gmail.com")
                    .password(passwordEncoder.encode("admin123"))
                    .role("SUPER_ADMIN")
                    .status("ACTIVE")
                    .active(true)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            adminRepository.save(admin);
        }
    }
}
