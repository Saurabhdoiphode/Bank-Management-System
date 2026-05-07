package com.bankmanagement.dto;

import lombok.*;

/**
 * Request DTO for withdrawal transaction
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WithdrawRequest {
    private Double amount;
    private String withdrawType;
    private String description;
}
