package com.netbanking.netbanking_app.service.impl;

import com.netbanking.netbanking_app.dto.LoanRepaymentSummary;
import com.netbanking.netbanking_app.model.Loan;
import com.netbanking.netbanking_app.model.LoanRepaymentTransaction;
import com.netbanking.netbanking_app.model.User;
import com.netbanking.netbanking_app.repository.LoanRepository;
import com.netbanking.netbanking_app.repository.LoanRepaymentTransactionRepository;
import com.netbanking.netbanking_app.service.AuditLogService;
import com.netbanking.netbanking_app.service.LoanRepaymentService;
import com.netbanking.netbanking_app.service.NotificationService;
import com.netbanking.netbanking_app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class LoanRepaymentServiceImpl implements LoanRepaymentService {

    @Autowired
    private LoanRepaymentTransactionRepository txnRepo;

    @Autowired
    private LoanRepository loanRepo;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserService userService;

    @Autowired
    private AuditLogService auditLogService;

    @Override
    public LoanRepaymentSummary getRepaymentSummary(Long loanId, Long userId) {
        List<LoanRepaymentTransaction> transactions = txnRepo.findByLoanIdAndUserId(loanId, userId);
        int paidEmis = transactions.size();

        Loan loan = loanRepo.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan not found with ID: " + loanId));
        int totalEmis = loan.getLoanDuration() * 12;

        BigDecimal totalPaid = transactions.stream()
                .map(LoanRepaymentTransaction::getAmountPaid)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal remainingPrincipal = loan.getLoanAmount().subtract(totalPaid);
        BigDecimal estimatedInterest = loan.getLoanAmount().multiply(BigDecimal.valueOf(0.07));

        LoanRepaymentSummary summary = new LoanRepaymentSummary();
        summary.setLoanId(loanId);
        summary.setEmisPaid(paidEmis);
        summary.setTotalEmis(totalEmis);
        summary.setRemainingPrincipal(remainingPrincipal);
        summary.setEstimatedInterest(estimatedInterest);
        summary.setTransactions(transactions);
        summary.setLoanStatus(loan.getStatus()); // ✅ Added

        return summary;
    }

    @Override
    public List<LoanRepaymentSummary> getAllRepaymentSummaries(Long userId) {
        List<Loan> approvedLoans = loanRepo.findByUserIdAndStatus(userId, "APPROVED");

        List<LoanRepaymentSummary> summaries = new ArrayList<>();
        for (Loan loan : approvedLoans) {
            LoanRepaymentSummary summary = getRepaymentSummary(loan.getLoanId(), userId);
            summaries.add(summary);
        }

        return summaries;
    }

    @Override
    public String payEmi(Long loanId, Long userId, int emiNumber, String paymentMode) {
        Loan loan = loanRepo.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan not found with ID: " + loanId));

        if (!"APPROVED".equalsIgnoreCase(loan.getStatus())) {
            throw new RuntimeException("EMI payment not allowed — loan is not yet approved by admin.");
        }

        int totalEmis = loan.getLoanDuration() * 12;
        BigDecimal monthlyAmount = loan.getLoanAmount()
                .divide(BigDecimal.valueOf(totalEmis), 2, RoundingMode.HALF_UP);

        LocalDate nextDueDate = LocalDate.now().plusMonths(1);
        int remainingEmis = totalEmis - emiNumber;
        String timestamp = "at " + LocalDateTime.now().withNano(0);

        LoanRepaymentTransaction txn = new LoanRepaymentTransaction();
        txn.setLoanId(loanId);
        txn.setUserId(userId);
        txn.setEmiNumber(emiNumber);
        txn.setTotalEmis(totalEmis);
        txn.setAmountPaid(monthlyAmount);
        txn.setPaymentDate(LocalDate.now());
        txn.setPaymentMode(paymentMode);
        txn.setStatus("COMPLETED");
        txn.setCreatedAt(LocalDateTime.now());
        txn.setUpdatedAt(LocalDateTime.now());

        txnRepo.save(txn);

        String message = "₹" + monthlyAmount + " EMI paid successfully for Loan ID " + loanId +
                " (EMI #" + emiNumber + ", Mode: " + paymentMode + ") " + timestamp + "\n" +
                "Remaining EMIs: " + remainingEmis + "\n" +
                "Next due date: " + nextDueDate;

        notificationService.triggerSqlNotification(userId, message);

        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findById(userId);
        String targetUsername = user != null ? user.getUsername() : "—";

        auditLogService.logAction(
                "EMI_PAID",
                currentUsername,
                userId,
                targetUsername,
                "EMI #" + emiNumber + " paid for Loan ID " + loanId + " using " + paymentMode,
                true,
                message
        );

        return "✅ EMI #" + emiNumber + " paid successfully";
    }
}
