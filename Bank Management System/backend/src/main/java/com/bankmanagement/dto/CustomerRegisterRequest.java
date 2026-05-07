package com.bankmanagement.dto;

import lombok.*;

/**
 * Request DTO for customer registration
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerRegisterRequest {
    private String fullName;
    private String email;
    private String password;
    private String mobileNumber;
    private String address;
    private String aadhaarNumber;
    private String panNumber;
    private String accountType;
}
