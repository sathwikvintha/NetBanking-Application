package com.netbanking.netbanking_app.service;

import com.netbanking.netbanking_app.model.Account;
import java.math.BigDecimal;
import java.util.List;

public interface AccountService {
    List<Account> getAccountsByUserId(Long userId);
    Account getAccountById(Long accountId);
    BigDecimal getBalance(Long accountId);
    void updateBalance(Long accountId, BigDecimal newBalance);
    Account createAccount(Account account);
    void deleteAccount(Long accountId);
    Account saveAccount(Account account);
    List<Account> getAllAccounts();

}