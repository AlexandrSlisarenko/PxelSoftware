package ru.slisarenko.pxelsoftware.db.dao;

import ru.slisarenko.pxelsoftware.db.entity.Account;
import ru.slisarenko.pxelsoftware.exception.AccountException;

import java.util.List;

public interface AccountDAO {

    List<Account> getAccounts() ;

    Account getAccount(Long id) throws AccountException;

    void saveAccount(Account account) throws AccountException;
}
