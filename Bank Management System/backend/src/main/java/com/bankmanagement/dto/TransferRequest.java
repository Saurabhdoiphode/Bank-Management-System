package com.bankmanagement.dto;

import lombok.*;

/**
 * Request DTO for transfer transaction
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransferRequest {
    private String recipientAccountNumber;
    private Double amount;
    private String description;
}
