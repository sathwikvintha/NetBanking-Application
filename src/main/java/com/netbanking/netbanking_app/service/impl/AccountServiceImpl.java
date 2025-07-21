package com.netbanking.netbanking_app.service.impl;

import com.netbanking.netbanking_app.model.Account;
import com.netbanking.netbanking_app.model.User;
import com.netbanking.netbanking_app.repository.AccountRepository;
import com.netbanking.netbanking_app.service.AccountService;
import com.netbanking.netbanking_app.service.NotificationService;
import com.netbanking.netbanking_app.service.AuditLogService;
import com.netbanking.netbanking_app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private AuditLogService auditLogService;

    @Autowired
    private UserService userService;

    @Override
    public List<Account> getAccountsByUserId(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }

        List<Account> accounts = accountRepository.findByUserId(userId);

        auditLogService.logAction(
                "ACCOUNTS_FETCHED",
                "system", // or use principal.getName() if available
                userId,
                "user-" + userId,
                "Fetched " + accounts.size() + " accounts for user ID: " + userId,
                false,
                null
        );

        return accounts;
    }

    @Override
    public Account getAccountById(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found with ID: " + accountId));

        auditLogService.logAction(
                "ACCOUNT_FETCHED",
                "system",
                account.getUserId(),
                "user-" + account.getUserId(),
                "Accessed account details for ID: " + accountId,
                false,
                null
        );

        return account;
    }

    @Override
    public BigDecimal getBalance(Long accountId) {
        return getAccountById(accountId).getBalance(); // already logs account fetch
    }

    @Override
    public void updateBalance(Long accountId, BigDecimal newBalance) {
        Account account = getAccountById(accountId);
        account.setBalance(newBalance);
        accountRepository.save(account);

        auditLogService.logAction(
                "BALANCE_UPDATED",
                "system",
                account.getUserId(),
                "user-" + account.getUserId(),
                "Balance updated for Acc#" + account.getAccountNumber() + " to ₹" + newBalance,
                false,
                null
        );
    }

    @Override
    public Account createAccount(Account account) {
        notificationService.triggerSqlNotification(
                account.getUserId(),
                "New " + account.getAccountType() + " account created: " + account.getAccountNumber()
        );

        Account saved = accountRepository.save(account);

        User user = userService.getUserById(saved.getUserId());

        auditLogService.logAction(
                "ACCOUNT_CREATED",
                user.getUsername(),
                user.getUserId(),
                user.getUsername(),
                "New " + saved.getAccountType() + " account created: " + saved.getAccountNumber(),
                false,
                null
        );

        return saved;
    }

    @Override
    public void deleteAccount(Long accountId) {
        Account account = getAccountById(accountId);
        accountRepository.deleteById(accountId);

        User user = userService.getUserById(account.getUserId());

        auditLogService.logAction(
                "ACCOUNT_DELETED",
                user.getUsername(),
                user.getUserId(),
                user.getUsername(),
                "Deleted account with ID: " + accountId,
                false,
                null
        );
    }

    @Override
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }


    @Override
    public Account saveAccount(Account account) {
        return accountRepository.save(account); // optional to log here
    }
}
