package com.backend.investment.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.investment.Exception.BadRequestException;
import com.backend.investment.Exception.ResourceNotFoundException;
import com.backend.investment.dto.RechargeRequestDto;
import com.backend.investment.dto.RechargeResponseDto;
import com.backend.investment.entity.RechargeHistory;
import com.backend.investment.entity.User;
import com.backend.investment.entity.WalletTransaction;
import com.backend.investment.repository.RechargeRepository;
import com.backend.investment.repository.UserRepository;
import com.backend.investment.repository.WalletTransactionRepository;
import com.backend.investment.service.IRechargeService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RechargeServiceImpl implements IRechargeService {

    private final RechargeRepository rechargeRepository;

    private final UserRepository userRepository;

    private final WalletTransactionRepository walletTransactionRepository;

    @Override
    @Transactional
    public RechargeResponseDto rechargeWallet(RechargeRequestDto request) {

        if (request.getUserId() == null) {
            throw new BadRequestException("User Id is mandatory.");
        }

        if (request.getAmount() == null ||
                request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Recharge amount should be greater than zero.");
        }

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() ->
                new ResourceNotFoundException("Rechage", "User", request.getUserId().toString())
            );

        RechargeHistory recharge = new RechargeHistory();

        recharge.setUser(user);
        recharge.setAmount(request.getAmount());
        recharge.setPaymentMethod(request.getPaymentMethod());
        recharge.setTransactionId(request.getTransactionId());
        recharge.setStatus("SUCCESS");

        recharge = rechargeRepository.save(recharge);

        //-----------------------------------------
        // Update Wallet Balance
        //-----------------------------------------

        BigDecimal openingBalance = user.getBalance();

        BigDecimal closingBalance =
                openingBalance.add(request.getAmount());

        user.setBalance(closingBalance);

        user.setTotalRecharge(
                user.getTotalRecharge().add(request.getAmount())
        );

        userRepository.save(user);

        //-----------------------------------------
        // Wallet Transaction Entry
        //-----------------------------------------

        WalletTransaction wallet = new WalletTransaction();

        wallet.setUser(user);

        wallet.setTransactionType("RECHARGE");

        wallet.setAmount(request.getAmount());

        wallet.setOpeningBalance(openingBalance);

        wallet.setClosingBalance(closingBalance);

        wallet.setReferenceId(recharge.getId());

        wallet.setReferenceType("RECHARGE");

        wallet.setRemarks("Wallet Recharge Successful");

        walletTransactionRepository.save(wallet);

        //-----------------------------------------
        // Response
        //-----------------------------------------

        RechargeResponseDto response = new RechargeResponseDto();

        response.setId(recharge.getId());

        response.setUserId(user.getId());

        response.setAmount(recharge.getAmount());

        response.setPaymentMethod(recharge.getPaymentMethod());

        response.setTransactionId(recharge.getTransactionId());

        response.setStatus(recharge.getStatus());

        response.setCreatedOn(recharge.getCreatedOn());

        return response;

    }

    @Override
    public List<RechargeResponseDto> rechargeHistory(Long userId) {

        List<RechargeHistory> list =
                rechargeRepository.findByUserId(userId);

        return list.stream().map(recharge -> {

            RechargeResponseDto dto = new RechargeResponseDto();

            dto.setId(recharge.getId());

            dto.setUserId(recharge.getUser().getId());

            dto.setAmount(recharge.getAmount());

            dto.setPaymentMethod(recharge.getPaymentMethod());

            dto.setTransactionId(recharge.getTransactionId());

            dto.setStatus(recharge.getStatus());

            dto.setCreatedOn(recharge.getCreatedOn());

            return dto;

        }).collect(Collectors.toList());

    }

}