package com.backend.investment.service;

import com.backend.investment.dto.AdminLoginRequest;
import com.backend.investment.dto.AdminLoginResponse;
import com.backend.investment.entity.PaymentRequest;
import com.backend.investment.entity.WithdrawHistory;

import java.util.List;

public interface IAdminService {
    AdminLoginResponse login(AdminLoginRequest request);
    List<PaymentRequest> getPendingRechargeRequests();
    String approveRecharge(Long paymentRequestId);
    String rejectRecharge(Long id);
    List<WithdrawHistory> getPendingWithdrawRequests();

    String approveWithdraw(Long withdrawId);

    String rejectWithdraw(Long withdrawId);

}
