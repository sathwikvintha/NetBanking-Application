package com.netbanking.netbanking_app.service.impl;

import com.netbanking.netbanking_app.model.LoanApplication;
import com.netbanking.netbanking_app.model.User;
import com.netbanking.netbanking_app.repository.AccountRepository;
import com.netbanking.netbanking_app.repository.LoanApplicationRepository;
import com.netbanking.netbanking_app.service.LoanApplicationService;
import com.netbanking.netbanking_app.service.NotificationService;
import com.netbanking.netbanking_app.service.AuditLogService;
import com.netbanking.netbanking_app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class LoanApplicationServiceImpl implements LoanApplicationService {

    @Autowired
    private LoanApplicationRepository loanRepo;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private AuditLogService auditLogService;

    @Autowired
    private UserService userService;

    private BigDecimal interestRate;

    public BigDecimal getInterestRate() { return interestRate; }
    public void setInterestRate(BigDecimal interestRate) { this.interestRate = interestRate; }

    @Override
    public LoanApplication applyLoan(LoanApplication loanApplication) {
        notificationService.triggerSqlNotification(
                loanApplication.getUserId(),
                "Loan request for ₹" + loanApplication.getLoanAmount() + " submitted (Type: " + loanApplication.getLoanType() + ")"
        );

        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusYears(loanApplication.getLoanDuration());
        LocalDate nextDueDate = startDate.plusMonths(1);
        loanApplication.setNextDueDate(nextDueDate);

        BigDecimal principal = BigDecimal.valueOf(loanApplication.getLoanAmount());
        BigDecimal annualRate = BigDecimal.valueOf(loanApplication.getInterestRate());
        BigDecimal monthlyRate = annualRate.divide(BigDecimal.valueOf(12 * 100), 6, RoundingMode.HALF_UP);

        int totalMonths = loanApplication.getLoanDuration() * 12;
        BigDecimal numerator = principal.multiply(monthlyRate).multiply(
                BigDecimal.valueOf(Math.pow(1 + monthlyRate.doubleValue(), totalMonths))
        );
        BigDecimal denominator = BigDecimal.valueOf(
                Math.pow(1 + monthlyRate.doubleValue(), totalMonths) - 1
        );
        BigDecimal emi = numerator.divide(denominator, 2, RoundingMode.HALF_UP);

        loanApplication.setStartDate(startDate);
        loanApplication.setEndDate(endDate);
        loanApplication.setMonthlyEmi(emi);
        loanApplication.setOutstandingAmount(principal);
        loanApplication.setStatus("ACTIVE");
        loanApplication.setCreatedAt(LocalDateTime.now());
        loanApplication.setUpdatedAt(LocalDateTime.now());

        LoanApplication savedLoan = loanRepo.save(loanApplication);

        try {
            User user = userService.getUserById(savedLoan.getUserId());

            auditLogService.logAction(
                    "LOAN_APPLIED",
                    user.getUsername(),
                    user.getUserId(),
                    user.getUsername(),
                    "Loan application submitted for ₹" + savedLoan.getLoanAmount() + " (Type: " + savedLoan.getLoanType() + ")",
                    false,
                    null
            );
        } catch (Exception e) {
            System.out.println("❌ Audit log error (LOAN_APPLIED): " + e.getMessage());
        }

        return savedLoan;
    }

    @Override
    public List<LoanApplication> getLoansByUserId(Long userId) {
        List<LoanApplication> loans = loanRepo.findByUserId(userId);

        auditLogService.logAction(
                "LOAN_FETCH_USER",
                "system",
                userId,
                "user-" + userId,
                "Fetched " + loans.size() + " loan applications for user ID: " + userId,
                false,
                null
        );

        return loans;
    }

    @Override
    public List<LoanApplication> getPendingLoans() {
        List<LoanApplication> pendingLoans = loanRepo.findByStatus("PENDING");

        auditLogService.logAction(
                "LOAN_FETCH_PENDING",
                "admin",
                null,
                "all-pending-loans",
                "Fetched " + pendingLoans.size() + " pending loan applications",
                false,
                null
        );

        return pendingLoans;
    }

    @Override
    public void updateLoanStatus(Long loanId, String status) {
        LoanApplication loan = loanRepo.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan not found"));

        loan.setStatus(status);
        loan.setUpdatedAt(LocalDateTime.now());
        loanRepo.save(loan);

        try {
            User user = userService.getUserById(loan.getUserId());

            auditLogService.logAction(
                    "LOAN_STATUS_UPDATE",
                    "admin",
                    user.getUserId(),
                    user.getUsername(),
                    "Loan status updated to " + status + " for loan ID: " + loanId,
                    false,
                    null
            );
        } catch (Exception e) {
            System.out.println("❌ Audit log error (LOAN_STATUS_UPDATE): " + e.getMessage());
        }
    }
}
