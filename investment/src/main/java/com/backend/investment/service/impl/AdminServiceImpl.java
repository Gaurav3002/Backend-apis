package com.backend.investment.service.impl;

import com.backend.investment.Exception.ResourceNotFoundException;
import com.backend.investment.dto.AdminLoginRequest;
import com.backend.investment.dto.AdminLoginResponse;
import com.backend.investment.entity.*;
import com.backend.investment.repository.*;
import com.backend.investment.service.IAdminService;
import com.backend.investment.service.JwtService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements IAdminService {
    private final AdminRepository adminRepository;
    private final BCryptPasswordEncoder encoder;
    private final JwtService jwtService;

    private final PaymentRequestRepository paymentRequestRepository;
    private final UserRepository userRepository;
    private final RechargeRepository rechargeRepository;
    private final WalletTransactionRepository walletTransactionRepository;
    private final WithdrawRepository withdrawRepository;


    @Override
    public AdminLoginResponse login(AdminLoginRequest request) {
        Admin admin = adminRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Admin", "login", request.getUsername()));

        if(!encoder.matches(request.getPassword(), admin.getPassword())){
            throw new RuntimeException("Invalid Password");
        }
        AdminLoginResponse adminLoginResponse = new AdminLoginResponse();
        adminLoginResponse.setToken(jwtService.generateToken(admin.getUsername(), "ADMIN"));
        return adminLoginResponse;
    }
    @Override
    public List<PaymentRequest> getPendingRechargeRequests() {

        return paymentRequestRepository.findByStatus("PENDING");
    }

    @Transactional
    @Override
    public String approveRecharge(Long paymentRequestId) {

        PaymentRequest paymentRequest =
                paymentRequestRepository.findById(paymentRequestId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "PaymentRequest",
                                        "id",
                                        paymentRequestId.toString()));

        if (!paymentRequest.getStatus().equals("PENDING")) {
            throw new RuntimeException("Recharge already processed.");
        }

        User user =
                userRepository.findById(paymentRequest.getUserId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "User",
                                        "id",
                                        paymentRequest.getUserId().toString()));

        BigDecimal openingBalance = user.getBalance();

        BigDecimal closingBalance =
                openingBalance.add(paymentRequest.getAmount());

        user.setBalance(closingBalance);

        user.setTotalRecharge(
                user.getTotalRecharge().add(paymentRequest.getAmount()));

        userRepository.save(user);

        RechargeHistory recharge = new RechargeHistory();

        recharge.setUser(user);

        recharge.setAmount(paymentRequest.getAmount());

        recharge.setPaymentMethod(paymentRequest.getPaymentMethod());

        recharge.setTransactionId(paymentRequest.getTransactionId());

        recharge.setStatus("SUCCESS");

        rechargeRepository.save(recharge);

        WalletTransaction wallet = new WalletTransaction();

        wallet.setUser(user);

        wallet.setTransactionType("RECHARGE");

        wallet.setAmount(paymentRequest.getAmount());

        wallet.setOpeningBalance(openingBalance);

        wallet.setClosingBalance(closingBalance);

        wallet.setReferenceId(recharge.getId());

        wallet.setReferenceType("RECHARGE");
        wallet.setRemarks("Recharge Approved By Admin");
        walletTransactionRepository.save(wallet);
        paymentRequest.setStatus("SUCCESS");
        paymentRequest.setVerifiedOn(LocalDateTime.now());

        paymentRequestRepository.save(paymentRequest);

        return "Recharge Approved Successfully";

    }

    @Override
    @Transactional
    public String rejectRecharge(Long id) {

        PaymentRequest request = paymentRequestRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Payment Request",
                                "id",
                                id.toString()
                        ));

        if (!"PENDING".equals(request.getStatus())) {
            throw new RuntimeException("Request already processed.");
        }

        request.setStatus("REJECTED");
        request.setAdminRemarks("Reject By Admin");
        request.setVerifiedOn(LocalDateTime.now());

        paymentRequestRepository.save(request);

        return "Recharge Request Rejected Successfully";
    }

    @Override
    public List<WithdrawHistory> getPendingWithdrawRequests() {
        return withdrawRepository.findByStatus("PENDING");
    }

    @Override
    @Transactional
    public String approveWithdraw(Long withdrawId) {

        WithdrawHistory withdraw = withdrawRepository.findById(withdrawId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Withdraw",
                                "id",
                                withdrawId.toString()
                        ));

        if (!"PENDING".equals(withdraw.getStatus())) {
            throw new RuntimeException("Withdraw already processed.");
        }

        User user = withdraw.getUser();

        BigDecimal requestedAmount = withdraw.getRequestedAmount();

        if (user.getBalance().compareTo(requestedAmount) < 0) {
            throw new RuntimeException("Insufficient balance.");
        }

        BigDecimal openingBalance = user.getBalance();

        BigDecimal closingBalance = openingBalance.subtract(requestedAmount);

        // Update User Wallet
        user.setBalance(closingBalance);

        user.setTotalWithdraw(
                user.getTotalWithdraw().add(requestedAmount)
        );

        userRepository.save(user);

        // Wallet Transaction
        WalletTransaction wallet = new WalletTransaction();

        wallet.setUser(user);

        wallet.setTransactionType("WITHDRAW");

        // Amount deducted from wallet
        wallet.setAmount(requestedAmount);
        wallet.setOpeningBalance(openingBalance);
        wallet.setClosingBalance(closingBalance);
        wallet.setReferenceId(withdraw.getId());
        wallet.setReferenceType("WITHDRAW");

        wallet.setRemarks(
                "Withdraw Approved. Requested: "
                        + requestedAmount
                        + ", Fee: "
                        + withdraw.getServiceFee()
                        + ", Payable: "
                        + withdraw.getPayableAmount()
        );

        walletTransactionRepository.save(wallet);

        // Update Withdraw Request
        withdraw.setStatus("APPROVED");

        withdraw.setApprovedOn(LocalDateTime.now());

        withdraw.setRemarks(
                "Approved. User Receives ₹"
                        + withdraw.getPayableAmount()
        );

        withdrawRepository.save(withdraw);

        return "Withdraw Approved Successfully";
    }

    @Override
    @Transactional
    public String rejectWithdraw(Long withdrawId) {

        WithdrawHistory withdraw = withdrawRepository.findById(withdrawId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Withdraw",
                                "id",
                                withdrawId.toString()
                        ));

        if (!"PENDING".equals(withdraw.getStatus())) {
            throw new RuntimeException("Withdraw already processed.");
        }

        withdraw.setStatus("REJECTED");

        withdraw.setRemarks("Rejected By Admin");

        withdraw.setApprovedOn(LocalDateTime.now());

        withdrawRepository.save(withdraw);

        return "Withdraw Rejected Successfully";
    }

}
