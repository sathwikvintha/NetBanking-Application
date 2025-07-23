package com.netbanking.netbanking_app.service.impl;

import com.netbanking.netbanking_app.dto.BillPaymentRequest;
import com.netbanking.netbanking_app.model.Account;
import com.netbanking.netbanking_app.model.Bill;
import com.netbanking.netbanking_app.model.Payment;
import com.netbanking.netbanking_app.model.Transaction;
import com.netbanking.netbanking_app.model.User;
import com.netbanking.netbanking_app.repository.AccountRepository;
import com.netbanking.netbanking_app.repository.BillRepository;
import com.netbanking.netbanking_app.repository.PaymentRepository;
import com.netbanking.netbanking_app.repository.TransactionRepository;
import com.netbanking.netbanking_app.service.BillPaymentService;
import com.netbanking.netbanking_app.service.NotificationService;
import com.netbanking.netbanking_app.service.AuditLogService;
import com.netbanking.netbanking_app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class BillPaymentServiceImpl implements BillPaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private AuditLogService auditLogService;

    @Autowired
    private UserService userService;



    @Override
    public Payment payBill(BillPaymentRequest request) {
        // Step 1: Fetch Bill
        Bill bill = billRepository.findById(request.getBillId())
                .orElseThrow(() -> new RuntimeException("Bill not found for ID: " + request.getBillId()));

        // Step 2: Fetch Account
        Account account = accountRepository.findById(request.getAccountId())
                .orElseThrow(() -> new RuntimeException("Account not found for ID: " + request.getAccountId()));

        BigDecimal amount = request.getAmount();
        BigDecimal currentBalance = account.getBalance();

        if (currentBalance.compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient funds for bill payment.");
        }

        // Step 3: Update Bill
        bill.setStatus("PAID");
        bill.setPaymentDate(LocalDateTime.now());
        bill.setUpdatedAt(LocalDateTime.now());
        billRepository.save(bill);

        // Step 4: Create Payment
        Payment payment = new Payment();
        payment.setBillId(bill.getBillId());
        payment.setAccountId(account.getAccountId());
        payment.setAmount(amount);
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setStatus("COMPLETED");
        payment.setTransactionFee(BigDecimal.ZERO);
        payment.setConfirmationNumber("CONF" + System.currentTimeMillis());
        payment.setReferenceNumber(UUID.randomUUID().toString());
        payment.setPaymentDate(LocalDateTime.now());
        payment.setCreatedAt(LocalDateTime.now());
        payment.setUpdatedAt(LocalDateTime.now());

        // Step 5: Create Transaction
        Transaction txn = new Transaction();
        txn.setAccountId(account.getAccountId());
        txn.setTransactionType("DEBIT");
        txn.setAmount(amount);
        txn.setDescription("Bill Payment to " + bill.getBillerName());
        txn.setTransactionDate(payment.getPaymentDate());
        txn.setBalanceBefore(currentBalance);
        txn.setBalanceAfter(currentBalance.subtract(amount));
        txn.setReferenceNumber(payment.getReferenceNumber());
        txn.setChannel(request.getPaymentMethod());
        txn.setFee(payment.getTransactionFee());
        txn.setStatus("COMPLETED");
        txn.setCreatedAt(LocalDateTime.now());

        // Step 6: Update Account
        account.setBalance(txn.getBalanceAfter());
        account.setUpdatedAt(LocalDateTime.now());
        accountRepository.save(account);

        // Step 7: Save Transaction and Payment
        transactionRepository.save(txn);
        Payment savedPayment = paymentRepository.save(payment);

        // Step 8: Send Notification
        notificationService.triggerSqlNotification(
                bill.getUserId(),
                "₹" + bill.getAmount() + " paid to " + bill.getBillerName() + " from Account " + payment.getAccountId()
        );

        // ✅ Step 9: Log in audit trail
        try {
            User user = userService.getUserById(bill.getUserId());

            auditLogService.logAction(
                    "BILL_PAYMENT",
                    user.getUsername(),
                    user.getUserId(),
                    user.getUsername(),
                    "₹" + bill.getAmount() + " paid to " + bill.getBillerName() +
                            " from Acc#" + account.getAccountNumber(),
                    false,
                    null
            );

            System.out.println("✅ Audit log saved for bill payment");
        } catch (Exception e) {
            System.out.println("❌ Audit log error: " + e.getMessage());
        }

        return savedPayment;
    }

    @Override
    public List<Bill> getBillsByUserId(Long userId) {
        return billRepository.findByUserId(userId);
    }

}
