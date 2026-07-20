package com.backend.investment.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "withdraw_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WithdrawHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * User who submitted the withdrawal request
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Withdrawal Amount
     */
    @Column(name = "requested_amount", nullable = false)
    private BigDecimal requestedAmount;

    @Column(name = "service_fee", nullable = false)
    private BigDecimal serviceFee;

    @Column(name = "payable_amount", nullable = false)
    private BigDecimal payableAmount;

    /**
     * ACCOUNT / MANUAL
     */
    @Column(name = "withdraw_type", nullable = false, length = 20)
    private String withdrawType;

    /**
     * UPI / USDT
     */
    @Column(name = "payment_method", length = 20)
    private String paymentMethod;

    /**
     * UPI ID or USDT Wallet Address
     */
    @Column(name = "account_details", length = 255)
    private String accountDetails;

    /**
     * PENDING / APPROVED / REJECTED
     */
    @Column(nullable = false, length = 20)
    private String status;

    /**
     * Admin Remarks
     */
    @Column(columnDefinition = "TEXT")
    private String remarks;

    /**
     * Request Creation Time
     */
    @Column(name = "created_on")
    private LocalDateTime createdOn;

    /**
     * Approval Time
     */
    @Column(name = "approved_on")
    private LocalDateTime approvedOn;

    @PrePersist
    public void prePersist() {

        if (createdOn == null) {
            createdOn = LocalDateTime.now();
        }

        if (status == null) {
            status = "PENDING";
        }

    }

}