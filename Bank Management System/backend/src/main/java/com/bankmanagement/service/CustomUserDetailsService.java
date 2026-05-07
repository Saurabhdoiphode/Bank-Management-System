package com.bankmanagement.service;

import com.bankmanagement.model.Admin;
import com.bankmanagement.model.Customer;
import com.bankmanagement.repository.AdminRepository;
import com.bankmanagement.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Custom User Details Service
 * Loads user details for authentication
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final CustomerRepository customerRepository;
    private final AdminRepository adminRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Try to find as customer first
        Optional<Customer> customer = customerRepository.findById(username);
        if (customer.isPresent()) {
            Customer c = customer.get();
            return User.builder()
                    .username(c.getId())
                    .password(c.getPassword())
                    .roles("CUSTOMER")
                    .build();
        }

        // Try to find as admin
        Optional<Admin> admin = adminRepository.findById(username);
        if (admin.isPresent()) {
            Admin a = admin.get();
            return User.builder()
                    .username(a.getId())
                    .password(a.getPassword())
                    .roles(a.getRole())
                    .build();
        }

        throw new UsernameNotFoundException("User not found: " + username);
    }
}