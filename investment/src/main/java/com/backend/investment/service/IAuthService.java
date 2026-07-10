package com.backend.investment.service;

import com.backend.investment.dto.LoginRequest;
import com.backend.investment.dto.RegisterRequest;
import com.backend.investment.dto.UserResponseDto;

public interface IAuthService {

    String register(RegisterRequest request);
    UserResponseDto login(LoginRequest request);
    String forgotPassword(LoginRequest request);

}
