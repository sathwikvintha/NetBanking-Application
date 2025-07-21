package com.netbanking.netbanking_app.service;

import com.netbanking.netbanking_app.model.Account;
import com.netbanking.netbanking_app.model.Transaction;

import java.math.BigDecimal;
import java.util.List;

public interface CustomerService {
    List<Account> getAccountsByUserId(Long userId);
    List<Transaction> getTransactionsByAccountId(Long accountId);
    void transferFunds(Long fromAccountId, Long toAccountId, BigDecimal amount);
}
