package com.netbanking.netbanking_app.service;

import com.netbanking.netbanking_app.model.Transaction;
import java.util.List;

public interface TransactionService {
    List<Transaction> getTransactions(Long accountId);
    void saveTransaction(Transaction txn); // ✅ NEW
}
