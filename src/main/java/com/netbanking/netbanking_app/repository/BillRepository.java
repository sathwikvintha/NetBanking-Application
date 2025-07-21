package com.netbanking.netbanking_app.repository;

import com.netbanking.netbanking_app.model.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BillRepository extends JpaRepository<Bill, Long> {
    List<Bill> findByUserId(Long userId);

    List<Bill> findByUserIdAndStatus(Long userId, String unpaid);
}
