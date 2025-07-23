package com.netbanking.netbanking_app.service;

import com.netbanking.netbanking_app.model.Loan;
import java.util.List;

public interface LoanService {
    List<Loan> getPendingLoans();        // returns pending loans
    List<Loan> getAllLoans();            // returns all loans
    String approveLoan(Long loanId);     // approves a loan
    List<Loan> getPendingRequests();
    List<Loan> getRequestsByStatus(String status);
    void updateLoanStatus(Long loanId, String newStatus);
    List<Loan> getAllRequests();
    List<Loan> getLoansByUserId(Long userId);

}
