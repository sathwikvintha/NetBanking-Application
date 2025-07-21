package com.netbanking.netbanking_app.repository;

import com.netbanking.netbanking_app.model.LoanApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LoanApplicationRepository extends JpaRepository<LoanApplication, Long> {
    List<LoanApplication> findByUserId(Long userId);
    List<LoanApplication> findByStatus(String status); // For manager
}
