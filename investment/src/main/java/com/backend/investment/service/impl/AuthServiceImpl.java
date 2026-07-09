package com.backend.investment.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.backend.investment.Exception.BadRequestException;
import com.backend.investment.Exception.ResourceNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.backend.investment.constants.InvestmentConstants;
import com.backend.investment.dto.LoginRequest;
import com.backend.investment.dto.RegisterRequest;
import com.backend.investment.entity.User;
import com.backend.investment.repository.UserRepository;
import com.backend.investment.service.IAuthService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public String register(RegisterRequest request) {

        if (request.getPhone() == null || request.getPhone().trim().isEmpty()) {
            throw new BadRequestException("Phone Number Required!");
        }

        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new BadRequestException("Password is Required!");
        }

        if (request.getWithdrawPassword() == null || request.getWithdrawPassword().trim().isEmpty()) {
            throw new BadRequestException("Password is Required!");
        }

        if (userRepository.findByPhone(request.getPhone().trim()).isPresent()) {
            throw new BadRequestException("User Already Exist!");
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
        return InvestmentConstants.MESSAGE_201;
    }

    @Override
    public User login(LoginRequest request) {

        if (request.getPhone() == null || request.getPhone().trim().isEmpty()) {
            throw new BadRequestException("Phone Number Required");
        }
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new BadRequestException("Password is Required");
        }
        User user = userRepository.findByPhone(request.getPhone().trim())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Account", "phoneNumber",request.getPhone())
                );
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadRequestException(InvestmentConstants.INVALID_LOGIN);
        }
        return user;
    }

    @Override
    public String forgotPassword(LoginRequest request) {

        if (request.getPhone() == null || request.getPhone().trim().isEmpty()) {
            throw new BadRequestException("Phone Number Required!");
        }

        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new BadRequestException("New Password Required!");
        }

        User user = userRepository.findByPhone(request.getPhone().trim())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Account",
                                "phoneNumber",
                                request.getPhone()
                        )
                );

        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);

        return "Password updated successfully.";
    }
}