package com.backend.investment.service.impl;

import com.backend.investment.dto.WithdrawRequestDto;
import com.backend.investment.dto.WithdrawResponseDto;
import com.backend.investment.entity.User;
import com.backend.investment.entity.WithdrawHistory;
import com.backend.investment.repository.UserRepository;
import com.backend.investment.repository.WithdrawRepository;
import com.backend.investment.service.IWithdrawService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WithdrawServiceImpl implements IWithdrawService {

    private final WithdrawRepository withdrawRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public WithdrawResponseDto requestWithdraw(WithdrawRequestDto request) {

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Invalid Amount");
        }

        if (user.getBalance().compareTo(request.getAmount()) < 0) {
            throw new RuntimeException("Insufficient Balance");
        }

        BigDecimal requestedAmount = request.getAmount();

        BigDecimal serviceFee = requestedAmount
                .multiply(BigDecimal.valueOf(0.10));

        BigDecimal payableAmount = requestedAmount.subtract(serviceFee);

        String paymentMethod;
        String accountDetails;

        if ("ACCOUNT".equalsIgnoreCase(request.getWithdrawType())) {

            if (user.getAccountHolderName() == null ||
                    user.getBankName() == null ||
                    user.getAccountNumber() == null ||
                    user.getIfscCode() == null) {

                throw new RuntimeException("Please add your bank account details first.");
            }

            paymentMethod = "BANK";

            accountDetails =
                    "Holder : " + user.getAccountHolderName()
                            + ", Bank : " + user.getBankName()
                            + ", Account : " + user.getAccountNumber()
                            + ", IFSC : " + user.getIfscCode();

        } else if ("MANUAL".equalsIgnoreCase(request.getWithdrawType())) {

            if (request.getPaymentMethod() == null ||
                    request.getPaymentMethod().isBlank()) {
                throw new RuntimeException("Please select payment method.");
            }

            if (request.getAccountDetails() == null ||
                    request.getAccountDetails().isBlank()) {
                throw new RuntimeException("Please enter account details.");
            }

            paymentMethod = request.getPaymentMethod();
            accountDetails = request.getAccountDetails();

        } else {

            throw new RuntimeException("Invalid withdraw type.");

        }

        WithdrawHistory withdraw = WithdrawHistory.builder()
                .user(user)
                .requestedAmount(requestedAmount)
                .serviceFee(serviceFee)
                .payableAmount(payableAmount)
                .withdrawType(request.getWithdrawType())
                .paymentMethod(paymentMethod)
                .accountDetails(accountDetails)
                .status("PENDING")
                .createdOn(LocalDateTime.now())
                .build();

        withdrawRepository.save(withdraw);

        return WithdrawResponseDto.builder()
                .id(withdraw.getId())
                .requestedAmount(withdraw.getRequestedAmount())
                .serviceFee(withdraw.getServiceFee())
                .payableAmount(withdraw.getPayableAmount())
                .withdrawType(withdraw.getWithdrawType())
                .paymentMethod(withdraw.getPaymentMethod())
                .accountDetails(withdraw.getAccountDetails())
                .status(withdraw.getStatus())
                .createdOn(withdraw.getCreatedOn())
                .message("Withdraw request submitted successfully.")
                .build();
    }
    @Override
    public List<WithdrawResponseDto> withdrawHistory(Long userId) {

        return withdrawRepository
                .findByUserIdOrderByCreatedOnDesc(userId)
                .stream()
                .map(withdraw -> WithdrawResponseDto.builder()
                        .id(withdraw.getId())
                        .requestedAmount(withdraw.getRequestedAmount())
                        .serviceFee(withdraw.getServiceFee())
                        .payableAmount(withdraw.getPayableAmount())
                        .withdrawType(withdraw.getWithdrawType())
                        .paymentMethod(withdraw.getPaymentMethod())
                        .accountDetails(withdraw.getAccountDetails())
                        .status(withdraw.getStatus())
                        .createdOn(withdraw.getCreatedOn())
                        .build())
                .toList();

    }


}
