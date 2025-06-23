package ru.slisarenko.pxelsoftware.db.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.slisarenko.pxelsoftware.db.entity.Account;
import ru.slisarenko.pxelsoftware.db.repositary.AccountRepository;
import ru.slisarenko.pxelsoftware.exception.AccountException;

import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class AccountDAOImpl implements AccountDepositDAO {

    private final AccountRepository accountRepository;

    @Override
    public List<Account> getAccounts() {
        return accountRepository.findAll();
    }

    @Override
    public Account getAccount(Long id) {
        return accountRepository.findById(id).orElseThrow(() -> new AccountException("Account is not found"));
    }

    @Override
    public void saveAccount(Account account) throws AccountException {
        accountRepository.saveAndFlush(account);
    }
}
