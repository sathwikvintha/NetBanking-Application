package com.netbanking.netbanking_app.service;

import com.netbanking.netbanking_app.model.LoanApplication;
import java.util.List;

public interface LoanApplicationService {
    LoanApplication applyLoan(LoanApplication loanApplication);
    List<LoanApplication> getLoansByUserId(Long userId);
    List<LoanApplication> getPendingLoans();
    void updateLoanStatus(Long loanId, String status); // approve/reject
}
