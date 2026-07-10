package com.backend.investment.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.investment.dto.InvestmentRequestDto;
import com.backend.investment.dto.InvestmentResponseDto;
import com.backend.investment.service.InvestmentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/investments")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class InvestmentController {

    private final InvestmentService investmentService;

    @PostMapping("/invest")
    public ResponseEntity<InvestmentResponseDto> invest(
            @RequestBody InvestmentRequestDto request){

        return new ResponseEntity<>(
                investmentService.invest(request),
                HttpStatus.CREATED);

    }

    @GetMapping("investMentDetails/{userId}")
    public ResponseEntity<List<InvestmentResponseDto>> myInvestments(
            @PathVariable Long userId){

        return ResponseEntity.ok(
                investmentService.myInvestments(userId));

    }

}
