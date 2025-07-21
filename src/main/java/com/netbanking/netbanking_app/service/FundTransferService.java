package com.netbanking.netbanking_app.service;

import com.netbanking.netbanking_app.dto.FundTransferRequest;
import com.netbanking.netbanking_app.model.Transaction;

public interface FundTransferService {
    Transaction transfer(FundTransferRequest request);
}
