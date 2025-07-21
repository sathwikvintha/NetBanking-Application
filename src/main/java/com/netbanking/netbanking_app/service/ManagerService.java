package com.netbanking.netbanking_app.service;

import com.netbanking.netbanking_app.model.Loan;

import java.util.List;

public interface ManagerService {
    List<Loan> getPendingLoans();
    Loan approveLoan(Long loanId);
    List<Loan> getAllLoans(); // For reporting
}
