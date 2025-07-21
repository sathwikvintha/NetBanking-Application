package com.netbanking.netbanking_app.repository;

import com.netbanking.netbanking_app.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    // JPQL query to get all accounts for a user
    @Query("SELECT a FROM Account a WHERE a.userId = :userId ORDER BY a.accountId")
    List<Account> findByUserId(@Param("userId") Long userId);

    Optional<Account> findByAccountNumber(String accountNumber);

    @Query("SELECT COUNT(a) FROM Account a WHERE a.userId = :userId")
    Long countByUserId(@Param("userId") Long userId);
}
