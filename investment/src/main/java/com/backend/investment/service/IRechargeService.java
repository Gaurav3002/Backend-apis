package com.backend.investment.service;

import com.backend.investment.dto.RechargeRequestDto;
import com.backend.investment.dto.RechargeResponseDto;

import java.util.List;

public interface IRechargeService {

    RechargeResponseDto rechargeWallet(RechargeRequestDto request);

    List<RechargeResponseDto> rechargeHistory(Long userId);

}