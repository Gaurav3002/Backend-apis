package com.backend.investment.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.backend.investment.Exception.BadRequestException;
import com.backend.investment.common.ConstantResponse;
import com.backend.investment.dto.LoginRequest;
import com.backend.investment.dto.RegisterRequest;
import com.backend.investment.entity.User;
import com.backend.investment.repository.UserRepository;
import com.backend.investment.service.AuthService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public String register(RegisterRequest request) {

        if (request.getPhone() == null || request.getPhone().trim().isEmpty()) {
            throw new BadRequestException(ConstantResponse.PHONE_REQUIRED);
        }

        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new BadRequestException(ConstantResponse.PASSWORD_REQUIRED);
        }

        if (request.getWithdrawPassword() == null || request.getWithdrawPassword().trim().isEmpty()) {
            throw new BadRequestException(ConstantResponse.WITHDRAW_PASSWORD_REQUIRED);
        }

        if (userRepository.findByPhone(request.getPhone().trim()).isPresent()) {
            throw new BadRequestException(ConstantResponse.PHONE_ALREADY_EXISTS);
        }

        User user = new User();

        user.setPhone(request.getPhone().trim());

        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );

        user.setWithdrawPassword(
                passwordEncoder.encode(request.getWithdrawPassword())
        );

        user.setReferralCode(request.getReferralCode());

        user.setBalance(BigDecimal.ZERO);
        user.setTotalIncome(BigDecimal.ZERO);
        user.setTotalRecharge(BigDecimal.ZERO);
        user.setTotalWithdraw(BigDecimal.ZERO);
        user.setStatus("ACTIVE");
        user.setCreatedOn(LocalDateTime.now());

        userRepository.save(user);

        return ConstantResponse.REGISTRATION_SUCCESS;
    }

    @Override
    public User login(LoginRequest request) {

        if (request.getPhone() == null || request.getPhone().trim().isEmpty()) {
            throw new BadRequestException(ConstantResponse.PHONE_REQUIRED);
        }

        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new BadRequestException(ConstantResponse.PASSWORD_REQUIRED);
        }

        User user = userRepository.findByPhone(request.getPhone().trim())
                .orElseThrow(() ->
                        new BadRequestException(ConstantResponse.INVALID_LOGIN));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadRequestException(ConstantResponse.INVALID_LOGIN);
        }

        return user;
    }

}