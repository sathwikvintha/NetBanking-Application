package com.netbanking.netbanking_app.service.impl;

import com.netbanking.netbanking_app.model.Account;
import com.netbanking.netbanking_app.model.Transaction;
import com.netbanking.netbanking_app.repository.AccountRepository;
import com.netbanking.netbanking_app.repository.TransactionRepository;
import com.netbanking.netbanking_app.service.CustomerService;
import com.netbanking.netbanking_app.service.AuditLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AuditLogService auditLogService;

    @Override
    public List<Account> getAccountsByUserId(Long userId) {
        List<Account> accounts = accountRepository.findByUserId(userId);

        auditLogService.logAction(
                "CUSTOMER_ACCOUNT_FETCH",
                "system",
                userId,
                "user-" + userId,
                "Fetched " + accounts.size() + " accounts for user ID " + userId,
                false,
                null
        );

        return accounts;
    }

    @Override
    public List<Transaction> getTransactionsByAccountId(Long accountId) {
        List<Transaction> txns = transactionRepository.findByAccountId(accountId);

        auditLogService.logAction(
                "CUSTOMER_TXN_FETCH",
                "system",
                null,
                "account-" + accountId,
                "Fetched " + txns.size() + " transactions for account ID " + accountId,
                false,
                null
        );

        return txns;
    }

    @Override
    public void transferFunds(Long fromAccountId, Long toAccountId, BigDecimal amount) {
        Account from = accountRepository.findById(fromAccountId)
                .orElseThrow(() -> new IllegalArgumentException("From Account not found"));

        Account to = accountRepository.findById(toAccountId)
                .orElseThrow(() -> new IllegalArgumentException("To Account not found"));

        if (from.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient balance in source account");
        }

        from.setBalance(from.getBalance().subtract(amount));
        to.setBalance(to.getBalance().add(amount));

        accountRepository.save(from);
        accountRepository.save(to);

        Transaction debit = new Transaction();
        debit.setAccountId(fromAccountId);
        debit.setAmount(amount);
        debit.setTransactionType("DEBIT");
        debit.setTransactionDate(LocalDateTime.now());
        transactionRepository.save(debit);

        Transaction credit = new Transaction();
        credit.setAccountId(toAccountId);
        credit.setAmount(amount);
        credit.setTransactionType("CREDIT");
        credit.setTransactionDate(LocalDateTime.now());
        transactionRepository.save(credit);

        auditLogService.logAction(
                "CUSTOMER_FUNDS_TRANSFER",
                "system",
                from.getUserId(),
                "user-" + from.getUserId(),
                "₹" + amount + " transferred from Acc#" + from.getAccountNumber() +
                        " to Acc#" + to.getAccountNumber(),
                false,
                null
        );
    }
}
