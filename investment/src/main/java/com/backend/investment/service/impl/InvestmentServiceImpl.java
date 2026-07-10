package com.backend.investment.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.backend.investment.Exception.BadRequestException;
import com.backend.investment.Exception.ResourceNotFoundException;
import com.backend.investment.dto.InvestmentRequestDto;
import com.backend.investment.dto.InvestmentResponseDto;
import com.backend.investment.entity.Product;
import com.backend.investment.entity.User;
import com.backend.investment.entity.UserInvestment;
import com.backend.investment.entity.WalletTransaction;
import com.backend.investment.repository.ProductRepository;
import com.backend.investment.repository.UserInvestmentRepository;
import com.backend.investment.repository.UserRepository;
import com.backend.investment.repository.WalletTransactionRepository;
import com.backend.investment.service.InvestmentService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InvestmentServiceImpl implements InvestmentService {

    private final UserRepository userRepository;

    private final ProductRepository productRepository;

    private final UserInvestmentRepository investmentRepository;

    private final WalletTransactionRepository walletRepository;

    @Override
    @Transactional
    public InvestmentResponseDto invest(InvestmentRequestDto request) {

        User user=userRepository.findById(request.getUserId())
                .orElseThrow(() ->
                new ResourceNotFoundException("Investment", "UserId", request.getUserId().toString())
            );

        Product product=productRepository.findById(request.getProductId())
                .orElseThrow(() ->
                new ResourceNotFoundException("Investment", "ProductId", request.getProductId().toString())
            );

        if(user.getBalance().compareTo(product.getInvestmentAmount())<0){

            throw new BadRequestException("Insufficient wallet balance.");

        }

        BigDecimal opening=user.getBalance();

        BigDecimal closing=opening.subtract(product.getInvestmentAmount());

        user.setBalance(closing);

        userRepository.save(user);

        UserInvestment investment=new UserInvestment();

        investment.setUser(user);

        investment.setProduct(product);

        investment.setInvestmentAmount(product.getInvestmentAmount());

        investment.setDailyIncome(product.getDailyIncome());

        investment.setDurationDays(product.getDurationDays());

        investment.setStartDate(LocalDate.now());

        investment.setEndDate(
                LocalDate.now().plusDays(product.getDurationDays())
        );

        investment.setStatus("ACTIVE");

        investment=investmentRepository.save(investment);

        WalletTransaction wallet=new WalletTransaction();

        wallet.setUser(user);

        wallet.setTransactionType("INVESTMENT");

        wallet.setAmount(product.getInvestmentAmount());

        wallet.setOpeningBalance(opening);

        wallet.setClosingBalance(closing);

        wallet.setReferenceId(investment.getId());

        wallet.setReferenceType("INVESTMENT");

        wallet.setRemarks("Investment Purchase");

        walletRepository.save(wallet);

        InvestmentResponseDto dto=new InvestmentResponseDto();

        dto.setInvestmentId(investment.getId());

        dto.setUserId(user.getId());

        dto.setProductId(product.getId());

        dto.setProductName(product.getProductName());

        dto.setInvestmentAmount(product.getInvestmentAmount());

        dto.setDailyIncome(product.getDailyIncome());

        dto.setDurationDays(product.getDurationDays());

        dto.setStartDate(investment.getStartDate());

        dto.setEndDate(investment.getEndDate());

        dto.setStatus(investment.getStatus());

        return dto;

    }

    @Override
    public List<InvestmentResponseDto> myInvestments(Long userId) {

        return investmentRepository.findByUserId(userId)
                .stream()
                .map(i->{

                    InvestmentResponseDto dto=new InvestmentResponseDto();

                    dto.setInvestmentId(i.getId());

                    dto.setUserId(i.getUser().getId());

                    dto.setProductId(i.getProduct().getId());

                    dto.setProductName(i.getProduct().getProductName());

                    dto.setInvestmentAmount(i.getInvestmentAmount());

                    dto.setDailyIncome(i.getDailyIncome());

                    dto.setDurationDays(i.getDurationDays());

                    dto.setStartDate(i.getStartDate());

                    dto.setEndDate(i.getEndDate());

                    dto.setStatus(i.getStatus());

                    return dto;

                }).toList();

    }

}
