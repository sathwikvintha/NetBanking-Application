package com.netbanking.netbanking_app.repository;

import com.netbanking.netbanking_app.model.LoanRepaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LoanRepaymentTransactionRepository extends JpaRepository<LoanRepaymentTransaction, Long> {
    List<LoanRepaymentTransaction> findByLoanIdAndUserId(Long loanId, Long userId);
}

