package com.backend.investment.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class WithdrawRequestDto {

    private Long userId;

    private BigDecimal amount;

    // ACCOUNT or MANUAL
    private String withdrawType;

    // UPI / USDT
    private String paymentMethod;

    // Wallet Address / UPI Id
    private String accountDetails;

}