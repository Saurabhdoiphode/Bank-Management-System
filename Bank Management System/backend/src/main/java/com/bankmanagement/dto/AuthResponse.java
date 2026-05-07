package com.bankmanagement.dto;

import lombok.*;

/**
 * Response DTO for authentication
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {
    private String token;
    private String id;
    private String name;
    private String email;
    private String accountNumber;
    private String message;
    private Boolean success;
}
