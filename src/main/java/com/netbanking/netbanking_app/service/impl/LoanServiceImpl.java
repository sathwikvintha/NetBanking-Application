package com.netbanking.netbanking_app.service.impl;

import com.netbanking.netbanking_app.model.Loan;
import com.netbanking.netbanking_app.model.User;
import com.netbanking.netbanking_app.repository.LoanRepository;
import com.netbanking.netbanking_app.service.LoanService;
import com.netbanking.netbanking_app.service.AuditLogService;
import com.netbanking.netbanking_app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LoanServiceImpl implements LoanService {

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private LoanRepository loanRepo;

    @Autowired
    private AuditLogService auditLogService;

    @Autowired
    private UserService userService;

    @Override
    public List<Loan> getPendingLoans() {
        List<Loan> pendingLoans = loanRepository.findByStatusIgnoreCase("PENDING");

        auditLogService.logAction(
                "LOAN_PENDING_FETCH",
                "admin",
                null,
                "pending-loans",
                "Fetched " + pendingLoans.size() + " loans with status PENDING",
                false,
                null
        );

        return pendingLoans;
    }

    public List<Loan> getLoansByUserId(Long userId) {
        return loanRepository.findByUserId(userId);
    }


    @Override
    public List<Loan> getPendingRequests() {
        List<Loan> pendingRequests = loanRepo.findByStatusIgnoreCase("PENDING");

        auditLogService.logAction(
                "LOAN_PENDING_FETCH",
                "admin",
                null,
                "pending-loan-requests",
                "Fetched " + pendingRequests.size() + " pending loan requests",
                false,
                null
        );

        return pendingRequests;
    }

    @Override
    public List<Loan> getRequestsByStatus(String status) {
        List<Loan> filteredLoans = loanRepo.findByStatusIgnoreCase(status);

        auditLogService.logAction(
                "LOAN_STATUS_FILTER",
                "admin",
                null,
                "filtered-loans",
                "Fetched " + filteredLoans.size() + " loan requests with status " + status,
                false,
                null
        );

        return filteredLoans;
    }

    @Override
    public List<Loan> getAllRequests() {
        List<Loan> allRequests = loanRepo.findAll();

        auditLogService.logAction(
                "LOAN_REQUESTS_FETCH",
                "admin",
                null,
                "all-loan-requests",
                "Fetched " + allRequests.size() + " total loan requests",
                false,
                null
        );

        return allRequests;
    }

    @Override
    public void updateLoanStatus(Long loanId, String newStatus) {
        Loan loan = loanRepo.findById(loanId).orElse(null);
        if (loan != null) {
            loan.setStatus(newStatus);
            loanRepo.save(loan);

            try {
                User user = userService.getUserById(loan.getUserId());
                auditLogService.logAction(
                        "LOAN_STATUS_UPDATE",
                        "admin",
                        user.getUserId(),
                        user.getUsername(),
                        "Loan ID " + loanId + " updated to status " + newStatus,
                        false,
                        null
                );
            } catch (Exception e) {
                System.out.println("❌ Audit log error (LOAN_STATUS_UPDATE): " + e.getMessage());
            }
        }
    }

    @Override
    public List<Loan> getAllLoans() {
        List<Loan> allLoans = loanRepository.findAll();

        auditLogService.logAction(
                "LOAN_ALL_FETCH",
                "admin",
                null,
                "all-loans",
                "Fetched " + allLoans.size() + " loans",
                false,
                null
        );

        return allLoans;
    }

    @Override
    public String approveLoan(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan not found with ID: " + loanId));

        loan.setStatus("APPROVED");
        loanRepository.save(loan);

        try {
            User user = userService.getUserById(loan.getUserId());
            auditLogService.logAction(
                    "LOAN_APPROVED",
                    "admin",
                    user.getUserId(),
                    user.getUsername(),
                    "Loan ID " + loanId + " approved successfully",
                    false,
                    null
            );
        } catch (Exception e) {
            System.out.println("❌ Audit log error (LOAN_APPROVED): " + e.getMessage());
        }

        return "Loan ID " + loanId + " approved successfully ✅";
    }
}
