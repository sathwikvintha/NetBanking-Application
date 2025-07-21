package com.netbanking.netbanking_app.service.impl;

import com.netbanking.netbanking_app.model.Transaction;
import com.netbanking.netbanking_app.repository.TransactionRepository;
import com.netbanking.netbanking_app.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Override
    public List<Transaction> getTransactions(Long accountId) {
        return transactionRepository.findByAccountId(accountId);
    }

    @Override
    public void saveTransaction(Transaction txn) {
        transactionRepository.save(txn);
    }
}
