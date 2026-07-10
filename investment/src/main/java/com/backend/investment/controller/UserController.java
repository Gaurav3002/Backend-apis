package com.backend.investment.controller;

import com.backend.investment.dto.UserResponseDto;
import com.backend.investment.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {

    private final IUserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {

        UserResponseDto user = userService.getUserById(id);

        return ResponseEntity.ok(user);
    }
}