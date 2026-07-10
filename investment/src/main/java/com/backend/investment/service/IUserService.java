package com.backend.investment.service;

import com.backend.investment.dto.UserResponseDto;

public interface IUserService {

    UserResponseDto getUserById(Long id);

}