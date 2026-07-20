package com.backend.investment.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WithdrawResponseDto {

    private Long id;

    private BigDecimal requestedAmount;

    private BigDecimal serviceFee;

    private BigDecimal payableAmount;

    // ACCOUNT / MANUAL
    private String withdrawType;

    // UPI / USDT
    private String paymentMethod;

    // UPI ID / Wallet Address
    private String accountDetails;

    // PENDING / APPROVED / REJECTED
    private String status;

    private String remarks;

    private LocalDateTime createdOn;

    private String message;

}