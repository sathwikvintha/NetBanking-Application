package com.netbanking.netbanking_app.repository;

import com.netbanking.netbanking_app.model.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CardRepository extends JpaRepository<Card, Long> {
    List<Card> findByUserId(Long userId);
    List<Card> findByStatus(String status);
    List<Card> findByStatusIgnoreCase(String status);
}
