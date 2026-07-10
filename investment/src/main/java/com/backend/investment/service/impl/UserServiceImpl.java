package com.backend.investment.service.impl;

import com.backend.investment.Exception.ResourceNotFoundException;
import com.backend.investment.dto.UserResponseDto;
import com.backend.investment.entity.User;
import com.backend.investment.repository.UserRepository;
import com.backend.investment.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.backend.investment.mapper.UserMapper;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;

    @Override
public UserResponseDto getUserById(Long id) {

    User user = userRepository.findById(id)
            .orElseThrow(() ->
                    new ResourceNotFoundException("User", "userId", id.toString()));

    UserResponseDto dto = UserMapper.userToUserResponseDto(user);

    return dto;
}
}