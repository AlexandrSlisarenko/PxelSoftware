package ru.slisarenko.pxelsoftware.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.slisarenko.pxelsoftware.db.dao.AccountDepositDAO;
import ru.slisarenko.pxelsoftware.db.entity.Account;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
@Builder
@Getter
public class DepositService {

    private final AccountDepositDAO accountDAO;

    @Builder.Default
    private List<Account> accountsWithMaxDeposit = new ArrayList<>();
    @Builder.Default
    private List<Account> accountsDeposit = new ArrayList<>();

    public void updateStateDeposits(Integer maxPercent){
        accountsDeposit.addAll(accountDAO.getAccounts());
        accountsDeposit.stream().filter( account -> {
            var maxDeposit = (account.getStartBalance().intValue() * maxPercent) / 100;
            return account.getBalance().intValue() > maxDeposit;
        }).forEach(accountsWithMaxDeposit::add);
    }

    public void clearStateDeposits(){
        accountsDeposit.clear();
        accountsWithMaxDeposit.clear();
    }

    public void addPercent(){
        accountsDeposit.forEach(account -> {
            var accountFromDB = accountDAO.getAccount(account.getId());
            var balance = accountFromDB.getBalance().longValue();
            accountFromDB.setBalance(BigDecimal.valueOf(balance + ((balance * accountFromDB.getInterestRate()) / 100)));
            accountDAO.saveAccount(accountFromDB);
        });
    }
}
