package com.bankmanagement.dto;

import lombok.*;

/**
 * Request DTO for deposit transaction
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepositRequest {
    private Double amount;
    private String method;
    private String description;
}
