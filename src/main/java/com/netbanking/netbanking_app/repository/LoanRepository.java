package com.netbanking.netbanking_app.repository;

import com.netbanking.netbanking_app.model.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
    List<Loan> findByStatusIgnoreCase(String status);
    List<Loan> findByUserIdAndStatus(Long userId, String status);
    List<Loan> findByStatus(String pending);
    List<Loan> findByUserIdAndStatusIn(Long userId, List<String> statuses);
    List<Loan> findByUserId(Long userId);
}
