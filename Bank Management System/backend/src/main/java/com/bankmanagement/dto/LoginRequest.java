package com.bankmanagement.dto;

import lombok.*;

/**
 * Request DTO for login
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequest {
    private String email;
    private String password;
}
