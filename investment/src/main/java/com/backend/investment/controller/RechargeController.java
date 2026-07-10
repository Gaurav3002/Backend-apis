package com.backend.investment.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.backend.investment.dto.RechargeRequestDto;
import com.backend.investment.dto.RechargeResponseDto;
import com.backend.investment.service.IRechargeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/recharge")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RechargeController {

    private final IRechargeService rechargeService;

    @PostMapping
    public ResponseEntity<RechargeResponseDto> rechargeWallet(
            @RequestBody RechargeRequestDto request) {

        return new ResponseEntity<>(
                rechargeService.rechargeWallet(request),
                HttpStatus.CREATED);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<RechargeResponseDto>> history(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                rechargeService.rechargeHistory(userId));
    }

}