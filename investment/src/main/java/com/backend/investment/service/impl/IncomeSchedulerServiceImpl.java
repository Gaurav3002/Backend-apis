package com.backend.investment.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.investment.entity.*;
import com.backend.investment.repository.*;
import com.backend.investment.service.IIncomeSchedulerService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class IncomeSchedulerServiceImpl implements IIncomeSchedulerService {

    private final UserInvestmentRepository investmentRepository;

    private final IncomeHistoryRepository incomeRepository;

    private final UserRepository userRepository;

    private final WalletTransactionRepository walletRepository;

    @Override
    @Transactional
    @Scheduled(cron = "0 1 0 * * *")
    public void generateDailyIncome() {

        List<UserInvestment> investments =
                investmentRepository.findByStatus("ACTIVE");

        LocalDate today = LocalDate.now();

        for(UserInvestment investment : investments){

            if(today.isAfter(investment.getEndDate())){

                investment.setStatus("COMPLETED");

                investmentRepository.save(investment);

                continue;
            }

            if(incomeRepository.existsByInvestmentIdAndIncomeDate(
                    investment.getId(),today)){

                continue;
            }

            User user = investment.getUser();

            BigDecimal income = investment.getDailyIncome();

            BigDecimal opening = user.getBalance();

            BigDecimal closing = opening.add(income);

            user.setBalance(closing);

            user.setTotalIncome(
                    user.getTotalIncome().add(income));

            userRepository.save(user);

            investment.setTotalIncomeGenerated(
                    investment.getTotalIncomeGenerated().add(income));

            investmentRepository.save(investment);

            IncomeHistory history = new IncomeHistory();

            history.setInvestment(investment);

            history.setUser(user);

            history.setIncomeAmount(income);

            history.setIncomeDate(today);

            history.setRemarks("Daily Income Credited");

            history = incomeRepository.save(history);

            WalletTransaction wallet = new WalletTransaction();

            wallet.setUser(user);

            wallet.setTransactionType("DAILY_INCOME");

            wallet.setAmount(income);

            wallet.setOpeningBalance(opening);

            wallet.setClosingBalance(closing);

            wallet.setReferenceId(history.getId());

            wallet.setReferenceType("INCOME");

            wallet.setRemarks("Daily Income");

            walletRepository.save(wallet);

        }

    }

}