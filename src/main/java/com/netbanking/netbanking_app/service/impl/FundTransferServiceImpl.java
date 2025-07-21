package com.netbanking.netbanking_app.service.impl;

import com.netbanking.netbanking_app.dto.FundTransferRequest;
import com.netbanking.netbanking_app.model.Account;
import com.netbanking.netbanking_app.model.Transaction;
import com.netbanking.netbanking_app.model.User;
import com.netbanking.netbanking_app.repository.AccountRepository;
import com.netbanking.netbanking_app.repository.TransactionRepository;
import com.netbanking.netbanking_app.service.FundTransferService;
import com.netbanking.netbanking_app.service.NotificationService;
import com.netbanking.netbanking_app.service.AuditLogService;
import com.netbanking.netbanking_app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class FundTransferServiceImpl implements FundTransferService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private AuditLogService auditLogService;

    @Autowired
    private UserService userService;

    @Override
    public Transaction transfer(FundTransferRequest request) {
        BigDecimal amount = BigDecimal.valueOf(request.getAmount());
        LocalDateTime now = LocalDateTime.now();

        // ✅ Fetch sender account
        Account fromAccount = accountRepository.findById(request.getFromAccountId())
                .orElseThrow(() -> new RuntimeException("From account not found"));

        // ✅ Fetch recipient account
        Account toAccount = accountRepository.findByAccountNumber(request.getToAccountNumber())
                .orElseThrow(() -> new RuntimeException("To account number not found"));

        // ✅ Update balances
        BigDecimal fromBefore = fromAccount.getBalance();
        BigDecimal toBefore = toAccount.getBalance();

        fromAccount.setBalance(fromBefore.subtract(amount));
        toAccount.setBalance(toBefore.add(amount));

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        // ✅ Record DEBIT transaction
        Transaction debit = new Transaction();
        debit.setAccountId(fromAccount.getAccountId());
        debit.setTransactionType("DEBIT");
        debit.setAmount(amount);
        debit.setDescription("Fund transfer to Account " + toAccount.getAccountNumber());
        debit.setTransactionDate(now);
        debit.setBalanceBefore(fromBefore);
        debit.setBalanceAfter(fromAccount.getBalance());
        debit.setStatus("COMPLETED");
        transactionRepository.save(debit);

        // ✅ Record CREDIT transaction
        Transaction credit = new Transaction();
        credit.setAccountId(toAccount.getAccountId());
        credit.setTransactionType("CREDIT");
        credit.setAmount(amount);
        credit.setDescription("Fund transfer from Account " + fromAccount.getAccountNumber());
        credit.setTransactionDate(now);
        credit.setBalanceBefore(toBefore);
        credit.setBalanceAfter(toAccount.getBalance());
        credit.setStatus("COMPLETED");
        transactionRepository.save(credit);

        // ✅ Trigger Notifications (with debug logging)
        try {
            notificationService.triggerSqlNotification(
                    fromAccount.getUserId(),
                    "₹" + amount + " debited from Account " + fromAccount.getAccountNumber() + " to Account " + toAccount.getAccountNumber()
            );

            notificationService.triggerSqlNotification(
                    toAccount.getUserId(),
                    "₹" + amount + " credited to Account " + toAccount.getAccountNumber() + " from Account " + fromAccount.getAccountNumber()
            );

        } catch (Exception e) {
            System.out.println("🔴 Notification error: " + e.getMessage());
        }

        // ✅ Audit logging after successful transfer
        try {
            User user = userService.getUserById(fromAccount.getUserId());

            auditLogService.logAction(
                    "FUND_TRANSFER",
                    user.getUsername(),
                    user.getUserId(),
                    toAccount.getAccountNumber(),
                    "₹" + amount + " transferred from Acc#" + fromAccount.getAccountNumber() +
                            " to Acc#" + toAccount.getAccountNumber(),
                    false,
                    null
            );

            System.out.println("✅ Audit log recorded for fund transfer");
        } catch (Exception e) {
            System.out.println("❌ Audit log error: " + e.getMessage());
        }

        return debit;
    }
}
