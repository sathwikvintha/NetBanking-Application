package com.netbanking.netbanking_app.service.impl;

import com.netbanking.netbanking_app.model.Loan;
import com.netbanking.netbanking_app.repository.LoanRepository;
import com.netbanking.netbanking_app.service.ManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ManagerServiceImpl implements ManagerService {

    @Autowired
    private LoanRepository loanRepository;

    @Override
    public List<Loan> getPendingLoans() {
        return loanRepository.findByStatus("PENDING");
    }

    @Override
    public Loan approveLoan(Long loanId) {
        Loan loan = loanRepository.findById(loanId).orElseThrow(() -> new RuntimeException("Loan not found"));
        loan.setStatus("APPROVED");
        return loanRepository.save(loan);
    }

    @Override
    public List<Loan> getAllLoans() {
        return loanRepository.findAll();
    }
}
