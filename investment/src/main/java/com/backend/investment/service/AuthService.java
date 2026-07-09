package com.backend.investment.service;

import com.backend.investment.dto.LoginRequest;
import com.backend.investment.dto.RegisterRequest;
import com.backend.investment.entity.User;

public interface AuthService {

    String register(RegisterRequest request);
    User login(LoginRequest request);

}
